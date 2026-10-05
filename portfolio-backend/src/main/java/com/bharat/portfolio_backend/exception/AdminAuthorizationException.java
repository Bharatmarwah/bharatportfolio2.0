package com.bharat.portfolio_backend.exception;

import org.springframework.http.HttpStatus;

public class AdminAuthorizationException extends RuntimeException {

    private final HttpStatus status;

    public AdminAuthorizationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}