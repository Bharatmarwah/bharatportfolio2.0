package com.bharat.portfolio_backend.requestDto;

import com.bharat.portfolio_backend.enums.Visibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CertificateRequest(

        @NotBlank @Size(max = 160) String name,

        @NotBlank @Size(max = 120) String sourceName,

        @Size(max = 255) String sourceUrl,

        @NotBlank String description,

        @Size(max = 500) String downloadUrl,

        @Size(max = 120) String credentialId,

        @Size(max = 255) String logoUrl,

        LocalDate issueDate,

        LocalDate expiryDate,

        Boolean hasExpiry,

        Visibility visibility,

        Boolean featured,

        @Min(0) Integer displayOrder,

        @Size(max = 50) List<@Size(max = 60) String> technologies
) {
}