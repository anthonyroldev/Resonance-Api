package com.resonance.dto.library;

import com.resonance.entities.enums.MediaType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;

@Builder
public record AddToLibraryRequest(
    @NotBlank(message = "Media ID (Spotify ID) is required")
    String mediaId,

    @NotNull(message = "Media type is required")
    MediaType mediaType,

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating must be at most 10")
    Integer rating,

    Boolean isFavorite,

    String comment
) {}
