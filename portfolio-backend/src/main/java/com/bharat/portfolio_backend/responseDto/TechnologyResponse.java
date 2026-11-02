package com.bharat.portfolio_backend.responseDto;

import com.bharat.portfolio_backend.enums.TechnologyCategory;

public record TechnologyResponse(
        Long id,
        String name,
        TechnologyCategory category,
        String iconUrl,
        String colorHex,
        String websiteUrl,
        int displayOrder
) {
}