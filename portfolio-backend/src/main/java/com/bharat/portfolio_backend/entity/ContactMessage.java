package com.bharat.portfolio_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Archive of messages submitted through the public contact form.
 * Intentionally not mapped on Profile - messages are not owned by it. The owner's
 * Gmail inbox is the working inbox; this table is only a durable backup, so it
 * carries no read/reply bookkeeping.
 */
@Getter
@Setter
@Entity
@Table(name = "contact_messages")
public class ContactMessage extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String email;

    @Size(max = 160)
    @Column(length = 160)
    private String subject;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

}
