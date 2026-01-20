package com.resonance.controller.doc;

import com.resonance.config.OpenApiConfig;
import com.resonance.dto.library.AddToLibraryRequest;
import com.resonance.dto.library.LibraryEntryResponse;
import com.resonance.dto.library.UpdateLibraryEntryRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * OpenAPI documentation interface for Library Controller.
 * <p>
 * Manages user's personal music library: adding, updating, removing,
 * and retrieving library entries with ratings, favorites, and comments.
 */
@Tag(name = "Library", description = "User library management endpoints (authenticated)")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public interface LibraryControllerDoc {

    @Operation(
            summary = "Add to library",
            description = "Adds a media item (album, artist, or track) to the user's library " +
                    "with optional rating, favorite status, and comment."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Media added to library successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "409", description = "Media already in library")
    })
    ResponseEntity<LibraryEntryResponse> addToLibrary(
            @RequestBody AddToLibraryRequest request,
            Principal principal
    );

    @Operation(
            summary = "Update library entry",
            description = "Updates the rating, favorite status, or comment of an existing library entry."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entry updated successfully"),
            @ApiResponse(responseCode = "404", description = "Entry not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized to modify this entry")
    })
    ResponseEntity<LibraryEntryResponse> updateLibraryEntry(
            @Parameter(description = "Library entry ID")
            @PathVariable UUID entryId,
            @RequestBody UpdateLibraryEntryRequest request,
            Principal principal
    );

    @Operation(
            summary = "Remove from library",
            description = "Removes a media item from the user's library."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Media removed from library successfully"),
            @ApiResponse(responseCode = "404", description = "Entry not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    ResponseEntity<Void> removeFromLibrary(
            @Parameter(description = "iTunes ID of the media to remove")
            @PathVariable String mediaId,
            Principal principal
    );

    @Operation(
            summary = "Get user library",
            description = "Retrieves all entries in the authenticated user's library."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Library retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    ResponseEntity<List<LibraryEntryResponse>> getUserLibrary(Principal principal);

    @Operation(
            summary = "Get user favorites",
            description = "Retrieves all media items marked as favorites by the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favorites retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    ResponseEntity<List<LibraryEntryResponse>> getUserFavorites(Principal principal);

    @Operation(
            summary = "Get library entry",
            description = "Retrieves a specific library entry for a given media item."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entry found successfully"),
            @ApiResponse(responseCode = "404", description = "Entry not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    ResponseEntity<LibraryEntryResponse> getLibraryEntry(
            @Parameter(description = "iTunes ID of the media")
            @PathVariable String mediaId,
            Principal principal
    );
}
