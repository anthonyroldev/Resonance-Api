package com.resonance.dto.reccobeats;

import lombok.Builder;

@Builder
public record ReccoBeatsTrackDTO(
    String id,
    String name,
    String href
) {}
