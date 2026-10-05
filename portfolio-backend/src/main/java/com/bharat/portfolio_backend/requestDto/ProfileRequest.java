package com.bharat.portfolio_backend.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Scalar profile fields only.
 * Child collections are intentionally absent so a profile update can never
 * orphan-remove projects, skills or certificates that were left out of the payload.
 */
public record ProfileRequest(

        @NotBlank @Size(max = 100) String name,

        @NotBlank @Size(max = 120) String slug,

        @NotBlank @Size(max = 160) String headline,

        @NotBlank String bio,

        @NotBlank @Size(max = 120) String location,

        @Size(max = 255) String profileUrl,

        @Size(max = 255) String avatarUrl,

        @Size(max = 255) String resumeUrl,

        @Pattern(regexp = "^$|^https?://.+$", message = "websiteUrl must be a valid URL")
        @Size(max = 255) String websiteUrl
) {
}