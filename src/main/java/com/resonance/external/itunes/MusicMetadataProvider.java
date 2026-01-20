package com.resonance.external.itunes;

import com.resonance.dto.media.MediaResponse;

import java.util.List;

/**
 * Interface for music metadata providers.
 * Abstracts the external API used for fetching music catalog data.
 */
public interface MusicMetadataProvider {

    /**
     * Search for albums by query.
     *
     * @param query the search term
     * @return list of matching album responses
     */
    List<MediaResponse> searchAlbums(String query);

    /**
     * Search for tracks by query.
     *
     * @param query the search term
     * @return list of matching track responses
     */
    List<MediaResponse> searchTracks(String query);

    /**
     * Search for artists by query.
     *
     * @param query the search term
     * @return list of matching artist responses
     */
    List<MediaResponse> searchArtists(String query);

    /**
     * Lookup media by its external ID.
     *
     * @param id the external media ID
     * @return the media response, or null if not found
     */
    MediaResponse lookupById(String id);
}
