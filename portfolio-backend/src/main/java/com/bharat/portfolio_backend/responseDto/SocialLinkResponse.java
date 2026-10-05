package com.bharat.portfolio_backend.responseDto;

public record SocialLinkResponse(
        Long id,
        String email,
        String number,
        String linkedInUrl,
        String githubUrl,
        String twitterUrl,
        String youtubeUrl,
        String instagramUrl
) {
}