package com.resonance.entities;

import com.resonance.entities.enums.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "media")
public class Media {
    // Spotify id
    @Id
    @Column(length = 62)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "release_date")
    private String releaseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType type;

    @Column(name = "spotify_uri", nullable = false, length = 100)
    private String spotifyUri;

    @Column(name = "average_rating", columnDefinition = "NUMERIC(3, 2)")
    private Double averageRating;

    @Column(name = "rating_count", nullable = false)
    @Builder.Default
    private Integer ratingCount = 0;

    @Column(name = "cached_at", nullable = false)
    private Instant cachedAt = Instant.now();
}


