package com.bharat.portfolio_backend.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String entity, Object identifier) {
        return new ResourceNotFoundException(entity + " not found with id " + identifier);
    }
}