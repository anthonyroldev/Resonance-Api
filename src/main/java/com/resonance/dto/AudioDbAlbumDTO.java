package com.resonance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AudioDbAlbumDTO(
    @JsonProperty("idAlbum") String idAlbum,
    @JsonProperty("idArtist") String idArtist,
    @JsonProperty("strAlbum") String title,
    @JsonProperty("strArtist") String artistName,
    @JsonProperty("intYearReleased") String yearReleased,
    @JsonProperty("strStyle") String style,
    @JsonProperty("strGenre") String genre,
    @JsonProperty("strLabel") String label,
    @JsonProperty("strAlbumThumb") String imageUrl,
    @JsonProperty("strDescriptionEN") String description,
    @JsonProperty("strMood") String mood
) {}