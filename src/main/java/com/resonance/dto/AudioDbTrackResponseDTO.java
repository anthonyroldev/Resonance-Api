package com.resonance.dto;

import java.util.List;

public record AudioDbTrackResponseDTO(
    List<AudioDbTrackDTO> track
) {}
