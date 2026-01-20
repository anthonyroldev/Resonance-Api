package com.resonance.external.itunes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a single result from the iTunes Search API.
 * Maps the relevant fields from the JSON response for album/music content.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ITunesResult(
        String wrapperType,
        String kind,
        Long collectionId,
        Long artistId,
        Long trackId,
        String collectionName,
        String artistName,
        String trackName,
        String artworkUrl100,
        String artworkUrl60,
        String releaseDate,
        String collectionViewUrl,
        String artistViewUrl,
        String trackViewUrl,
        String primaryGenreName,
        String collectionExplicitness,
        Integer trackCount,
        String country,
        String currency,
        Double collectionPrice,
        Double trackPrice,
        Long trackTimeMillis,
        Integer discCount,
        Integer discNumber,
        Integer trackNumber
) {
    /**
     * Checks if this result represents a collection (album).
     */
    public boolean isCollection() {
        return "collection".equals(wrapperType);
    }

    /**
     * Checks if this result represents a track (song).
     */
    public boolean isTrack() {
        return "track".equals(wrapperType);
    }

    /**
     * Checks if this result represents an artist.
     */
    public boolean isArtist() {
        return "artist".equals(wrapperType);
    }
}
