package com.bharat.portfolio_backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Root aggregate for the single portfolio owner.
 * Every child entity is mapped back to here, so the whole site
 * can be served from one /v1/me read.
 */
@Getter
@Setter
@Entity
@Table(name = "profile")
public class Profile extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @NotBlank
    @Size(max = 160)
    @Column(nullable = false, length = 160)
    private String headline;

   /*
    * Removed Current Role
    */

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String bio;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String location;

    @Column(name = "profile_url", length = 255)
    private String profileUrl;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "resume_url", length = 255)
    private String resumeUrl;

    @Pattern(regexp = "^https?://.+$", message = "websiteUrl must be a valid URL")
    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @OneToOne(mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private SocialLink socialLink;

    @OneToMany(mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Certificate> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<BlogPost> blogPosts = new ArrayList<>();
}
