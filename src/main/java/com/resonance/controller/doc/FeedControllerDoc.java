package com.resonance.controller.doc;

import com.resonance.dto.media.MediaResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * OpenAPI documentation interface for the Feed Controller.
 */
@Tag(name = "Feed", description = "Public discovery feed endpoints")
public interface FeedControllerDoc {

    @Operation(
            summary = "Get discovery feed",
            description = "Returns a randomized list of albums for the homepage discovery feed. " +
                    "This endpoint is public and does not require authentication. " +
                    "Results are shuffled for variety on each request."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Discovery feed retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MediaResponse.class))
                    )
            )
    })
    ResponseEntity<List<MediaResponse>> getDiscoveryFeed();
}
