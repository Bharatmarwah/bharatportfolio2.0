package com.bharat.portfolio_backend.repository;

import com.bharat.portfolio_backend.entity.Project;
import com.bharat.portfolio_backend.enums.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Project> findByFeaturedTrueAndVisibilityOrderByDisplayOrderAsc(Visibility visibility);

    List<Project> findByVisibilityOrderByDisplayOrderAsc(Visibility visibility);

    List<Project> findAllByOrderByDisplayOrderAsc();
}