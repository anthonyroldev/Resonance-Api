package com.resonance.dto.library;

import com.resonance.entities.enums.MediaType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record LibraryEntryResponse(
    UUID id,
    String mediaId,
    String mediaTitle,
    String artistName,
    String imageUrl,
    String previewUrl,
    MediaType mediaType,
    Integer rating,
    boolean isFavorite,
    String comment,
    Instant addedAt,
    Instant updatedAt
) {}
