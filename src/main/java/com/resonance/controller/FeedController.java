package com.resonance.controller;

import com.resonance.controller.doc.FeedControllerDoc;
import com.resonance.dto.media.MediaResponse;
import com.resonance.service.FeedService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for the public discovery feed.
 * <p>
 * This controller provides unauthenticated access to a randomized
 * feed of albums, allowing users to explore content before registering.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/feed")
public class FeedController implements FeedControllerDoc {

    private final FeedService feedService;

    /**
     * Returns a randomized discovery feed of albums.
     * <p>
     * This endpoint is public and does not require authentication.
     *
     * @return list of MediaResponse DTOs representing albums
     */
    @Override
    @GetMapping
    public ResponseEntity<List<MediaResponse>> getDiscoveryFeed() {
        List<MediaResponse> feed = feedService.getDiscoveryFeed();
        return ResponseEntity.ok(feed);
    }
}
