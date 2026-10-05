package com.bharat.portfolio_backend.requestDto;

import com.bharat.portfolio_backend.enums.ProficiencyLevel;
import com.bharat.portfolio_backend.enums.Visibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SkillRequest(

        @NotBlank @Size(max = 80) String domain,

        @Size(max = 255) String description,

        ProficiencyLevel proficiencyLevel,

        Visibility visibility,

        @Min(0) Integer displayOrder,

        @Size(max = 50) List<@Size(max = 60) String> technologies
) {
}