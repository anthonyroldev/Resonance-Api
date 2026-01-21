package com.resonance.service;

import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.media.SearchResponse;
import com.resonance.entities.Media;
import com.resonance.external.itunes.ITunesClient;
import com.resonance.external.itunes.ITunesResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for searching music metadata via external APIs.
 * <p>
 * Implements eager caching: all search results are persisted to the local
 * database before being returned, building the catalog as users browse.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public final class SearchService {

    private final ITunesClient iTunesClient;
    private final MediaService mediaService;

    /**
     * Search albums by query.
     * <p>
     * Fetches results from iTunes, eagerly caches them to the database,
     * and returns the persisted entities as DTOs.
     *
     * @param query the search term
     * @return paginated search response containing album results
     */
    public SearchResponse<MediaResponse> searchAlbums(String query) {
        log.debug("Searching albums with query: {}", query);

        // Fetch raw results from iTunes
        ITunesResponse response = iTunesClient.searchAlbums(query);

        // Sync to DB (eager caching) and convert to DTOs
        List<Media> syncedMedia = mediaService.syncAlbums(response.results());
        List<MediaResponse> results = mediaService.toMediaResponses(syncedMedia);

        return buildSearchResponse(results);
    }

    /**
     * Search artists by query.
     * <p>
     * Fetches results from iTunes, eagerly caches them to the database,
     * and returns the persisted entities as DTOs.
     *
     * @param query the search term
     * @return paginated search response containing artist results
     */
    public SearchResponse<MediaResponse> searchArtists(String query) {
        log.debug("Searching artists with query: {}", query);

        // Fetch raw results from iTunes
        ITunesResponse response = iTunesClient.searchArtists(query);

        // Sync to DB (eager caching) and convert to DTOs
        List<Media> syncedMedia = mediaService.syncArtists(response.results());
        List<MediaResponse> results = mediaService.toMediaResponses(syncedMedia);

        return buildSearchResponse(results);
    }

    /**
     * Search tracks by query.
     * <p>
     * Fetches results from iTunes, eagerly caches them to the database,
     * and returns the persisted entities as DTOs.
     *
     * @param query the search term
     * @return paginated search response containing track results
     */
    public SearchResponse<MediaResponse> searchTracks(String query) {
        log.debug("Searching tracks with query: {}", query);

        // Fetch raw results from iTunes
        ITunesResponse response = iTunesClient.searchTracks(query);

        // Sync to DB (eager caching) and convert to DTOs
        List<Media> syncedMedia = mediaService.syncTracks(response.results());
        List<MediaResponse> results = mediaService.toMediaResponses(syncedMedia);

        return buildSearchResponse(results);
    }

    /**
     * Build a SearchResponse from a list of results.
     * Uses simple pagination (single page with all results).
     */
    private SearchResponse<MediaResponse> buildSearchResponse(List<MediaResponse> content) {
        return SearchResponse.<MediaResponse>builder()
                .content(content != null ? content : List.of())
                .page(0)
                .size(content != null ? content.size() : 0)
                .totalElements(content != null ? content.size() : 0)
                .totalPages(1)
                .build();
    }
}
