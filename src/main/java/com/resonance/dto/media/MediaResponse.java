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
    String spotifyUri,
    Double averageRating,
    Integer ratingCount,
    // Additional metadata (from AudioDB)
    String description,
    String genre,
    String style,
    String mood
) {}
