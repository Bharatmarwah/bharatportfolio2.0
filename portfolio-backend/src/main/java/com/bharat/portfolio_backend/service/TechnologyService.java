package com.bharat.portfolio_backend.service;

import com.bharat.portfolio_backend.entity.Technology;
import com.bharat.portfolio_backend.enums.TechnologyCategory;
import com.bharat.portfolio_backend.exception.DuplicateResourceException;
import com.bharat.portfolio_backend.exception.ResourceNotFoundException;
import com.bharat.portfolio_backend.repository.TechnologyRepository;
import com.bharat.portfolio_backend.requestDto.TechnologyRequest;
import com.bharat.portfolio_backend.responseDto.TechnologyResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class TechnologyService {

    private final TechnologyRepository technologyRepository;

    public TechnologyService(TechnologyRepository technologyRepository) {
        this.technologyRepository = technologyRepository;
    }

    @Transactional(readOnly = true)
    public List<TechnologyResponse> findAll() {
        return technologyRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(TechnologyService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TechnologyResponse findById(Long id) {
        return toResponse(require(id));
    }

    @Transactional
    public TechnologyResponse create(TechnologyRequest request) {
        String name = request.name().trim();
        if (technologyRepository.existsByNameIgnoreCase(name)) {
            throw DuplicateResourceException.of("technology name", name);
        }
        Technology technology = new Technology();
        technology.setName(name);
        apply(technology, request);
        return toResponse(technologyRepository.save(technology));
    }

    @Transactional
    public TechnologyResponse update(Long id, TechnologyRequest request) {
        Technology technology = require(id);
        String name = request.name().trim();
        if (!name.equalsIgnoreCase(technology.getName())
                && technologyRepository.existsByNameIgnoreCase(name)) {
            throw DuplicateResourceException.of("technology name", name);
        }
        technology.setName(name);
        apply(technology, request);
        return toResponse(technologyRepository.save(technology));
    }

    @Transactional
    public void delete(Long id) {
        technologyRepository.delete(require(id));
    }

    /**
     * Resolves technology names to persisted rows, inserting any that are new.
     * Lets a caller send "Spring Boot" as a plain string the first time it is used.
     */
    @Transactional
    public Set<Technology> resolveByNames(Collection<String> names) {
        List<String> cleaned = normalise(names);
        if (cleaned.isEmpty()) {
            return new LinkedHashSet<>();
        }

        Map<String, Technology> resolvedByName = new LinkedHashMap<>();
        technologyRepository.findByNameInIgnoreCase(cleaned)
                .forEach(technology -> resolvedByName.put(technology.getName().toLowerCase(Locale.ROOT), technology));

        List<Technology> toCreate = new ArrayList<>();
        for (String name : cleaned) {
            String key = name.toLowerCase(Locale.ROOT);
            if (!resolvedByName.containsKey(key)) {
                Technology technology = new Technology();
                technology.setName(name);
                technology.setCategory(TechnologyCategory.OTHER);
                toCreate.add(technology);
                resolvedByName.put(key, technology);
            }
        }
        if (!toCreate.isEmpty()) {
            technologyRepository.saveAll(toCreate);
        }

        Set<Technology> resolved = new LinkedHashSet<>();
        for (String name : cleaned) {
            resolved.add(resolvedByName.get(name.toLowerCase(Locale.ROOT)));
        }
        return resolved;
    }

    private List<String> normalise(Collection<String> names) {
        if (names == null) {
            return List.of();
        }
        return names.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }

    private void apply(Technology technology, TechnologyRequest request) {
        technology.setCategory(request.category() == null ? TechnologyCategory.OTHER : request.category());
        technology.setIconUrl(blankToNull(request.iconUrl()));
        technology.setColorHex(blankToNull(request.colorHex()));
        technology.setWebsiteUrl(blankToNull(request.websiteUrl()));
        if (request.displayOrder() != null) {
            technology.setDisplayOrder(request.displayOrder());
        }
    }

    private Technology require(Long id) {
        return technologyRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Technology", id));
    }

    static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    static TechnologyResponse toResponse(Technology technology) {
        return new TechnologyResponse(
                technology.getId(),
                technology.getName(),
                technology.getCategory(),
                technology.getIconUrl(),
                technology.getColorHex(),
                technology.getWebsiteUrl(),
                technology.getDisplayOrder());
    }

    static List<TechnologyResponse> toResponses(Collection<Technology> technologies) {
        return technologies.stream()
                .sorted(Comparator.comparingInt(Technology::getDisplayOrder)
                        .thenComparing(Technology::getName, String.CASE_INSENSITIVE_ORDER))
                .map(TechnologyService::toResponse)
                .toList();
    }
}