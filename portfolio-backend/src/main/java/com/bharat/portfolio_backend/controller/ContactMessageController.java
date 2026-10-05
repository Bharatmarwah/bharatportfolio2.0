package com.bharat.portfolio_backend.controller;

import com.bharat.portfolio_backend.config.PublicEndpoint;
import com.bharat.portfolio_backend.requestDto.ContactMessageRequest;
import com.bharat.portfolio_backend.responseDto.ContactMessageResponse;
import com.bharat.portfolio_backend.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/contact-messages")
public class ContactMessageController {

    private static final int MAX_PAGE_SIZE = 100;

    private final ContactMessageService contactMessageService;

    public ContactMessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    /**
     * Public contact form. Marked {@link PublicEndpoint} so it works without the
     * admin key. The row is archived first, then the site owner is emailed to the
     * address configured in app.mail.notification-to.
     */
    @PublicEndpoint
    @PostMapping
    public ResponseEntity<ContactMessageResponse> create(@Valid @RequestBody ContactMessageRequest request) {
        ContactMessageResponse created = contactMessageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public Page<ContactMessageResponse> list(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);
        return contactMessageService.findAll(
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @GetMapping("/{id}")
    public ContactMessageResponse getById(@PathVariable Long id) {
        return contactMessageService.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contactMessageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}