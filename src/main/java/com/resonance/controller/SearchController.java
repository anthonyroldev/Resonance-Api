package com.resonance.controller;

import com.resonance.controller.doc.SearchControllerDoc;
import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.media.SearchResponse;
import com.resonance.service.SearchService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for music search operations.
 * <p>
 * Provides search functionality for albums, artists, and tracks
 * using the iTunes Search API. Requires authentication.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController implements SearchControllerDoc {

    private final SearchService searchService;

    /**
     * Search for albums by keyword.
     *
     * @param q the search query
     * @return paginated search results containing albums
     */
    @Override
    @GetMapping("/albums")
    public ResponseEntity<SearchResponse<MediaResponse>> searchAlbums(@RequestParam String q) {
        SearchResponse<MediaResponse> response = searchService.searchAlbums(q);
        return ResponseEntity.ok(response);
    }

    /**
     * Search for artists by keyword.
     *
     * @param q the search query
     * @return paginated search results containing artists
     */
    @Override
    @GetMapping("/artists")
    public ResponseEntity<SearchResponse<MediaResponse>> searchArtists(@RequestParam String q) {
        SearchResponse<MediaResponse> response = searchService.searchArtists(q);
        return ResponseEntity.ok(response);
    }

    /**
     * Search for tracks by keyword.
     *
     * @param q the search query
     * @return paginated search results containing tracks
     */
    @Override
    @GetMapping("/tracks")
    public ResponseEntity<SearchResponse<MediaResponse>> searchTracks(@RequestParam String q) {
        SearchResponse<MediaResponse> response = searchService.searchTracks(q);
        return ResponseEntity.ok(response);
    }
}
