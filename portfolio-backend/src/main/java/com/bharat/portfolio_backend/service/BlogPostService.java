package com.bharat.portfolio_backend.service;

import com.bharat.portfolio_backend.entity.BlogPost;
import com.bharat.portfolio_backend.enums.PostStatus;
import com.bharat.portfolio_backend.exception.DuplicateResourceException;
import com.bharat.portfolio_backend.exception.ResourceNotFoundException;
import com.bharat.portfolio_backend.repository.BlogPostRepository;
import com.bharat.portfolio_backend.requestDto.BlogPostRequest;
import com.bharat.portfolio_backend.responseDto.BlogPostResponse;
import com.bharat.portfolio_backend.responseDto.BlogPostSummaryResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class BlogPostService {

    private static final int WORDS_PER_MINUTE = 200;

    private final BlogPostRepository blogPostRepository;
    private final ProfileService profileService;

    public BlogPostService(BlogPostRepository blogPostRepository, ProfileService profileService) {
        this.blogPostRepository = blogPostRepository;
        this.profileService = profileService;
    }

    /**
     * Public blog index. Only PUBLISHED posts are eligible; featured and tag are
     * optional filters. Results are newest-first by publication date.
     */
    @Transactional(readOnly = true)
    public Page<BlogPostSummaryResponse> findPublished(Boolean featured, String tag, Pageable pageable) {
        String normalisedTag = TechnologyService.blankToNull(tag);
        LocalDateTime now = LocalDateTime.now();
        Specification<BlogPost> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), PostStatus.PUBLISHED));
            // A future publishedAt is a scheduled post and stays private until then.
            predicates.add(cb.lessThanOrEqualTo(root.get("publishedAt"), now));
            if (featured != null) {
                predicates.add(cb.equal(root.get("featured"), featured));
            }
            if (normalisedTag != null) {
                predicates.add(cb.isMember(normalisedTag, root.get("tags")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return blogPostRepository.findAll(specification, sorted(pageable, "publishedAt"))
                .map(BlogPostService::toSummary);
    }

    @Transactional(readOnly = true)
    public BlogPostResponse findPublishedBySlug(String slug) {
        BlogPost post = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> notFound(slug));
        if (post.getStatus() != PostStatus.PUBLISHED
                || post.getPublishedAt() == null
                || post.getPublishedAt().isAfter(LocalDateTime.now())) {
            // A draft, archived or scheduled post must be indistinguishable from a
            // missing post on the public API.
            throw notFound(slug);
        }
        return toResponse(post);
    }

    @Transactional(readOnly = true)
    public Page<BlogPostSummaryResponse> findAll(Pageable pageable) {
        return blogPostRepository.findAll(sorted(pageable, "createdAt"))
                .map(BlogPostService::toSummary);
    }

    @Transactional(readOnly = true)
    public BlogPostResponse findById(Long id) {
        return toResponse(require(id));
    }

    @Transactional
    public BlogPostResponse create(BlogPostRequest request) {
        BlogPost post = new BlogPost();
        post.setProfile(profileService.requireProfile());
        apply(post, request);
        return toResponse(blogPostRepository.save(post));
    }

    @Transactional
    public BlogPostResponse update(Long id, BlogPostRequest request) {
        BlogPost post = require(id);
        apply(post, request);
        return toResponse(blogPostRepository.save(post));
    }

    @Transactional
    public void delete(Long id) {
        blogPostRepository.delete(require(id));
    }

    private void apply(BlogPost post, BlogPostRequest request) {
        String slug = request.slug().trim().toLowerCase(Locale.ROOT);
        if (!slug.equals(post.getSlug()) && blogPostRepository.existsBySlug(slug)) {
            throw DuplicateResourceException.of("blog post slug", slug);
        }

        post.setSlug(slug);
        post.setTitle(request.title().trim());
        post.setExcerpt(request.excerpt().trim());
        post.setContent(request.content());
        post.setCoverImageUrl(TechnologyService.blankToNull(request.coverImageUrl()));

        PostStatus status = request.status() == null ? post.getStatus() : request.status();
        post.setStatus(status);

        if (request.featured() != null) {
            post.setFeatured(request.featured());
        }

        post.setReadingTimeMinutes(request.readingTimeMinutes() == null
                ? estimateReadingTime(request.content())
                : request.readingTimeMinutes());

        LocalDateTime publishedAt = request.publishedAt() == null
                ? post.getPublishedAt()
                : request.publishedAt();
        if (status == PostStatus.PUBLISHED && publishedAt == null) {
            // First publication: stamp the post so the public ordering is stable.
            publishedAt = LocalDateTime.now();
        }
        post.setPublishedAt(publishedAt);

        post.setTags(normaliseTags(request.tags()));
    }

    private static Pageable sorted(Pageable pageable, String property) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc(property), Sort.Order.desc("id")));
    }

    private static int estimateReadingTime(String content) {
        if (content == null || content.isBlank()) {
            return 1;
        }
        int words = content.trim().split("\\s+").length;
        return Math.max(1, (int) Math.ceil(words / (double) WORDS_PER_MINUTE));
    }

    private static List<String> normaliseTags(Collection<String> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        Map<String, String> byKey = new LinkedHashMap<>();
        for (String tag : tags) {
            if (tag == null || tag.isBlank()) {
                continue;
            }
            String trimmed = tag.trim();
            byKey.putIfAbsent(trimmed.toLowerCase(Locale.ROOT), trimmed);
        }
        return new ArrayList<>(byKey.values());
    }

    private BlogPost require(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("BlogPost", id));
    }

    private static ResourceNotFoundException notFound(String slug) {
        return new ResourceNotFoundException("Blog post not found with slug " + slug);
    }

    static BlogPostResponse toResponse(BlogPost post) {
        return new BlogPostResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getExcerpt(),
                post.getContent(),
                post.getCoverImageUrl(),
                post.getStatus(),
                post.isFeatured(),
                post.getReadingTimeMinutes(),
                post.getPublishedAt(),
                safeTags(post.getTags()),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }

    static BlogPostSummaryResponse toSummary(BlogPost post) {
        return new BlogPostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getExcerpt(),
                post.getCoverImageUrl(),
                post.getStatus(),
                post.isFeatured(),
                post.getReadingTimeMinutes(),
                post.getPublishedAt(),
                safeTags(post.getTags()),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }

    private static List<String> safeTags(Collection<String> tags) {
        if (tags == null) {
            return List.of();
        }
        return tags.stream().filter(Objects::nonNull).toList();
    }
}
