package com.resonance.service;

import com.resonance.dto.media.MediaResponse;
import com.resonance.external.itunes.ITunesClient;
import com.resonance.external.itunes.ITunesMediaMapper;
import com.resonance.external.itunes.ITunesResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service for generating discovery feed content.
 * <p>
 * Provides a "TikTok-style" randomized feed of albums by selecting
 * a random keyword and fetching results from iTunes Search API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    /**
     * Curated list of keywords for discovery feed.
     * These keywords are designed to return diverse, popular music content.
     */
    private static final List<String> DISCOVERY_KEYWORDS = List.of(
            "Top Hits 2024",
            "Pop Hits",
            "Rock Classics",
            "Hip Hop",
            "Indie Folk",
            "Electronic Dance",
            "R&B Soul",
            "Jazz Essentials",
            "Country Music",
            "Latin Hits",
            "Alternative Rock",
            "Classical Music",
            "Reggae",
            "Metal",
            "K-Pop",
            "French Pop",
            "Acoustic",
            "Chill Vibes",
            "Party Mix",
            "Throwback 90s"
    );

    private static final int FEED_SIZE = 20;

    private final ITunesClient iTunesClient;
    private final ITunesMediaMapper iTunesMediaMapper;
    private final SecureRandom random = new SecureRandom();

    /**
     * Generates a randomized discovery feed of albums.
     * <p>
     * Algorithm:
     * 1. Pick a random keyword from the curated list
     * 2. Fetch albums from iTunes Search API
     * 3. Shuffle results for additional randomness
     *
     * @return list of MediaResponse DTOs representing albums, shuffled for variety
     */
    public List<MediaResponse> getDiscoveryFeed() {
        String keyword = DISCOVERY_KEYWORDS.get(random.nextInt(DISCOVERY_KEYWORDS.size()));
        log.debug("Generating discovery feed with keyword: '{}'", keyword);

        ITunesResponse response = iTunesClient.searchAlbums(keyword, FEED_SIZE);

        if (response.results() == null || response.results().isEmpty()) {
            log.warn("No results from iTunes for discovery keyword: '{}'", keyword);
            return List.of();
        }

        List<MediaResponse> results = new ArrayList<>(
                iTunesMediaMapper.toAlbumResponses(response.results())
        );

        // Shuffle for additional randomness
        Collections.shuffle(results, random);

        log.debug("Discovery feed generated with {} albums for keyword: '{}'", results.size(), keyword);
        return results;
    }
}
