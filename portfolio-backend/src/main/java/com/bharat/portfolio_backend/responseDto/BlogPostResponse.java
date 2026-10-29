package com.bharat.portfolio_backend.responseDto;

import com.bharat.portfolio_backend.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BlogPostResponse(
        Long id,
        String title,
        String slug,
        String excerpt,
        String content,
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
