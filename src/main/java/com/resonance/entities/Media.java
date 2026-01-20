package com.resonance.entities;

import com.resonance.entities.enums.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "media")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Media {
    /**
     * iTunes collection/track/artist ID
     */
    @Id
    @Column(length = 62)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(name = "release_date")
    private String releaseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, insertable = false, updatable = false)
    private MediaType type;

    @Column(name = "itunes_url", length = 512)
    private String itunesUrl;

    @Column(name = "average_rating", columnDefinition = "NUMERIC(3, 2)")
    private Double averageRating;

    @Column(name = "rating_count", nullable = false)
    @Builder.Default
    private Integer ratingCount = 0;

    @Column(name = "cached_at", nullable = false)
    @Builder.Default
    private Instant cachedAt = Instant.now();
}
