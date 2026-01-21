package com.resonance.external.itunes;

import com.resonance.entities.media.Album;
import com.resonance.entities.media.Artist;
import com.resonance.entities.media.Track;

import org.springframework.stereotype.Component;

/**
 * Mapper that converts iTunes API results to JPA entities for database persistence.
 * <p>
 * This mapper is used by the lazy caching strategy to persist full metadata
 * from iTunes into the local database.
 */
@Component
public final class ITunesEntityMapper {

    private static final String ARTWORK_SIZE_SMALL = "100x100bb";
    private static final String ARTWORK_SIZE_LARGE = "600x600bb";

    /**
     * Converts an ITunesResult (album/collection) to an Album entity.
     *
     * @param result the iTunes result to convert
     * @return Album entity ready for persistence, or null if result is null
     */
    public Album toAlbumEntity(ITunesResult result) {
        if (result == null || result.collectionId() == null) {
            return null;
        }

        return Album.builder()
                .id(String.valueOf(result.collectionId()))
                .title(result.collectionName() != null ? result.collectionName() : "Unknown Album")
                .artistName(result.artistName() != null ? result.artistName() : "Unknown Artist")
                .imageUrl(upgradeArtworkUrl(result.artworkUrl100()))
                .releaseDate(result.releaseDate())
                .itunesUrl(result.collectionViewUrl())
                .genre(result.primaryGenreName())
                .label(result.copyright())
                .build();
    }

    /**
     * Converts an ITunesResult (track/song) to a Track entity.
     *
     * @param result the iTunes result to convert
     * @return Track entity ready for persistence, or null if result is null
     */
    public Track toTrackEntity(ITunesResult result) {
        if (result == null || result.trackId() == null) {
            return null;
        }

        Integer durationMs = result.trackTimeMillis() != null
                ? result.trackTimeMillis().intValue()
                : null;

        return Track.builder()
                .id(String.valueOf(result.trackId()))
                .title(result.trackName() != null ? result.trackName() : "Unknown Track")
                .artistName(result.artistName() != null ? result.artistName() : "Unknown Artist")
                .imageUrl(upgradeArtworkUrl(result.artworkUrl100()))
                .thumbnailUrl(result.artworkUrl60())
                .releaseDate(result.releaseDate())
                .itunesUrl(result.trackViewUrl())
                .genre(result.primaryGenreName())
                .duration(durationMs)
                .trackNumber(result.trackNumber())
                .previewUrl(result.previewUrl())
                .build();
    }

    /**
     * Converts an ITunesResult (artist) to an Artist entity.
     *
     * @param result the iTunes result to convert
     * @return Artist entity ready for persistence, or null if result is null
     */
    public Artist toArtistEntity(ITunesResult result) {
        if (result == null || result.artistId() == null) {
            return null;
        }

        return Artist.builder()
                .id(String.valueOf(result.artistId()))
                .title(result.artistName() != null ? result.artistName() : "Unknown Artist")
                .artistName(result.artistName() != null ? result.artistName() : "Unknown Artist")
                .itunesUrl(result.artistLinkUrl())
                .genre(result.primaryGenreName())
                .build();
    }

    /**
     * Upgrades artwork URL from 100x100 to 600x600 for higher resolution.
     *
     * @param artworkUrl the original artwork URL
     * @return upgraded URL with higher resolution, or null if input is null/blank
     */
    private String upgradeArtworkUrl(String artworkUrl) {
        if (artworkUrl == null || artworkUrl.isBlank()) {
            return null;
        }
        return artworkUrl.replace(ARTWORK_SIZE_SMALL, ARTWORK_SIZE_LARGE);
    }
}
