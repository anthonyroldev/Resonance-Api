package com.resonance.entities.media;

import com.resonance.entities.Media;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("TRACK")
public class Track extends Media {

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "genre")
    private String genre;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "track_number")
    private Integer trackNumber;

    @Column(name = "thumbnail_url", length = 512)
    private String thumbnailUrl;

    @Column(name = "preview_url", length = 512)
    private String previewUrl;
}
