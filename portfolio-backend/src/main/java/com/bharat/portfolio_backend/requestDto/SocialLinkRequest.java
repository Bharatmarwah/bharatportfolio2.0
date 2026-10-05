package com.bharat.portfolio_backend.requestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SocialLinkRequest(

        @Email @Size(max = 120) String email,

        @Pattern(regexp = "^$|^[+]?[0-9\\-\\s()]{7,20}$",
                message = "number must be a valid phone number")
        @Size(max = 20) String number,

        @Size(max = 255) String linkedInUrl,

        @Size(max = 255) String githubUrl,

        @Size(max = 255) String twitterUrl,

        @Size(max = 255) String youtubeUrl,

        @Size(max = 255) String instagramUrl
) {
}