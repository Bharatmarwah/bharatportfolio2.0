package com.bharat.portfolio_backend.service;

/**
 * Server-built mail payload. Not a client input type: the recipient is always
 * resolved from configuration, never from a request body.
 */
public record MailRequest(String to, String subject, String body) {
}