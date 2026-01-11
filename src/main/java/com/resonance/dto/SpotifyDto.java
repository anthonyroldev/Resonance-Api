package com.resonance.dto;

import lombok.Builder;

@Builder
public record SpotifyDto(
        String id,
        String name,
        String releaseDate,
        SpotifyArtistDTO[] artists,
        SpotifyImageDTO[] images,
        String uri
) {
}

