package com.resonance.service;

import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.media.SearchResponse;
import com.resonance.entities.Media;
import com.resonance.external.itunes.ITunesClient;
import com.resonance.external.itunes.ITunesResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Service for generating discovery feed content.
 * <p>
 * Implements eager caching: all feed results are persisted to the local
 * database before being returned, building the catalog as users browse.
 * <p>
 * The feed returns tracks (songs) with 30-second audio previews, enabling
 * TikTok-style audio playback in the frontend.
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
            "Top Hits 2025", "Top Hits 2024", "Top Hits 2023", "Viral Hits",
            "Pop 2025", "Rock Classics", "Hip Hop Essentials",
            "Indie Folk", "Electronic 2024", "R&B Soul",
            "Jazz Classics", "Country Hits", "Latin Pop",
            "Alternative 2000s", "Classical Essentials", "Reggae Vibes",
            "Metal", "K-Pop", "French Pop",
            "Acoustic Covers", "Chill Lo-Fi", "Party Anthems",
            "Throwback 2010s", "90s Hits", "80s Pop"
    );

    private static final int MAX_ITUNES_LIMIT = 200; // iTunes API max

    private final ITunesClient iTunesClient;
    private final MediaService mediaService;
    private final SecureRandom random = new SecureRandom();

    /**
     * Generates a paginated discovery feed of tracks with audio previews.
     * Using tracks instead of albums ensures each item has a 30-second
     * audio preview URL for TikTok-style playback.
     *
     * @param page page number (0-indexed)
     * @param size page size
     * @return paginated SearchResponse with tracks including previewUrl
     */
    public SearchResponse<MediaResponse> getDiscoveryFeed(int page, int size) {
        String keyword1 = getRandomKeyword();
        String keyword2 = getRandomKeyword();
        while (keyword2.equals(keyword1)) {
            keyword2 = getRandomKeyword();
        }

        log.debug("Generating hybrid feed with keywords: '{}' & '{}'", keyword1, keyword2);

        int batchSize = Math.min((size * 2), 50);

        List<ITunesResult> rawResults = new ArrayList<>();

        try {
            var response1 = iTunesClient.searchTracks(keyword1, batchSize);
            if (response1.results() != null) rawResults.addAll(response1.results());

            var response2 = iTunesClient.searchTracks(keyword2, batchSize);
            if (response2.results() != null) rawResults.addAll(response2.results());

        } catch (Exception e) {
            log.error("Error fetching iTunes feed", e);
        }

        if (rawResults.isEmpty()) {
            return buildEmptyResponse(page);
        }
        List<Media> syncedMedia = mediaService.syncTracks(new ArrayList<>(new HashSet<>(rawResults)));
        List<MediaResponse> allResults = new ArrayList<>(mediaService.toMediaResponses(syncedMedia));

        Collections.shuffle(allResults, random);
        List<MediaResponse> pageContent = allResults.stream()
                .limit(size)
                .toList();

        return SearchResponse.<MediaResponse>builder()
                .content(pageContent)
                .page(page)
                .size(pageContent.size())
                .totalElements(MAX_ITUNES_LIMIT)
                .totalPages(MAX_ITUNES_LIMIT / size)
                .build();
    }

    private String getRandomKeyword() {
        return DISCOVERY_KEYWORDS.get(random.nextInt(DISCOVERY_KEYWORDS.size()));
    }

    private SearchResponse<MediaResponse> buildEmptyResponse(int page) {
        return SearchResponse.<MediaResponse>builder()
                .content(List.of())
                .page(page)
                .size(0)
                .totalElements(0)
                .totalPages(0)
                .build();
    }
}
