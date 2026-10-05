package com.bharat.portfolio_backend.service;

import com.bharat.portfolio_backend.entity.Certificate;
import com.bharat.portfolio_backend.entity.Profile;
import com.bharat.portfolio_backend.entity.Project;
import com.bharat.portfolio_backend.entity.Skill;
import com.bharat.portfolio_backend.entity.SocialLink;
import com.bharat.portfolio_backend.enums.Visibility;
import com.bharat.portfolio_backend.exception.DuplicateResourceException;
import com.bharat.portfolio_backend.exception.ResourceNotFoundException;
import com.bharat.portfolio_backend.repository.ProfileRepository;
import com.bharat.portfolio_backend.requestDto.ProfileRequest;
import com.bharat.portfolio_backend.requestDto.SocialLinkRequest;
import com.bharat.portfolio_backend.responseDto.CertificateResponse;
import com.bharat.portfolio_backend.responseDto.ProfileResponse;
import com.bharat.portfolio_backend.responseDto.ProjectResponse;
import com.bharat.portfolio_backend.responseDto.SkillResponse;
import com.bharat.portfolio_backend.responseDto.SocialLinkResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile() {
        return toResponse(requireProfile());
    }

    @Transactional
    public ProfileResponse createProfile(ProfileRequest request) {
        if (profileRepository.count() > 0) {
            throw new DuplicateResourceException("A profile already exists. Use PUT /v1/me to update it.");
        }
        Profile profile = new Profile();
        applyScalars(profile, request);
        return toResponse(profileRepository.save(profile));
    }

    @Transactional
    public ProfileResponse updateProfile(ProfileRequest request) {
        Profile profile = requireProfile();
        String slug = request.slug().trim();
        if (!slug.equalsIgnoreCase(profile.getSlug()) && profileRepository.findBySlug(slug).isPresent()) {
            throw DuplicateResourceException.of("profile slug", slug);
        }
        applyScalars(profile, request);
        return toResponse(profileRepository.save(profile));
    }

    @Transactional
    public SocialLinkResponse updateSocialLinks(SocialLinkRequest request) {
        Profile profile = requireProfile();
        SocialLink socialLink = profile.getSocialLink();
        if (socialLink == null) {
            socialLink = new SocialLink();
            socialLink.setProfile(profile);
            profile.setSocialLink(socialLink);
        }
        socialLink.setEmail(TechnologyService.blankToNull(request.email()));
        socialLink.setNumber(TechnologyService.blankToNull(request.number()));
        socialLink.setLinkedInUrl(TechnologyService.blankToNull(request.linkedInUrl()));
        socialLink.setGithubUrl(TechnologyService.blankToNull(request.githubUrl()));
        socialLink.setTwitterUrl(TechnologyService.blankToNull(request.twitterUrl()));
        socialLink.setYoutubeUrl(TechnologyService.blankToNull(request.youtubeUrl()));
        socialLink.setInstagramUrl(TechnologyService.blankToNull(request.instagramUrl()));
        return toSocialLinkResponse(profileRepository.save(profile).getSocialLink());
    }

    /**
     * The portfolio has a single owner, so the aggregate read always targets the
     * first profile row.
     */
    @Transactional(readOnly = true)
    public Profile requireProfile() {
        return profileRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No profile exists yet. Create one with POST /v1/me"));
    }

    private void applyScalars(Profile profile, ProfileRequest request) {
        profile.setName(request.name().trim());
        profile.setSlug(request.slug().trim());
        profile.setHeadline(request.headline().trim());
        profile.setBio(request.bio());
        profile.setLocation(request.location().trim());
        profile.setProfileUrl(TechnologyService.blankToNull(request.profileUrl()));
        profile.setAvatarUrl(TechnologyService.blankToNull(request.avatarUrl()));
        profile.setResumeUrl(TechnologyService.blankToNull(request.resumeUrl()));
        profile.setWebsiteUrl(TechnologyService.blankToNull(request.websiteUrl()));
    }

    static ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getSlug(),
                profile.getHeadline(),
                profile.getBio(),
                profile.getLocation(),
                profile.getProfileUrl(),
                profile.getAvatarUrl(),
                profile.getResumeUrl(),
                profile.getWebsiteUrl(),
                toSocialLinkResponse(profile.getSocialLink()),
                profile.getProjects().stream()
                        .filter(project -> project.getVisibility() == Visibility.PUBLIC)
                        .sorted(Comparator.comparingInt(Project::getDisplayOrder))
                        .map(ProjectService::toResponse)
                        .toList(),
                profile.getSkills().stream()
                        .filter(skill -> skill.getVisibility() == Visibility.PUBLIC)
                        .sorted(Comparator.comparingInt(Skill::getDisplayOrder))
                        .map(SkillService::toResponse)
                        .toList(),
                profile.getCertifications().stream()
                        .filter(certificate -> certificate.getVisibility() == Visibility.PUBLIC)
                        .sorted(Comparator.comparingInt(Certificate::getDisplayOrder))
                        .map(CertificateService::toResponse)
                        .toList());
    }

    static SocialLinkResponse toSocialLinkResponse(SocialLink socialLink) {
        if (socialLink == null) {
            return null;
        }
        return new SocialLinkResponse(
                socialLink.getId(),
                socialLink.getEmail(),
                socialLink.getNumber(),
                socialLink.getLinkedInUrl(),
                socialLink.getGithubUrl(),
                socialLink.getTwitterUrl(),
                socialLink.getYoutubeUrl(),
                socialLink.getInstagramUrl());
    }
}