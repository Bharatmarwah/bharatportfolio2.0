package com.bharat.portfolio_backend.controller;

import com.bharat.portfolio_backend.config.PublicEndpoint;
import com.bharat.portfolio_backend.requestDto.BlogPostRequest;
import com.bharat.portfolio_backend.responseDto.BlogPostResponse;
import com.bharat.portfolio_backend.responseDto.BlogPostSummaryResponse;
import com.bharat.portfolio_backend.service.BlogPostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/blog")
public class BlogPostController {

    /**
     * The journal index is filtered client-side (search + facets), so the front
     * end requests one large page. Keep this in sync with BLOG_FETCH_SIZE in
     * portfolio-frontend/src/lib/api.ts.
     */
    private static final int MAX_PAGE_SIZE = 200;

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    /**
     * Public blog index. Returns PUBLISHED posts only, newest first. Optional
     * ?featured=true and ?tag=java filters.
     */
    @PublicEndpoint
    @GetMapping
    public Page<BlogPostSummaryResponse> list(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) Boolean featured,
                                              @RequestParam(required = false) String tag) {
        return blogPostService.findPublished(featured, tag, pageRequest(page, size));
    }

    /**
     * Public article read by slug. Drafts and archived posts return 404.
     */
    @PublicEndpoint
    @GetMapping("/{slug}")
    public BlogPostResponse getBySlug(@PathVariable String slug) {
        return blogPostService.findPublishedBySlug(slug);
    }

    /**
     * Admin read by id, including drafts and archived posts.
     */
    @GetMapping("/id/{id}")
    public BlogPostResponse getById(@PathVariable Long id) {
        return blogPostService.findById(id);
    }

    @PostMapping
    public ResponseEntity<BlogPostResponse> create(@Valid @RequestBody BlogPostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blogPostService.create(request));
    }

    @PutMapping("/{id}")
    public BlogPostResponse update(@PathVariable Long id, @Valid @RequestBody BlogPostRequest request) {
        return blogPostService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        blogPostService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private static PageRequest pageRequest(int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);
        return PageRequest.of(safePage, safeSize);
    }
}
