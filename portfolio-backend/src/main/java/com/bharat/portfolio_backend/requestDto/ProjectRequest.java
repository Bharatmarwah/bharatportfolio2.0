package com.bharat.portfolio_backend.requestDto;

import com.bharat.portfolio_backend.enums.ProjectStatus;
import com.bharat.portfolio_backend.enums.Visibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * technologies is a list of names rather than ids, so a project can be created
 * before the technology catalogue has been seeded. Names are upserted by the service.
 */
public record ProjectRequest(

        @NotBlank @Size(max = 120) String name,

        @NotBlank @Size(max = 120) String slug,

        @NotBlank @Size(max = 180) String headline,

        @NotBlank String description,

        @Size(max = 120) String role,

        ProjectStatus status,

        Visibility visibility,

        Boolean featured,

        @Min(0) Integer displayOrder,

        @Size(max = 255) String githubUrl,

        @Size(max = 255) String liveUrl,

        @Size(max = 255) String demoVideoUrl,

        @Size(max = 500) String coverImageUrl,

        @Size(max = 50) List<@Size(max = 500) String> imagesUrl,

        @Size(max = 30) List<@Size(max = 500) String> highlights,

        @Size(max = 50) List<@Size(max = 60) String> technologies,

        LocalDate startDate,

        LocalDate endDate,

        Boolean ongoing
) {
}