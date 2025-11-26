package com.example.exception;

public class InvalidDealException extends RuntimeException {
    public InvalidDealException(String message) {
        super(message);
    }
}
