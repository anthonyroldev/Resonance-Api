package com.resonance.controller.doc;

import com.resonance.dto.media.MediaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * OpenAPI documentation interface for the Media Controller.
 */
@Tag(name = "Media", description = "Media retrieval with lazy caching (authenticated)")
@SecurityRequirement(name = "bearerAuth")
public interface MediaControllerDoc {

    @Operation(
            summary = "Get media by ID",
            description = "Retrieves media (album, track, or artist) by iTunes ID. " +
                    "Uses lazy caching: checks local database first, " +
                    "fetches from iTunes and caches if not found."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Media found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MediaResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Media not found in iTunes catalog",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated",
                    content = @Content
            )
    })
    ResponseEntity<MediaResponse> getMediaById(
            @Parameter(
                    description = "iTunes ID (collectionId, trackId, or artistId)",
                    required = true,
                    example = "1440857781"
            )
            String id
    );
}
