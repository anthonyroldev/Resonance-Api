package com.resonance.dto.reccobeats;

import lombok.Builder;

@Builder
public record ReccoBeatsArtistDTO(
    String id,
    String name,
    String href
) {}
