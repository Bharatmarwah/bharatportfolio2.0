package com.bharat.portfolio_backend.requestDto;

import com.bharat.portfolio_backend.enums.TechnologyCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TechnologyRequest(

        @NotBlank @Size(max = 60) String name,

        TechnologyCategory category,

        @Size(max = 255) String iconUrl,

        @Pattern(regexp = "^$|^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$",
                message = "colorHex must be a hex value such as #61DAFB")
        @Size(max = 7) String colorHex,

        @Size(max = 255) String websiteUrl,

        @Min(0) Integer displayOrder
) {
}