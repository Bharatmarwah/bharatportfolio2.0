package com.bharat.portfolio_backend.repository;

import com.bharat.portfolio_backend.entity.Technology;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TechnologyRepository extends JpaRepository<Technology, Long> {

    Optional<Technology> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Technology> findByNameInIgnoreCase(Collection<String> names);

    List<Technology> findAllByOrderByNameAsc();
}