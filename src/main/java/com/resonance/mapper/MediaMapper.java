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
 * This mapper handles Entity -> Response mappings.
 * For external API DTOs, use ITunesMediaMapper.
 */
@Mapper(componentModel = "spring")
public interface MediaMapper {

    // ==================== Entity to Response mappings ====================

    @Mapping(target = "type", constant = "ALBUM")
    @Mapping(target = "previewUrl", ignore = true)
    MediaResponse albumToResponse(Album album);

    @Mapping(target = "type", constant = "ARTIST")
    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "previewUrl", ignore = true)
    MediaResponse artistToResponse(Artist artist);

    @Mapping(target = "type", constant = "TRACK")
    @Mapping(target = "imageUrl", source = "imageUrl")
    MediaResponse trackToResponse(Track track);
}
