package com.example.mapper;

import com.example.dto.DealRequestDto;
import com.example.entity.Deal;

public final class DealMapper {

    private DealMapper() {}

    public static Deal toEntity(DealRequestDto dto) {
        if (dto == null) return null;
        return new Deal(
                dto.getAmount(),
                dto.getCurrency(),
                dto.getCounterparty(),
                dto.getReference()
        );
    }
}
