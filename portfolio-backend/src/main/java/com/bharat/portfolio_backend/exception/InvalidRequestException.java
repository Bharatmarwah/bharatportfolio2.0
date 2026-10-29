package com.bharat.portfolio_backend.exception;

/**
 * A request that is syntactically valid but semantically contradictory, such as
 * an end date that precedes its start date. Mapped to HTTP 400.
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
