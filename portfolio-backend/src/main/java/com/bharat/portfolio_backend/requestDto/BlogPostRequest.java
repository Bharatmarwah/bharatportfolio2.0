package com.bharat.portfolio_backend.requestDto;

import  com.bharat.portfolio_backend.enums.PostStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Create/update payload for a blog post.
 * On update, a null status/featured keeps the current value instead of resetting it.
 */
public record BlogPostRequest(

        @NotBlank @Size(max = 160) String title,

        @NotBlank
        @Size(max = 160)
        @Pattern(regexp = "^[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*$",
                message = "slug must contain only letters, digits and single hyphens")
        String slug,

        @NotBlank @Size(max = 500) String excerpt,

        @NotBlank @Size(max = 100_000) String content,

        @Size(max = 500) String coverImageUrl,

        PostStatus status,

        Boolean featured,

        @Min(1) Integer readingTimeMinutes,

        LocalDateTime publishedAt,

        @Size(max = 20) List<@NotBlank @Size(max = 40) String> tags
) {
}
