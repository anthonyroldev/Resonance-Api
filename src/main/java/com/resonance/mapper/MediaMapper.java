package com.resonance.mapper;

import com.resonance.dto.media.MediaResponse;
import com.resonance.entities.media.Album;
import com.resonance.entities.media.Artist;
import com.resonance.entities.media.Track;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting media entities to MediaResponse.
 * <p>
 * This mapper handles only Entity -> Response mappings.
 * For external API DTOs, use the specialized mappers:
 * - {@link ReccoBeatsMapper} for ReccoBeats/Spotify data
 * - {@link AudioDbMapper} for AudioDB data
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
}
