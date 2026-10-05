package com.bharat.portfolio_backend.responseDto;

import com.bharat.portfolio_backend.enums.ProjectStatus;
import com.bharat.portfolio_backend.enums.Visibility;

import java.time.LocalDate;
import java.util.List;

public record ProjectResponse(
        Long id,
        String name,
        String slug,
        String headline,
        String description,
        String role,
        ProjectStatus status,
        Visibility visibility,
        boolean featured,
        int displayOrder,
        String githubUrl,
        String liveUrl,
        String demoVideoUrl,
        String coverImageUrl,
        List<String> imagesUrl,
        List<String> highlights,
        List<TechnologyResponse> technologies,
        LocalDate startDate,
        LocalDate endDate,
        boolean ongoing
) {
}