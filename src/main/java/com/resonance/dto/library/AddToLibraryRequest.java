package com.resonance.dto.library;

import com.resonance.entities.enums.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AddToLibraryRequest(
    @NotBlank(message = "Media ID is required")
    String mediaId,

    @NotNull(message = "Media type is required")
    MediaType mediaType,

    String comment
) {}
