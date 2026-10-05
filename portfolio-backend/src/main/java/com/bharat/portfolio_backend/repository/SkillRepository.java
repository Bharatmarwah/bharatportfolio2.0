package com.bharat.portfolio_backend.repository;

import com.bharat.portfolio_backend.entity.Skill;
import com.bharat.portfolio_backend.enums.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByDomain(String domain);

    boolean existsByDomain(String domain);

    List<Skill> findByVisibilityOrderByDisplayOrderAsc(Visibility visibility);

    List<Skill> findAllByOrderByDisplayOrderAsc();
}