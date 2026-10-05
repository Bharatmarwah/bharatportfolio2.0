package com.bharat.portfolio_backend.controller;

import com.bharat.portfolio_backend.config.PublicEndpoint;
import com.bharat.portfolio_backend.requestDto.TechnologyRequest;
import com.bharat.portfolio_backend.responseDto.TechnologyResponse;
import com.bharat.portfolio_backend.service.TechnologyService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/technologies")
public class TechnologyController {

    private final TechnologyService technologyService;

    public TechnologyController(TechnologyService technologyService) {
        this.technologyService = technologyService;
    }

    @PublicEndpoint
    @GetMapping
    public List<TechnologyResponse> list() {
        return technologyService.findAll();
    }

    @GetMapping("/{id}")
    public TechnologyResponse getById(@PathVariable Long id) {
        return technologyService.findById(id);
    }

    @PostMapping
    public ResponseEntity<TechnologyResponse> create(@Valid @RequestBody TechnologyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(technologyService.create(request));
    }

    @PutMapping("/{id}")
    public TechnologyResponse update(@PathVariable Long id, @Valid @RequestBody TechnologyRequest request) {
        return technologyService.update(id, request);
    }

    /**
     * Returns 409 while a technology is still referenced by a project, skill or
     * certificate, because the join-table row would violate its foreign key.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        technologyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}