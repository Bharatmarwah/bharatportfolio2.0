package com.bharat.portfolio_backend.responseDto;

import java.util.List;

/**
 * Full aggregate read for the homepage.
 * Children are sorted by displayOrder and are already filtered to PUBLIC
 * visibility so the frontend never has to hide anything itself.
 */
public record ProfileResponse(
        Long id,
        String name,
        String slug,
        String headline,
        String bio,
        String location,
        String profileUrl,
        String avatarUrl,
        String resumeUrl,
        String websiteUrl,
        SocialLinkResponse socialLink,
        List<ProjectResponse> projects,
        List<SkillResponse> skills,
        List<CertificateResponse> certifications
) {
}