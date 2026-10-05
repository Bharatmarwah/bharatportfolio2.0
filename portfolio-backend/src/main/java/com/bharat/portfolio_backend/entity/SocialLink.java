package com.bharat.portfolio_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "social_links")
public class SocialLink extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    @Email
    @Column(length = 120)
    private String email;

    @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,20}$",
            message = "number must be a valid phone number")
    @Column(name = "phone_number", length = 20)
    private String number;

    @Column(name = "linkedin_url", length = 255)
    private String linkedInUrl;

    @Column(name = "github_url", length = 255)
    private String githubUrl;

    @Column(name = "twitter_url", length = 255)
    private String twitterUrl;

    @Column(name = "youtube_url", length = 255)
    private String youtubeUrl;

    @Column(name = "instagram_url", length = 255)
    private String instagramUrl;
}
