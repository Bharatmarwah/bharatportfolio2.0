package com.bharat.portfolio_backend.responseDto;

import com.bharat.portfolio_backend.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * List projection: everything needed to render a blog index card, without the
 * potentially large article body.
 */
public record BlogPostSummaryResponse(
        Long id,
        String title,
        String slug,
        String excerpt,
        String coverImageUrl,
        PostStatus status,
        boolean featured,
        int readingTimeMinutes,
        LocalDateTime publishedAt,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
