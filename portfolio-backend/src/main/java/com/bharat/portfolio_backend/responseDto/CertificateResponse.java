package com.bharat.portfolio_backend.responseDto;

import com.bharat.portfolio_backend.enums.Visibility;

import java.time.LocalDate;
import java.util.List;

public record CertificateResponse(
        Long id,
        String name,
        String sourceName,
        String sourceUrl,
        String description,
        String downloadUrl,
        String credentialId,
        String logoUrl,
        LocalDate issueDate,
        LocalDate expiryDate,
        boolean hasExpiry,
        Visibility visibility,
        boolean featured,
        int displayOrder,
        List<TechnologyResponse> technologies
) {
}