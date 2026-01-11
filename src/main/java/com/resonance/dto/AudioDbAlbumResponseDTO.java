package com.resonance.dto;

import java.util.List;

public record AudioDbAlbumResponseDTO(
    List<AudioDbAlbumDTO> album
) {}
