package com.resonance.external.itunes;

import com.resonance.dto.media.MediaResponse;
import com.resonance.entities.enums.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Mapper that converts iTunes API results to domain MediaResponse DTOs.
 */
@Component
public final class ITunesMediaMapper {

    private static final String ARTWORK_SIZE_SMALL = "100x100bb";
    private static final String ARTWORK_SIZE_LARGE = "600x600bb";

    /**
     * Converts an ITunesResult (album/collection) to a MediaResponse.
     *
     * @param result the iTunes result to convert
     * @return MediaResponse representation of the album
     */
    public MediaResponse toAlbumResponse(ITunesResult result) {
        if (result == null) {
            return null;
        }

        return MediaResponse.builder()
                .id(result.collectionId() != null ? String.valueOf(result.collectionId()) : null)
                .title(result.collectionName())
                .artistName(result.artistName())
                .imageUrl(upgradeArtworkUrl(result.artworkUrl100()))
                .releaseDate(result.releaseDate())
                .type(MediaType.ALBUM)
                .itunesUrl(result.collectionViewUrl())
                .genre(result.primaryGenreName())
                .build();
    }

    /**
     * Converts an ITunesResult (track/song) to a MediaResponse.
     *
     * @param result the iTunes result to convert
     * @return MediaResponse representation of the track
     */
    public MediaResponse toTrackResponse(ITunesResult result) {
        if (result == null) {
            return null;
        }

        return MediaResponse.builder()
                .id(result.trackId() != null ? String.valueOf(result.trackId()) : null)
                .title(result.trackName())
                .artistName(result.artistName())
                .imageUrl(upgradeArtworkUrl(result.artworkUrl100()))
                .releaseDate(result.releaseDate())
                .type(MediaType.TRACK)
                .itunesUrl(result.trackViewUrl())
                .genre(result.primaryGenreName())
                .previewUrl(result.previewUrl())
                .build();
    }

    /**
     * Converts an ITunesResult (artist) to a MediaResponse.
     *
     * @param result the iTunes result to convert
     * @return MediaResponse representation of the artist
     */
    public MediaResponse toArtistResponse(ITunesResult result) {
        if (result == null) {
            return null;
        }

        return MediaResponse.builder()
                .id(result.artistId() != null ? String.valueOf(result.artistId()) : null)
                .title(result.artistName())
                .artistName(result.artistName())
                .imageUrl(null) // Artists don't have artwork in iTunes Search API
                .type(MediaType.ARTIST)
                .itunesUrl(result.artistLinkUrl())
                .genre(result.primaryGenreName())
                .build();
    }

    /**
     * Converts a list of ITunesResult (albums) to MediaResponse list.
     *
     * @param results list of iTunes results
     * @return list of MediaResponse DTOs
     */
    public List<MediaResponse> toAlbumResponses(List<ITunesResult> results) {
        if (results == null) {
            return List.of();
        }
        return results.stream()
                .filter(Objects::nonNull)
                .map(this::toAlbumResponse)
                .toList();
    }

    /**
     * Converts a list of ITunesResult (tracks) to MediaResponse list.
     *
     * @param results list of iTunes results
     * @return list of MediaResponse DTOs
     */
    public List<MediaResponse> toTrackResponses(List<ITunesResult> results) {
        if (results == null) {
            return List.of();
        }
        return results.stream()
                .filter(Objects::nonNull)
                .map(this::toTrackResponse)
                .toList();
    }

    /**
     * Converts a list of ITunesResult (artists) to MediaResponse list.
     *
     * @param results list of iTunes results
     * @return list of MediaResponse DTOs
     */
    public List<MediaResponse> toArtistResponses(List<ITunesResult> results) {
        if (results == null) {
            return List.of();
        }
        return results.stream()
                .filter(Objects::nonNull)
                .map(this::toArtistResponse)
                .toList();
    }

    /**
     * Upgrades artwork URL from 100x100 to 600x600 for higher resolution.
     *
     * @param artworkUrl the original artwork URL
     * @return upgraded URL with higher resolution, or original if upgrade not possible
     */
    private String upgradeArtworkUrl(String artworkUrl) {
        if (artworkUrl == null || artworkUrl.isBlank()) {
            return null;
        }
        return artworkUrl.replace(ARTWORK_SIZE_SMALL, ARTWORK_SIZE_LARGE);
    }
}
