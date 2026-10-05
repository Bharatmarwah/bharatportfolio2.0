package com.bharat.portfolio_backend.responseDto;

import com.bharat.portfolio_backend.enums.ProficiencyLevel;
import com.bharat.portfolio_backend.enums.Visibility;

import java.util.List;

public record SkillResponse(
        Long id,
        String domain,
        String description,
        ProficiencyLevel proficiencyLevel,
        Visibility visibility,
        int displayOrder,
        List<TechnologyResponse> technologies
) {
}