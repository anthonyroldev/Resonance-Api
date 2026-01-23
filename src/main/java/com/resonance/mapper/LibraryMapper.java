package com.resonance.mapper;

import com.resonance.dto.library.LibraryEntryResponse;
import com.resonance.entities.UserLibraryEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for converting UserLibraryEntry entities to LibraryEntryResponse DTOs.
 */
@Mapper(componentModel = "spring")
public interface LibraryMapper {

    @Mapping(target = "mediaId", source = "media.id")
    @Mapping(target = "mediaTitle", source = "media.title")
    @Mapping(target = "artistName", source = "media.artistName")
    @Mapping(target = "imageUrl", source = "media.imageUrl")
    @Mapping(target = "mediaType", source = "media.type")
    @Mapping(target = "previewUrl", source = "media.previewUrl")
    @Mapping(target = "isFavorite", source = "favorite")
    LibraryEntryResponse toResponse(UserLibraryEntry entry);

    List<LibraryEntryResponse> toResponseList(List<UserLibraryEntry> entries);
}
