package com.bharat.portfolio_backend.entity;

import com.bharat.portfolio_backend.enums.ProjectStatus;
import com.bharat.portfolio_backend.enums.Visibility;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "projects")
public class Project extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @NotBlank
    @Size(max = 120)
    @Column(name = "project_name", nullable = false, length = 120)
    private String name;

    @NotBlank
    @Size(max = 120)
    @Column(name = "project_slug", nullable = false, unique = true, length = 120)
    private String slug;

    @NotBlank
    @Size(max = 180)
    @Column(name = "project_headline", nullable = false, length = 180)
    private String headline;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Size(max = 120)
    @Column(name = "role", length = 120)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.COMPLETED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Visibility visibility = Visibility.PUBLIC;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_technologies",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id"))
    private Set<Technology> technologies = new LinkedHashSet<>();

    @ElementCollection
    @CollectionTable(name = "project_highlights", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "highlight", columnDefinition = "TEXT")
    @OrderColumn(name = "display_order")
    private List<String> highlights = new ArrayList<>();

    @Column(name = "github_url", length = 255)
    private String githubUrl;

    @Column(name = "live_url", length = 255)
    private String liveUrl;

    @Column(name = "demo_video_url", length = 255)
    private String demoVideoUrl;

    @ElementCollection
    @CollectionTable(name = "project_images", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "image_url", length = 500)
    @OrderColumn(name = "display_order")
    private List<String> imagesUrl = new ArrayList<>();

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "ongoing", nullable = false)
    private boolean ongoing;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;
}
