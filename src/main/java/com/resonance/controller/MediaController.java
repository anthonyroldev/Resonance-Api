package com.resonance.controller;

import com.resonance.controller.doc.MediaControllerDoc;
import com.resonance.dto.media.MediaResponse;
import com.resonance.service.MediaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for media retrieval with lazy caching.
 * <p>
 * Provides a unified endpoint to retrieve any media type (album, track, artist)
 * by iTunes ID. Implements lazy caching strategy: checks local database first,
 * fetches from iTunes API and persists if not found.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media")
public class MediaController implements MediaControllerDoc {

    private final MediaService mediaService;

    /**
     * Get media by iTunes ID with lazy caching.
     * <p>
     * Auto-detects media type from iTunes response (album, track, or artist).
     * Caches the result to local database for future requests.
     *
     * @param id the iTunes ID (collectionId, trackId, or artistId)
     * @return MediaResponse if found, 404 if not found in iTunes
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getMediaById(@PathVariable String id) {
        MediaResponse response = mediaService.getMediaById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
