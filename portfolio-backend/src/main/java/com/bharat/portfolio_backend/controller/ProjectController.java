package com.bharat.portfolio_backend.controller;

import com.bharat.portfolio_backend.config.PublicEndpoint;
import com.bharat.portfolio_backend.requestDto.ProjectRequest;
import com.bharat.portfolio_backend.responseDto.ProjectResponse;
import com.bharat.portfolio_backend.service.ProjectService;
import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@RequestMapping("/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PublicEndpoint
    @GetMapping
    public List<ProjectResponse> list(@RequestParam(defaultValue = "false") boolean featured) {
        return projectService.findAll(featured, false);
    }

    @PublicEndpoint
    @GetMapping("/{slug}")
    public ProjectResponse getBySlug(@PathVariable String slug) {
        return projectService.findBySlug(slug);
    }

    @GetMapping("/id/{id}")
    public ProjectResponse getById(@PathVariable Long id) {
        return projectService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request));
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return projectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}