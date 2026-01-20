package com.resonance.mapper;

import com.resonance.dto.reccobeats.ReccoBeatsAlbumDTO;
import com.resonance.dto.reccobeats.ReccoBeatsArtistDTO;
import com.resonance.dto.reccobeats.ReccoBeatsTrackDTO;
import com.resonance.dto.unified.UnifiedMediaDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReccoBeatsMapper {

    @Mapping(target = "spotifyId", source = "href", qualifiedByName = "extractSpotifyIdFromUrl")
    @Mapping(target = "source", constant = "RECCOBEATS")
    @Mapping(target = "type", constant = "ARTIST")
    UnifiedMediaDTO toUnifiedMedia(ReccoBeatsArtistDTO dto);

    @Mapping(target = "spotifyId", source = "href", qualifiedByName = "extractSpotifyIdFromUrl")
    @Mapping(target = "source", constant = "RECCOBEATS")
    @Mapping(target = "type", constant = "ALBUM")
    UnifiedMediaDTO toUnifiedMedia(ReccoBeatsAlbumDTO dto);

    @Mapping(target = "spotifyId", source = "href", qualifiedByName = "extractSpotifyIdFromUrl")
    @Mapping(target = "source", constant = "RECCOBEATS")
    @Mapping(target = "type", constant = "TRACK")
    UnifiedMediaDTO toUnifiedMedia(ReccoBeatsTrackDTO dto);

    List<UnifiedMediaDTO> artistsToUnifiedMedia(List<ReccoBeatsArtistDTO> dtos);

    List<UnifiedMediaDTO> albumsToUnifiedMedia(List<ReccoBeatsAlbumDTO> dtos);

    List<UnifiedMediaDTO> tracksToUnifiedMedia(List<ReccoBeatsTrackDTO> dtos);

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
