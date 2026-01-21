package com.resonance.controller;

import com.resonance.controller.doc.FeedControllerDoc;
import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.media.SearchResponse;
import com.resonance.service.FeedService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

/**
 * REST controller for the public discovery feed.
 * <p>
 * This controller provides unauthenticated access to a randomized
 * feed of albums, allowing users to explore content before registering.
 * Authenticated users get unlimited pagination; guests are limited to 5 albums.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/feed")
public class FeedController implements FeedControllerDoc {

    private static final int GUEST_MAX_SIZE = 5;
    private static final int AUTH_MAX_SIZE = 50; // API rate limit safeguard

    private final FeedService feedService;

    @Value("${spring.security.jwt.cookie-name}")
    private String cookieName;

    /**
     * Returns a paginated discovery feed of albums.
     * <p>
     * Authenticated users (with valid AUTH_TOKEN cookie) can request up to 50 items.
     * Guests are limited to 5 items per request.
     */
    @Override
    @GetMapping
    public ResponseEntity<SearchResponse<MediaResponse>> getDiscoveryFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        boolean authenticated = isAuthenticated(request);
        int effectiveSize = authenticated
                ? Math.min(size, AUTH_MAX_SIZE)
                : Math.min(size, GUEST_MAX_SIZE);

        SearchResponse<MediaResponse> feed = feedService.getDiscoveryFeed(page, effectiveSize);
        return ResponseEntity.ok(feed);
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        return cookie != null && cookie.getValue() != null && !cookie.getValue().isBlank();
    }
}
