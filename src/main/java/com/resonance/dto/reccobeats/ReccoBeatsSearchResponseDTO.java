package com.resonance.dto.reccobeats;

import java.util.List;

public record ReccoBeatsSearchResponseDTO<T>(
    List<T> content,
    Integer page,
    Integer size,
    Integer totalElements,
    Integer totalPages
) {}
