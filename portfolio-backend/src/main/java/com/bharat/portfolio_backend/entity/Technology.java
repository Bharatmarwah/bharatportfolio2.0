package com.bharat.portfolio_backend.entity;

import com.bharat.portfolio_backend.enums.TechnologyCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Catalogue row shared by projects, skills, certificates and experience
 * so the frontend can render icons and colours from one place.
 */
@Getter
@Setter
@Entity
@Table(name = "technologies")
public class Technology extends BaseEntity {

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, unique = true, length = 60)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private TechnologyCategory category = TechnologyCategory.OTHER;

    @Size(max = 255)
    @Column(name = "icon_url")
    private String iconUrl;

    @Pattern(regexp = "^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$",
            message = "colorHex must be a hex value such as #61DAFB")
    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "display_order", nullable = false, columnDefinition = "integer not null default 0")
    private int displayOrder = 0;
}
