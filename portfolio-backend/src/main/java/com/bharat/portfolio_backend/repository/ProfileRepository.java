package com.bharat.portfolio_backend.repository;

import com.bharat.portfolio_backend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findFirstByOrderByIdAsc();

    Optional<Profile> findBySlug(String slug);
}