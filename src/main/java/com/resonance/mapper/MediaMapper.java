package com.resonance.mapper;

import com.resonance.dto.audiodb.AudioDbTrackDTO;
import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.reccobeats.ReccoBeatsAlbumDTO;
import com.resonance.dto.reccobeats.ReccoBeatsArtistDTO;
import com.resonance.entities.media.Album;
import com.resonance.entities.media.Artist;
import com.resonance.entities.media.Track;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * MapStruct mapper for converting media entities and DTOs to MediaResponse.
 */
@Mapper(componentModel = "spring")
public interface MediaMapper {

    // ==================== Entity to Response mappings ====================

    @Mapping(target = "type", constant = "ALBUM")
    MediaResponse albumToResponse(Album album);

    @Mapping(target = "type", constant = "ARTIST")
    @Mapping(target = "releaseDate", ignore = true)
    MediaResponse artistToResponse(Artist artist);

    @Mapping(target = "type", constant = "TRACK")
    @Mapping(target = "imageUrl", source = "thumbnailUrl")
    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "style", ignore = true)
    @Mapping(target = "mood", ignore = true)
    MediaResponse trackToResponse(Track track);

    // ==================== ReccoBeats DTO to Response mappings ====================

    @Mapping(target = "id", source = "href", qualifiedByName = "extractSpotifyId")
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
    MediaResponse reccoBeatsAlbumToResponse(ReccoBeatsAlbumDTO dto);

    @Mapping(target = "id", source = "href", qualifiedByName = "extractSpotifyId")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "spotifyUri", source = "href")
    @Mapping(target = "type", constant = "ARTIST")
    @Mapping(target = "artistName", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "style", ignore = true)
    @Mapping(target = "mood", ignore = true)
    MediaResponse reccoBeatsArtistToResponse(ReccoBeatsArtistDTO dto);

    List<MediaResponse> reccoBeatsAlbumsToResponses(List<ReccoBeatsAlbumDTO> dtos);

    List<MediaResponse> reccoBeatsArtistsToResponses(List<ReccoBeatsArtistDTO> dtos);

    // ==================== AudioDB DTO to Response mappings ====================

    @Mapping(target = "id", source = "idTrack")
    @Mapping(target = "imageUrl", source = "thumbnailUrl")
    @Mapping(target = "type", constant = "TRACK")
    @Mapping(target = "artistName", ignore = true)
    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "spotifyUri", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "style", ignore = true)
    @Mapping(target = "mood", ignore = true)
    MediaResponse audioDbTrackToResponse(AudioDbTrackDTO dto);

    List<MediaResponse> audioDbTracksToResponses(List<AudioDbTrackDTO> dtos);

    // ==================== Helper methods ====================

    @Named("extractSpotifyId")
    default String extractSpotifyId(String href) {
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
