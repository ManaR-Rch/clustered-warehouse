package com.example.service;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.dto.DealRequestDto;
import com.example.entity.Deal;
import com.example.exception.InvalidDealException;
import com.example.mapper.DealMapper;
import com.example.repository.DealRepository;

@Service
public class DealService {

    private static final Logger log = LoggerFactory.getLogger(DealService.class);

    private final Validator validator;
    private final DealRepository repository;

    public DealService(Validator validator, DealRepository repository) {
        this.validator = validator;
        this.repository = repository;
    }

    public ProcessResult processDeal(DealRequestDto dto) {
        // Validate DTO
        Set<ConstraintViolation<DealRequestDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .collect(Collectors.joining(", "));
            throw new InvalidDealException("Invalid deal: " + msg);
        }

        String uniqueId = dto.getDealUniqueId();
        if (uniqueId != null && repository.existsByDealUniqueId(uniqueId)) {
            log.info("Duplicate deal detected, skipping import for uniqueId={}", uniqueId);
            return ProcessResult.DUPLICATE;
        }

        Deal entity = DealMapper.toEntity(dto);
        try {
            repository.save(entity);
            return ProcessResult.IMPORTED;
        } catch (Exception ex) {
            // Log and do not rollback/propagate so caller can continue processing others
            log.error("Error saving deal uniqueId={}: {}", uniqueId, ex.getMessage(), ex);
            return ProcessResult.ERROR;
        }
    }
}
