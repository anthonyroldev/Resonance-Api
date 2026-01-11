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
@DiscriminatorValue("ARTIST")
public class Artist extends Media {

    @Column(name = "artist_audio_db_id", length = 20)
    private String audioDbArtistId;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "genre")
    private String genre;

    @Column(name = "style")
    private String style;

    @Column(name = "mood")
    private String mood;

    @Column(name = "gender")
    private String gender;

    @Column(name = "country")
    private String country;

    @Column(name = "formed_year")
    private String formedYear;
}
