package com.resonance.dto.favorite;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AddToFavoriteRequest(
        @NotBlank(message = "Media ID is required") String mediaId
) {
}
