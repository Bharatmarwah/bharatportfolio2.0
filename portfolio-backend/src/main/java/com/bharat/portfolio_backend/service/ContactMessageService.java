package com.bharat.portfolio_backend.service;

import com.bharat.portfolio_backend.entity.ContactMessage;
import com.bharat.portfolio_backend.exception.ResourceNotFoundException;
import com.bharat.portfolio_backend.repository.ContactMessageRepository;
import com.bharat.portfolio_backend.requestDto.ContactMessageRequest;
import com.bharat.portfolio_backend.responseDto.ContactMessageResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ContactMessageService(ContactMessageRepository contactMessageRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.contactMessageRepository = contactMessageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ContactMessageResponse create(ContactMessageRequest request) {
        ContactMessage message = new ContactMessage();
        message.setName(request.name().trim());
        message.setEmail(request.email().trim());
        message.setSubject(TechnologyService.blankToNull(request.subject()));
        message.setMessage(request.message());
        ContactMessage saved = contactMessageRepository.save(message);

        // Delivered after commit by ContactMessageNotificationListener; a mail
        // failure must not roll back the archived row.
        eventPublisher.publishEvent(new ContactMessageReceivedEvent(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getSubject(),
                saved.getMessage()));

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ContactMessageResponse> findAll(Pageable pageable) {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc(pageable).map(ContactMessageService::toResponse);
    }

    @Transactional(readOnly = true)
    public ContactMessageResponse findById(Long id) {
        return toResponse(require(id));
    }

    @Transactional
    public void delete(Long id) {
        contactMessageRepository.delete(require(id));
    }

    private ContactMessage require(Long id) {
        return contactMessageRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("ContactMessage", id));
    }

    static ContactMessageResponse toResponse(ContactMessage message) {
        return new ContactMessageResponse(
                message.getId(),
                message.getName(),
                message.getEmail(),
                message.getSubject(),
                message.getMessage(),
                message.getCreatedAt());
    }
}