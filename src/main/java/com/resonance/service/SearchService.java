package com.resonance.service;

import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.media.SearchResponse;
import com.resonance.external.itunes.MusicMetadataProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for searching music metadata via external APIs.
 * Uses MusicMetadataProvider for catalog lookups.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public final class SearchService {

    private final MusicMetadataProvider musicMetadataProvider;

    /**
     * Search albums by query.
     *
     * @param query the search term
     * @return paginated search response containing album results
     */
    public SearchResponse<MediaResponse> searchAlbums(String query) {
        log.debug("Searching albums with query: {}", query);
        List<MediaResponse> albums = musicMetadataProvider.searchAlbums(query);
        return buildSearchResponse(albums);
    }

    /**
     * Search artists by query.
     *
     * @param query the search term
     * @return paginated search response containing artist results
     */
    public SearchResponse<MediaResponse> searchArtists(String query) {
        log.debug("Searching artists with query: {}", query);
        List<MediaResponse> artists = musicMetadataProvider.searchArtists(query);
        return buildSearchResponse(artists);
    }

    /**
     * Search tracks by query.
     *
     * @param query the search term
     * @return paginated search response containing track results
     */
    public SearchResponse<MediaResponse> searchTracks(String query) {
        log.debug("Searching tracks with query: {}", query);
        List<MediaResponse> tracks = musicMetadataProvider.searchTracks(query);
        return buildSearchResponse(tracks);
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
