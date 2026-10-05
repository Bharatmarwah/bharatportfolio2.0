package com.bharat.portfolio_backend.controller;

import com.bharat.portfolio_backend.config.PublicEndpoint;
import com.bharat.portfolio_backend.requestDto.SkillRequest;
import com.bharat.portfolio_backend.responseDto.SkillResponse;
import com.bharat.portfolio_backend.service.SkillService;
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
@RequestMapping("/v1/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PublicEndpoint
    @GetMapping
    public List<SkillResponse> list() {
        return skillService.findAll(false);
    }

    @GetMapping("/{id}")
    public SkillResponse getById(@PathVariable Long id) {
        return skillService.findById(id);
    }

    @PostMapping
    public ResponseEntity<SkillResponse> create(@Valid @RequestBody SkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.create(request));
    }

    @PutMapping("/{id}")
    public SkillResponse update(@PathVariable Long id, @Valid @RequestBody SkillRequest request) {
        return skillService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}