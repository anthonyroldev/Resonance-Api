package com.resonance.mapper;

import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.reccobeats.ReccoBeatsAlbumDTO;
import com.resonance.dto.reccobeats.ReccoBeatsArtistDTO;
import com.resonance.dto.reccobeats.ReccoBeatsTrackDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * MapStruct mapper for converting ReccoBeats DTOs to MediaResponse.
 */
@Mapper(componentModel = "spring")
public interface ReccoBeatsMapper {

    // ==================== Artist mappings ====================

    @Mapping(target = "id", source = "href", qualifiedByName = "extractSpotifyIdFromUrl")
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

    @Mapping(target = "id", source = "href", qualifiedByName = "extractSpotifyIdFromUrl")
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

    @Mapping(target = "id", source = "href", qualifiedByName = "extractSpotifyIdFromUrl")
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

    // ==================== Helper methods ====================

    /**
     * Extracts the Spotify ID from a Spotify URL.
     * Example: "https://api.spotify.com/v1/artists/0f3C3g5HiVbe2znBvdEtno" -> "0f3C3g5HiVbe2znBvdEtno"
     */
    @Named("extractSpotifyIdFromUrl")
    default String extractSpotifyIdFromUrl(String href) {
        if (href == null || href.isBlank()) {
            return null;
        }
        int lastSlash = href.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < href.length() - 1) {
            return href.substring(lastSlash + 1);
        }
        return null;
    }
}
