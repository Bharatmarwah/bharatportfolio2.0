package com.bharat.portfolio_backend.service;

import com.bharat.portfolio_backend.entity.Project;
import com.bharat.portfolio_backend.enums.ProjectStatus;
import com.bharat.portfolio_backend.enums.Visibility;
import com.bharat.portfolio_backend.exception.DuplicateResourceException;
import com.bharat.portfolio_backend.exception.ResourceNotFoundException;
import com.bharat.portfolio_backend.repository.ProjectRepository;
import com.bharat.portfolio_backend.requestDto.ProjectRequest;
import com.bharat.portfolio_backend.responseDto.ProjectResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProfileService profileService;
    private final TechnologyService technologyService;

    public ProjectService(ProjectRepository projectRepository,
                          ProfileService profileService,
                          TechnologyService technologyService) {
        this.projectRepository = projectRepository;
        this.profileService = profileService;
        this.technologyService = technologyService;
    }

    /**
     * includeHidden is for admin use so hidden items stay discoverable and can be
     * un-hidden; it is only reachable because this endpoint is not public.
     */
    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll(boolean featuredOnly, boolean includeHidden) {
        List<Project> projects;
        if (includeHidden) {
            projects = projectRepository.findAllByOrderByDisplayOrderAsc();
            if (featuredOnly) {
                projects = projects.stream().filter(Project::isFeatured).toList();
            }
        } else {
            projects = featuredOnly
                    ? projectRepository.findByFeaturedTrueAndVisibilityOrderByDisplayOrderAsc(Visibility.PUBLIC)
                    : projectRepository.findByVisibilityOrderByDisplayOrderAsc(Visibility.PUBLIC);
        }
        return projects.stream().map(ProjectService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findBySlug(String slug) {
        Project project = projectRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with slug " + slug));
        if (project.getVisibility() != Visibility.PUBLIC) {
            throw new ResourceNotFoundException("Project not found with slug " + slug);
        }
        return toResponse(project);
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return toResponse(require(id));
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        String slug = request.slug().trim();
        if (projectRepository.existsBySlug(slug)) {
            throw DuplicateResourceException.of("project slug", slug);
        }
        Project project = new Project();
        project.setProfile(profileService.requireProfile());
        project.setSlug(slug);
        apply(project, request);
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = require(id);
        String slug = request.slug().trim();
        if (!slug.equalsIgnoreCase(project.getSlug()) && projectRepository.existsBySlug(slug)) {
            throw DuplicateResourceException.of("project slug", slug);
        }
        project.setSlug(slug);
        apply(project, request);
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public void delete(Long id) {
        projectRepository.delete(require(id));
    }

    private void apply(Project project, ProjectRequest request) {
        project.setName(request.name().trim());
        project.setHeadline(request.headline().trim());
        project.setDescription(request.description());
        project.setRole(TechnologyService.blankToNull(request.role()));
        project.setStatus(request.status() == null ? ProjectStatus.COMPLETED : request.status());
        project.setVisibility(request.visibility() == null ? Visibility.PUBLIC : request.visibility());
        project.setFeatured(Boolean.TRUE.equals(request.featured()));
        project.setDisplayOrder(request.displayOrder() == null ? 0 : request.displayOrder());
        project.setGithubUrl(TechnologyService.blankToNull(request.githubUrl()));
        project.setLiveUrl(TechnologyService.blankToNull(request.liveUrl()));
        project.setDemoVideoUrl(TechnologyService.blankToNull(request.demoVideoUrl()));
        project.setCoverImageUrl(TechnologyService.blankToNull(request.coverImageUrl()));
        project.setImagesUrl(cleanList(request.imagesUrl()));
        project.setHighlights(cleanList(request.highlights()));
        project.setTechnologies(technologyService.resolveByNames(request.technologies()));
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setOngoing(Boolean.TRUE.equals(request.ongoing()));
    }

    private List<String> cleanList(List<String> values) {
        if (values == null) {
            return new ArrayList<>();
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }

    private Project require(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Project", id));
    }

    static ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getSlug(),
                project.getHeadline(),
                project.getDescription(),
                project.getRole(),
                project.getStatus(),
                project.getVisibility(),
                project.isFeatured(),
                project.getDisplayOrder(),
                project.getGithubUrl(),
                project.getLiveUrl(),
                project.getDemoVideoUrl(),
                project.getCoverImageUrl(),
                List.copyOf(project.getImagesUrl()),
                List.copyOf(project.getHighlights()),
                TechnologyService.toResponses(project.getTechnologies()),
                project.getStartDate(),
                project.getEndDate(),
                project.isOngoing());
    }
}