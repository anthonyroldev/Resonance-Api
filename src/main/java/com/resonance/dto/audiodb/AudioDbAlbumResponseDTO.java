package com.resonance.dto.audiodb;

import java.util.List;

public record AudioDbAlbumResponseDTO(
    List<AudioDbAlbumDTO> album
) {}
