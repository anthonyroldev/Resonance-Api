package com.resonance.dto.spotify;

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

