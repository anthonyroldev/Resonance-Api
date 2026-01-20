package com.resonance.mapper;

import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.reccobeats.ReccoBeatsAlbumDTO;
import com.resonance.dto.reccobeats.ReccoBeatsArtistDTO;
import com.resonance.dto.reccobeats.ReccoBeatsTrackDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for converting ReccoBeats DTOs to MediaResponse.
 */
@Mapper(componentModel = "spring")
public interface ReccoBeatsMapper {

    @Mapping(target = "title", source = "name")
    @Mapping(target = "spotifyUri", source = "href")
    @Mapping(target = "type", constant = "ARTIST")
    @Mapping(target = "artistName", source = "name")
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "style", ignore = true)
    @Mapping(target = "mood", ignore = true)
    MediaResponse artistToResponse(ReccoBeatsArtistDTO dto);

    List<MediaResponse> artistsToResponses(List<ReccoBeatsArtistDTO> dtos);

    // ==================== Album mappings ====================

    @Mapping(target = "title", source = "name")
    @Mapping(target = "spotifyUri", source = "href")
    @Mapping(target = "type", constant = "ALBUM")
    @Mapping(target = "artistName", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "style", ignore = true)
    @Mapping(target = "mood", ignore = true)
    MediaResponse albumToResponse(ReccoBeatsAlbumDTO dto);

    List<MediaResponse> albumsToResponses(List<ReccoBeatsAlbumDTO> dtos);

    // ==================== Track mappings ====================

    @Mapping(target = "title", source = "name")
    @Mapping(target = "spotifyUri", source = "href")
    @Mapping(target = "type", constant = "TRACK")
    @Mapping(target = "artistName", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "style", ignore = true)
    @Mapping(target = "mood", ignore = true)
    MediaResponse trackToResponse(ReccoBeatsTrackDTO dto);

    List<MediaResponse> tracksToResponses(List<ReccoBeatsTrackDTO> dtos);

}
