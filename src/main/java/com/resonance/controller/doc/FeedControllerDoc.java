package com.resonance.controller.doc;

import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.media.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

/**
 * OpenAPI documentation interface for the Feed Controller.
 */
@Tag(name = "Feed", description = "Public discovery feed endpoints")
public interface FeedControllerDoc {

    @Operation(
            summary = "Get discovery feed",
            description = "Returns a paginated list of tracks with 30-second audio previews for the homepage discovery feed. " +
                    "Each track includes a previewUrl for TikTok-style audio playback. " +
                    "Authenticated users (with AUTH_TOKEN cookie) can request up to 50 items. " +
                    "Guests are limited to 5 items per request."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Discovery feed retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SearchResponse.class)
                    )
            )
    })
    ResponseEntity<SearchResponse<MediaResponse>> getDiscoveryFeed(
            @Parameter(description = "Page number (0-indexed)", example = "0") int page,
            @Parameter(description = "Page size (max 5 for guests, max 50 for authenticated)", example = "10") int size,
            HttpServletRequest request
    );
}
