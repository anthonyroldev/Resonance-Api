package com.resonance.external.itunes;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Client for the Apple iTunes Search API.
 * Uses Spring RestClient for HTTP requests.
 *
 * @see <a href="https://performance-partners.apple.com/search-api">iTunes Search API Documentation</a>
 */
@Slf4j
@Component
public final class ITunesClient {

    private static final int DEFAULT_LIMIT = 20;
    private static final String MEDIA_MUSIC = "music";
    private static final String ENTITY_ALBUM = "album";
    private static final String ENTITY_SONG = "song";
    private static final String ENTITY_MUSIC_ARTIST = "musicArtist";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ITunesClient(
            @Value("${itunes.base-url}") String baseUrl,
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Search for albums in the iTunes Store.
     *
     * @param query the search term
     * @return ITunesResponse containing album results, or empty response on error
     */
    public ITunesResponse searchAlbums(String query) {
        return search(query, ENTITY_ALBUM, DEFAULT_LIMIT);
    }

    /**
     * Search for albums with a custom limit.
     *
     * @param query the search term
     * @param limit maximum number of results (1-200)
     * @return ITunesResponse containing album results, or empty response on error
     */
    public ITunesResponse searchAlbums(String query, int limit) {
        return search(query, ENTITY_ALBUM, limit);
    }

    /**
     * Search for songs/tracks in the iTunes Store.
     *
     * @param query the search term
     * @return ITunesResponse containing track results, or empty response on error
     */
    public ITunesResponse searchTracks(String query) {
        return search(query, ENTITY_SONG, DEFAULT_LIMIT);
    }

    /**
     * Search for songs/tracks with a custom limit.
     *
     * @param query the search term
     * @param limit maximum number of results (1-200)
     * @return ITunesResponse containing track results, or empty response on error
     */
    public ITunesResponse searchTracks(String query, int limit) {
        return search(query, ENTITY_SONG, limit);
    }

    /**
     * Search for artists in the iTunes Store.
     *
     * @param query the search term
     * @return ITunesResponse containing artist results, or empty response on error
     */
    public ITunesResponse searchArtists(String query) {
        return search(query, ENTITY_MUSIC_ARTIST, DEFAULT_LIMIT);
    }

    /**
     * Search for artists with a custom limit.
     *
     * @param query the search term
     * @param limit maximum number of results (1-200)
     * @return ITunesResponse containing artist results, or empty response on error
     */
    public ITunesResponse searchArtists(String query, int limit) {
        return search(query, ENTITY_MUSIC_ARTIST, limit);
    }

    /**
     * Lookup content by iTunes ID.
     *
     * @param itunesId the iTunes ID to lookup
     * @return ITunesResponse containing the result, or empty response on error
     */
    public ITunesResponse lookupById(Long itunesId) {
        try {
            log.debug("Looking up iTunes content by ID: {}", itunesId);
            ITunesResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lookup")
                            .queryParam("id", itunesId)
                            .build())
                    .exchange((_, res) -> {
                        if (res.getStatusCode().is2xxSuccessful()) {
                            return objectMapper.readValue(res.getBody(), ITunesResponse.class);
                        }
                        return ITunesResponse.empty();
                    });

            if (response == null) {
                log.warn("Received null response from iTunes lookup API for ID: {}", itunesId);
                return ITunesResponse.empty();
            }

            log.debug("iTunes lookup returned {} result(s) for ID: {}", response.resultCount(), itunesId);
            return response;

        } catch (RestClientException e) {
            log.error("Error during iTunes lookup for ID {}: {}", itunesId, e.getMessage());
            return ITunesResponse.empty();
        }
    }

    private ITunesResponse search(String query, String entity, int limit) {
        try {
            log.debug("Searching iTunes for '{}' with entity={}, limit={}", query, entity, limit);

            ITunesResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("term", query)
                            .queryParam("media", MEDIA_MUSIC)
                            .queryParam("entity", entity)
                            .queryParam("limit", Math.min(Math.max(limit, 1), 200))
                            .build())
                    .exchange((_, res) -> {
                        if (res.getStatusCode().is2xxSuccessful()) {
                            return objectMapper.readValue(res.getBody(), ITunesResponse.class);
                        }
                        return ITunesResponse.empty();
                    });

            if (response == null) {
                log.warn("Received null response from iTunes Search API for query: {}", query);
                return ITunesResponse.empty();
            }

            log.debug("iTunes search returned {} result(s) for query: {}", response.resultCount(), query);
            return response;

        } catch (RestClientException e) {
            log.error("Error during iTunes search for query '{}': {}", query, e.getMessage());
            return ITunesResponse.empty();
        }
    }
}
