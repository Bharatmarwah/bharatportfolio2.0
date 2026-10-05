package com.bharat.portfolio_backend.entity;

import com.bharat.portfolio_backend.enums.Visibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "certificates")
public class Certificate extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @NotBlank
    @Size(max = 160)
    @Column(name = "certificate_name", nullable = false, length = 160)
    private String name;

    @NotBlank
    @Size(max = 120)
    @Column(name = "certificate_source_name", nullable = false, length = 120)
    private String sourceName;

    @Column(name = "certificate_source_url", length = 255)
    private String sourceUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Direct download / verification link for the certificate document.
     */
    @Column(name = "certificate_download_url", length = 500)
    private String downloadUrl;

    @Column(name = "credential_id", length = 120)
    private String credentialId;

    @Column(name = "certificate_logo_url", length = 255)
    private String logoUrl;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "has_expiry", nullable = false)
    private boolean hasExpiry = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "certificate_technologies",
            joinColumns = @JoinColumn(name = "certificate_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id"))
    private Set<Technology> technologies = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Visibility visibility = Visibility.PUBLIC;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;
}
