package com.bharat.portfolio_backend.requestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ipAddress and userAgent are intentionally not accepted from the client;
 * the service takes them from the incoming request so a caller cannot spoof them.
 */
public record ContactMessageRequest(

        @NotBlank @Size(max = 100) String name,

        @NotBlank @Email @Size(max = 120) String email,

        @Size(max = 160) String subject,

        @NotBlank @Size(max = 5000) String message
) {
}