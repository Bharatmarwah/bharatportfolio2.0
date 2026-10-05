package com.bharat.portfolio_backend.repository;

import com.bharat.portfolio_backend.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    Page<ContactMessage> findAllByOrderByCreatedAtDesc(Pageable pageable);
}