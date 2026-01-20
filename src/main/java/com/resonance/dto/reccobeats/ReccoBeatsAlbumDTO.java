package com.resonance.dto.reccobeats;

import lombok.Builder;

@Builder
public record ReccoBeatsAlbumDTO(
    String id,
    String name,
    String href
) {}
