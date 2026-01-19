package com.resonance.dto.audiodb;

import java.util.List;

public record AudioDbArtistResponseDTO(
    List<AudioDbArtistDTO> artists
) {}
