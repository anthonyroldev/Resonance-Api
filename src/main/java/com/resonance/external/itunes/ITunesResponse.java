package com.resonance.external.itunes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Root response object from the iTunes Search API.
 * Contains the result count and list of results.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ITunesResponse(
        Integer resultCount,
        List<ITunesResult> results
) {
    /**
     * Returns an empty response with zero results.
     */
    public static ITunesResponse empty() {
        return new ITunesResponse(0, List.of());
    }
}
