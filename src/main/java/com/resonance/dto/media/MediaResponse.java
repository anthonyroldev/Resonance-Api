package com.resonance.dto.media;

import com.resonance.entities.enums.MediaType;

import lombok.Builder;

@Builder
public record MediaResponse(
    String id,
    String title,
    String artistName,
    String imageUrl,
    String releaseDate,
    MediaType type,
    String itunesUrl,
    Double averageRating,
    Integer ratingCount,
    String description,
    String genre,
    String previewUrl
) {}
