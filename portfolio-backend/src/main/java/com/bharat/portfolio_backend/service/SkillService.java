package com.bharat.portfolio_backend.service;

import com.bharat.portfolio_backend.entity.Skill;
import com.bharat.portfolio_backend.enums.ProficiencyLevel;
import com.bharat.portfolio_backend.enums.Visibility;
import com.bharat.portfolio_backend.exception.DuplicateResourceException;
import com.bharat.portfolio_backend.exception.ResourceNotFoundException;
import com.bharat.portfolio_backend.repository.SkillRepository;
import com.bharat.portfolio_backend.requestDto.SkillRequest;
import com.bharat.portfolio_backend.responseDto.SkillResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final ProfileService profileService;
    private final TechnologyService technologyService;

    public SkillService(SkillRepository skillRepository,
                        ProfileService profileService,
                        TechnologyService technologyService) {
        this.skillRepository = skillRepository;
        this.profileService = profileService;
        this.technologyService = technologyService;
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> findAll(boolean includeHidden) {
        List<Skill> skills = includeHidden
                ? skillRepository.findAllByOrderByDisplayOrderAsc()
                : skillRepository.findByVisibilityOrderByDisplayOrderAsc(Visibility.PUBLIC);
        return skills.stream().map(SkillService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SkillResponse findById(Long id) {
        return toResponse(require(id));
    }

    @Transactional
    public SkillResponse create(SkillRequest request) {
        String domain = request.domain().trim();
        if (skillRepository.existsByDomain(domain)) {
            throw DuplicateResourceException.of("skill domain", domain);
        }
        Skill skill = new Skill();
        skill.setProfile(profileService.requireProfile());
        skill.setDomain(domain);
        apply(skill, request);
        return toResponse(skillRepository.save(skill));
    }

    @Transactional
    public SkillResponse update(Long id, SkillRequest request) {
        Skill skill = require(id);
        String domain = request.domain().trim();
        if (!domain.equalsIgnoreCase(skill.getDomain()) && skillRepository.existsByDomain(domain)) {
            throw DuplicateResourceException.of("skill domain", domain);
        }
        skill.setDomain(domain);
        apply(skill, request);
        return toResponse(skillRepository.save(skill));
    }

    @Transactional
    public void delete(Long id) {
        skillRepository.delete(require(id));
    }

    private void apply(Skill skill, SkillRequest request) {
        skill.setDescription(TechnologyService.blankToNull(request.description()));
        skill.setProficiencyLevel(request.proficiencyLevel() == null
                ? ProficiencyLevel.INTERMEDIATE
                : request.proficiencyLevel());
        skill.setVisibility(request.visibility() == null ? Visibility.PUBLIC : request.visibility());
        skill.setDisplayOrder(request.displayOrder() == null ? 0 : request.displayOrder());
        skill.setTechnologies(technologyService.resolveByNames(request.technologies()));
    }

    private Skill require(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Skill", id));
    }

    static SkillResponse toResponse(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getDomain(),
                skill.getDescription(),
                skill.getProficiencyLevel(),
                skill.getVisibility(),
                skill.getDisplayOrder(),
                TechnologyService.toResponses(skill.getTechnologies()));
    }
}