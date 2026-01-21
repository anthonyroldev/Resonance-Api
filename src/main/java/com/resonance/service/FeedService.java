package com.resonance.service;

import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.media.SearchResponse;
import com.resonance.entities.Media;
import com.resonance.external.itunes.ITunesClient;
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
 * Implements eager caching: all feed results are persisted to the local
 * database before being returned, building the catalog as users browse.
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
            "Top Hits 2025",
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

    private static final int MAX_ITUNES_LIMIT = 200; // iTunes API max

    private final ITunesClient iTunesClient;
    private final MediaService mediaService;
    private final SecureRandom random = new SecureRandom();

    /**
     * Generates a paginated discovery feed of albums.
     * <p>
     * Algorithm:
     * 1. Pick a random keyword from the curated list
     * 2. Fetch albums from iTunes Search API
     * 3. Eagerly cache all results to the database
     * 4. Shuffle results for additional randomness
     * 5. Apply pagination
     *
     * @param page page number (0-indexed)
     * @param size page size
     * @return paginated SearchResponse with albums
     */
    public SearchResponse<MediaResponse> getDiscoveryFeed(int page, int size) {
        String keyword = DISCOVERY_KEYWORDS.get(random.nextInt(DISCOVERY_KEYWORDS.size()));
        log.debug("Generating discovery feed with keyword: '{}', page: {}, size: {}", keyword, page, size);

        // Fetch enough results for pagination
        int fetchLimit = Math.min((page + 1) * size, MAX_ITUNES_LIMIT);
        ITunesResponse response = iTunesClient.searchAlbums(keyword, fetchLimit);

        if (response.results() == null || response.results().isEmpty()) {
            log.warn("No results from iTunes for discovery keyword: '{}'", keyword);
            return buildEmptyResponse(page);
        }

        // Sync to DB (eager caching) and convert to DTOs
        List<Media> syncedMedia = mediaService.syncAlbums(response.results());
        List<MediaResponse> allResults = new ArrayList<>(
                mediaService.toMediaResponses(syncedMedia)
        );
        Collections.shuffle(allResults, random);

        // Apply pagination
        int totalElements = allResults.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<MediaResponse> pageContent = (fromIndex < totalElements)
                ? allResults.subList(fromIndex, toIndex)
                : List.of();

        log.debug("Discovery feed: {} albums total, returning page {} with {} items", totalElements, page, pageContent.size());

        return SearchResponse.<MediaResponse>builder()
                .content(pageContent)
                .page(page)
                .size(pageContent.size())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
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
