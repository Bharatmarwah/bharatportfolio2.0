package com.bharat.portfolio_backend.repository;

import com.bharat.portfolio_backend.entity.Certificate;
import com.bharat.portfolio_backend.enums.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByCredentialId(String credentialId);

    List<Certificate> findByVisibilityOrderByDisplayOrderAsc(Visibility visibility);

    List<Certificate> findAllByOrderByDisplayOrderAsc();
}