package com.resonance.dto;

import java.util.List;

public record AudioDbArtistResponseDTO(
    List<AudioDbArtistDTO> artists
) {}
