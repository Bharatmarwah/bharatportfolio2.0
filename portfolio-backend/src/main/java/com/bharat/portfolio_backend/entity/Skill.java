package com.bharat.portfolio_backend.entity;

import com.bharat.portfolio_backend.enums.ProficiencyLevel;
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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A grouping of related technologies, e.g. "Backend" or "Data Engineering".
 * Replaces the earlier tech-name-to-url Map with a shared Technology catalogue.
 */
@Getter
@Setter
@Entity
@Table(name = "skills")
public class Skill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String domain;

    @Size(max = 255)
    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency_level", nullable = false, length = 20)
    private ProficiencyLevel proficiencyLevel = ProficiencyLevel.INTERMEDIATE;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "skill_technologies",
            joinColumns = @JoinColumn(name = "skill_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id"))
    @OrderBy("displayOrder ASC")
    private Set<Technology> technologies = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Visibility visibility = Visibility.PUBLIC;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;
}
