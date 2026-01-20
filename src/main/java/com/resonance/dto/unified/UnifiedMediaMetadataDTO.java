package com.resonance.dto.unified;

import com.resonance.entities.enums.MediaSource;
import lombok.Builder;

/**
 * Unified DTO for media metadata (mood, genre, style, etc.).
 * Typically populated from AudioDB which provides rich metadata.
 */
@Builder
public record UnifiedMediaMetadataDTO(
    String mediaId,
    String genre,
    String style,
    String mood,
    String description,
    String imageUrl,
    String yearReleased,
    String country,
    MediaSource source
) {
}