package com.resonance.dto.audiodb;

import java.util.List;

public record AudioDbTrackResponseDTO(
    List<AudioDbTrackDTO> track
) {}
