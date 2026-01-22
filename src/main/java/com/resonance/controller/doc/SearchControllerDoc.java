package com.resonance.controller.doc;

import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.media.SearchResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.Size;

import org.springframework.http.ResponseEntity;

/**
 * OpenAPI documentation interface for the Search Controller.
 */
@Tag(name = "Search", description = "Music search endpoints (authenticated)")
@SecurityRequirement(name = "bearerAuth")
public interface SearchControllerDoc {

    @Operation(
            summary = "Search albums",
            description = "Search for albums in the iTunes catalog by keyword"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SearchResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated",
                    content = @Content
            )
    })
    ResponseEntity<SearchResponse<MediaResponse>> searchAlbums(
            @Parameter(description = "Search query", required = true, example = "The Beatles")
            @Size(min = 3, message = "recherche avec au moins 3 caractères")
            String q
    );

    @Operation(
            summary = "Search artists",
            description = "Search for artists in the iTunes catalog by keyword"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SearchResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated",
                    content = @Content
            )
    })
    ResponseEntity<SearchResponse<MediaResponse>> searchArtists(
            @Parameter(description = "Search query", required = true, example = "Taylor Swift")
            @Size(min = 3, message = "recherche avec au moins 3 caractères")
            String q
    );

    @Operation(
            summary = "Search tracks",
            description = "Search for tracks/songs in the iTunes catalog by keyword"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SearchResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated",
                    content = @Content
            )
    })
    ResponseEntity<SearchResponse<MediaResponse>> searchTracks(
            @Parameter(description = "Search query", required = true, example = "Bohemian Rhapsody")
            @Size(min = 3, message = "recherche avec au moins 3 caractères")
            String q
    );
}
