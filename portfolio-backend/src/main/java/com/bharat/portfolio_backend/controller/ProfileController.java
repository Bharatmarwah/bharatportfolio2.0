package com.bharat.portfolio_backend.controller;

import com.bharat.portfolio_backend.config.PublicEndpoint;
import com.bharat.portfolio_backend.requestDto.ProfileRequest;
import com.bharat.portfolio_backend.requestDto.SocialLinkRequest;
import com.bharat.portfolio_backend.responseDto.BlogPostSummaryResponse;
import com.bharat.portfolio_backend.responseDto.CertificateResponse;
import com.bharat.portfolio_backend.responseDto.ProfileResponse;
import com.bharat.portfolio_backend.responseDto.ProjectResponse;
import com.bharat.portfolio_backend.responseDto.SkillResponse;
import com.bharat.portfolio_backend.responseDto.SocialLinkResponse;
import com.bharat.portfolio_backend.service.BlogPostService;
import com.bharat.portfolio_backend.service.CertificateService;
import com.bharat.portfolio_backend.service.ProfileService;
import com.bharat.portfolio_backend.service.ProjectService;
import com.bharat.portfolio_backend.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/me")
public class ProfileController {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProfileService profileService;
    private final ProjectService projectService;
    private final SkillService skillService;
    private final CertificateService certificateService;
    private final BlogPostService blogPostService;

    public ProfileController(ProfileService profileService,
                             ProjectService projectService,
                             SkillService skillService,
                             CertificateService certificateService,
                             BlogPostService blogPostService) {
        this.profileService = profileService;
        this.projectService = projectService;
        this.skillService = skillService;
        this.certificateService = certificateService;
        this.blogPostService = blogPostService;
    }

    /**
     * Public. Returns the whole aggregate for the homepage: profile, social card,
     * and every PUBLIC project, skill and certificate ordered by displayOrder.
     */
    @PublicEndpoint
    @GetMapping
    public ProfileResponse getProfile() {
        return profileService.getProfile();
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(@Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.createProfile(request));
    }

    /**
     * Scalar fields only - see ProfileRequest. Child sections are managed through
     * their own endpoints so this can never orphan-remove a project or skill.
     */
    @PutMapping
    public ProfileResponse updateProfile(@Valid @RequestBody ProfileRequest request) {
        return profileService.updateProfile(request);
    }

    @PutMapping("/social-links")
    public SocialLinkResponse updateSocialLinks(@Valid @RequestBody SocialLinkRequest request) {
        return profileService.updateSocialLinks(request);
    }

    /**
     * Admin listings that include HIDDEN items, so drafts stay discoverable and can
     * be un-hidden. Deliberately not {@link PublicEndpoint}, and deliberately not on
     * the public /v1/projects style paths, which only ever return PUBLIC content.
     */
    @GetMapping("/projects")
    public List<ProjectResponse> listAllProjects(@RequestParam(defaultValue = "false") boolean featured) {
        return projectService.findAll(featured, true);
    }

    @GetMapping("/skills")
    public List<SkillResponse> listAllSkills() {
        return skillService.findAll(true);
    }

    @GetMapping("/certifications")
    public List<CertificateResponse> listAllCertifications() {
        return certificateService.findAll(true);
    }

    /**
     * Admin blog index including drafts and archived posts. The public index lives
     * at GET /v1/blog and only ever returns PUBLISHED posts.
     */
    @GetMapping("/blog")
    public Page<BlogPostSummaryResponse> listAllBlogPosts(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);
        return blogPostService.findAll(PageRequest.of(safePage, safeSize));
    }
}