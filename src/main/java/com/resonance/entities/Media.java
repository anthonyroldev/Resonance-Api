package com.resonance.entities;

import com.resonance.entities.enums.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "media")
public class Media {
    // Spotify id
    @Id
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

    @Column(name = "spotify_uri", nullable = false)
    private String spotifyUri;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @Column(name = "cached_at", nullable = false)
    private Instant cachedAt;

    @PrePersist
    protected void onCreate() {
        if (this.cachedAt == null) {
            this.cachedAt = Instant.now();
        }
        if (this.ratingCount == null) {
            this.ratingCount = 0;
        }
    }
}


