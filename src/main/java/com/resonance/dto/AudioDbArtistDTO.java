package com.resonance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AudioDbArtistDTO(
    @JsonProperty("idArtist") String idArtist,
    @JsonProperty("strArtist") String artistName,
    @JsonProperty("intFormedYear") String formedYear,
    @JsonProperty("strStyle") String style,
    @JsonProperty("strGenre") String genre,
    @JsonProperty("strMood") String mood,
    @JsonProperty("strBiographyEN") String biography,
    @JsonProperty("strGender") String gender,
    @JsonProperty("strCountry") String country,
    @JsonProperty("strArtistThumb") String imageUrl
) {}
