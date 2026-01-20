package com.resonance.dto.unified;

import com.resonance.entities.enums.MediaSource;
import com.resonance.entities.enums.MediaType;
import lombok.Builder;

/**
 * Unified DTO for basic media information.
 * Can represent data from any source (ReccoBeats, AudioDB, Spotify).
 */
@Builder
public record UnifiedMediaDTO(
    String id,
    String spotifyId,
    String name,
    String href,
    MediaSource source,
    MediaType type
) {
}
