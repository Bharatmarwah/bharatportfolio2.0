package com.bharat.portfolio_backend.responseDto;

import java.time.LocalDateTime;

public record ContactMessageResponse(
        Long id,
        String name,
        String email,
        String subject,
        String message,
        LocalDateTime createdAt
) {
}