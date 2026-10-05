package com.bharat.portfolio_backend.service;

/**
 * Raised once a contact message row is committed, so notification delivery never
 * participates in the database transaction.
 */
public record ContactMessageReceivedEvent(Long id, String name, String email, String subject, String message) {
}