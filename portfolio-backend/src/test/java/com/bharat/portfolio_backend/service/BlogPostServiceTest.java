package com.bharat.portfolio_backend.service;

import com.bharat.portfolio_backend.entity.BlogPost;
import com.bharat.portfolio_backend.entity.Profile;
import com.bharat.portfolio_backend.enums.PostStatus;
import com.bharat.portfolio_backend.exception.DuplicateResourceException;
import com.bharat.portfolio_backend.exception.ResourceNotFoundException;
import com.bharat.portfolio_backend.repository.BlogPostRepository;
import com.bharat.portfolio_backend.requestDto.BlogPostRequest;
import com.bharat.portfolio_backend.responseDto.BlogPostResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceTest {

    @Mock
    private BlogPostRepository blogPostRepository;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private BlogPostService blogPostService;

    @Test
    void createNormalisesSlugTagsAndReadingTime() {
        when(profileService.requireProfile()).thenReturn(new Profile());
        when(blogPostRepository.existsBySlug("hello-world")).thenReturn(false);
        when(blogPostRepository.save(any(BlogPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BlogPostRequest request = new BlogPostRequest(
                "  Hello World  ",
                "Hello-World",
                "  A short excerpt.  ",
                "word ".repeat(500),
                " ",
                PostStatus.PUBLISHED,
                true,
                null,
                null,
                List.of("Java", " java ", "Spring Boot"));

        BlogPostResponse response = blogPostService.create(request);

        assertThat(response.title()).isEqualTo("Hello World");
        assertThat(response.slug()).isEqualTo("hello-world");
        assertThat(response.excerpt()).isEqualTo("A short excerpt.");
        assertThat(response.coverImageUrl()).isNull();
        assertThat(response.status()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(response.featured()).isTrue();
        assertThat(response.publishedAt()).isNotNull();
        assertThat(response.readingTimeMinutes()).isEqualTo(3);
        assertThat(response.tags()).containsExactly("Java", "Spring Boot");
    }

    @Test
    void createRejectsDuplicateSlug() {
        when(profileService.requireProfile()).thenReturn(new Profile());
        when(blogPostRepository.existsBySlug("hello-world")).thenReturn(true);

        BlogPostRequest request = request("Hello World", "hello-world", PostStatus.DRAFT);

        assertThatThrownBy(() -> blogPostService.create(request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(blogPostRepository, never()).save(any(BlogPost.class));
    }

    @Test
    void publicSlugLookupHidesDrafts() {
        BlogPost draft = new BlogPost();
        draft.setSlug("secret-draft");
        draft.setStatus(PostStatus.DRAFT);
        when(blogPostRepository.findBySlug("secret-draft")).thenReturn(Optional.of(draft));

        assertThatThrownBy(() -> blogPostService.findPublishedBySlug("secret-draft"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void publicSlugLookupHidesScheduledPosts() {
        BlogPost scheduled = new BlogPost();
        scheduled.setSlug("future-post");
        scheduled.setStatus(PostStatus.PUBLISHED);
        scheduled.setPublishedAt(LocalDateTime.now().plusDays(1));
        when(blogPostRepository.findBySlug("future-post")).thenReturn(Optional.of(scheduled));

        assertThatThrownBy(() -> blogPostService.findPublishedBySlug("future-post"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateKeepsStateWhenOptionalFieldsAreOmitted() {
        LocalDateTime publishedAt = LocalDateTime.now().minusDays(2);
        BlogPost existing = new BlogPost();
        existing.setSlug("existing-post");
        existing.setTitle("Old title");
        existing.setExcerpt("Old excerpt");
        existing.setContent("Old content");
        existing.setStatus(PostStatus.PUBLISHED);
        existing.setFeatured(true);
        existing.setPublishedAt(publishedAt);

        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(blogPostRepository.save(existing)).thenReturn(existing);

        BlogPostRequest request = new BlogPostRequest(
                "New title", "existing-post", "New excerpt", "New content",
                null, null, null, null, null, null);

        BlogPostResponse response = blogPostService.update(1L, request);

        assertThat(response.title()).isEqualTo("New title");
        assertThat(response.status()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(response.featured()).isTrue();
        assertThat(response.publishedAt()).isEqualTo(publishedAt);
        assertThat(response.tags()).isEmpty();
    }

    private static BlogPostRequest request(String title, String slug, PostStatus status) {
        return new BlogPostRequest(title, slug, "Excerpt", "Content", null, status, false, 1, null, List.of());
    }
}
