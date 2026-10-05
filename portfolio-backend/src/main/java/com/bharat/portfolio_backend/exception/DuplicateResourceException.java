package com.bharat.portfolio_backend.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public static DuplicateResourceException of(String field, Object value) {
        return new DuplicateResourceException(field + " '" + value + "' is already in use");
    }
}