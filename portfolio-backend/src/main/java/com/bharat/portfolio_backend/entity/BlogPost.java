package com.bharat.portfolio_backend.entity;

import com.bharat.portfolio_backend.enums.PostStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A blog article belonging to the single portfolio owner.
 * Drafts and archived posts stay in the table but are never served by the
 * public /v1/blog endpoints.
 */
@Getter
@Setter
@Entity
@Table(name = "blog_posts", indexes = {
        @Index(name = "idx_blog_posts_status_published", columnList = "status, published_at")
})
public class BlogPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @NotBlank
    @Size(max = 160)
    @Column(name = "post_title", nullable = false, length = 160)
    private String title;

    @NotBlank
    @Size(max = 160)
    @Column(name = "post_slug", nullable = false, unique = true, length = 160)
    private String slug;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String excerpt;

    /**
     * Markdown/HTML body. LONGTEXT so long tutorials do not hit the VARCHAR limit.
     */
    @NotBlank
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status = PostStatus.DRAFT;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(name = "reading_time_minutes", nullable = false)
    private int readingTimeMinutes = 1;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "blog_post_tags", joinColumns = @JoinColumn(name = "blog_post_id"))
    @Column(name = "tag", nullable = false, length = 40)
    @OrderColumn(name = "display_order")
    private List<String> tags = new ArrayList<>();
}
