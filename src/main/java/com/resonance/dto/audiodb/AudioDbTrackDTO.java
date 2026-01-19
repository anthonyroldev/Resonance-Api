package com.resonance.dto.audiodb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AudioDbTrackDTO(
    @JsonProperty("idTrack") String idTrack,
    @JsonProperty("idAlbum") String idAlbum,
    @JsonProperty("idArtist") String idArtist,
    @JsonProperty("strTrack") String title,
    @JsonProperty("intDuration") String duration,
    @JsonProperty("strGenre") String genre,
    @JsonProperty("intTrackNumber") String trackNumber,
    @JsonProperty("strDescriptionEN") String description,
    @JsonProperty("strTrackThumb") String thumbnailUrl
) {}