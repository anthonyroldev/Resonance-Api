package com.resonance.mapper;

import com.resonance.dto.audiodb.AudioDbAlbumDTO;
import com.resonance.dto.audiodb.AudioDbArtistDTO;
import com.resonance.dto.audiodb.AudioDbTrackDTO;
import com.resonance.dto.media.MediaResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for converting AudioDB DTOs to MediaResponse.
 */
@Mapper(componentModel = "spring")
public interface AudioDbMapper {

    @Mapping(target = "id", source = "idArtist")
    @Mapping(target = "title", source = "artistName")
    @Mapping(target = "artistName", source = "artistName")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "type", constant = "ARTIST")
    @Mapping(target = "description", source = "biography")
    @Mapping(target = "genre", source = "genre")
    @Mapping(target = "style", source = "style")
    @Mapping(target = "mood", source = "mood")
    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "spotifyUri", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    MediaResponse artistToResponse(AudioDbArtistDTO dto);

    List<MediaResponse> artistsToResponses(List<AudioDbArtistDTO> dtos);

    @Mapping(target = "id", source = "idAlbum")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "artistName", source = "artistName")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "releaseDate", source = "yearReleased")
    @Mapping(target = "type", constant = "ALBUM")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "genre", source = "genre")
    @Mapping(target = "style", source = "style")
    @Mapping(target = "mood", source = "mood")
    @Mapping(target = "spotifyUri", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    MediaResponse albumToResponse(AudioDbAlbumDTO dto);

    List<MediaResponse> albumsToResponses(List<AudioDbAlbumDTO> dtos);

    @Mapping(target = "id", source = "idTrack")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "imageUrl", source = "thumbnailUrl")
    @Mapping(target = "type", constant = "TRACK")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "genre", source = "genre")
    @Mapping(target = "artistName", ignore = true)
    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "spotifyUri", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "style", ignore = true)
    @Mapping(target = "mood", ignore = true)
    MediaResponse trackToResponse(AudioDbTrackDTO dto);

    List<MediaResponse> tracksToResponses(List<AudioDbTrackDTO> dtos);
}
