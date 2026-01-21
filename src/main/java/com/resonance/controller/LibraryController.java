package com.resonance.controller;

import com.resonance.controller.doc.LibraryControllerDoc;
import com.resonance.dto.favorite.AddToFavoriteRequest;
import com.resonance.dto.library.AddToLibraryRequest;
import com.resonance.dto.library.LibraryEntryResponse;
import com.resonance.dto.library.UpdateLibraryEntryRequest;
import com.resonance.entities.User;
import com.resonance.repository.UserRepository;
import com.resonance.service.LibraryService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for user library management.
 * <p>
 * Handles CRUD operations for the user's personal music library,
 * including ratings, favorites, and comments.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/library")
public class LibraryController implements LibraryControllerDoc {

    private final LibraryService libraryService;
    private final UserRepository userRepository;

    /**
     * Adds a media item to the user's library.
     *
     * @param request   the request containing media details and optional rating/favorite/comment
     * @param principal the authenticated user
     * @return the created library entry
     */
    @Override
    @PostMapping
    public ResponseEntity<LibraryEntryResponse> addToLibrary(
            @Valid @RequestBody AddToLibraryRequest request,
            Principal principal) {
        User user = getCurrentUser(principal);
        LibraryEntryResponse response = libraryService.addToLibrary(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/favorites")
    public ResponseEntity<LibraryEntryResponse> addToFavorite(
            @Valid @RequestBody AddToFavoriteRequest request,
            Principal principal) {
        User user = getCurrentUser(principal);
        LibraryEntryResponse response = libraryService.addToFavorites(user, request.mediaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing library entry.
     *
     * @param entryId   the ID of the library entry to update
     * @param request   the update request with new rating/favorite/comment values
     * @param principal the authenticated user
     * @return the updated library entry
     */
    @Override
    @PutMapping("/{entryId}")
    public ResponseEntity<LibraryEntryResponse> updateLibraryEntry(
            @PathVariable UUID entryId,
            @Valid @RequestBody UpdateLibraryEntryRequest request,
            Principal principal) {
        User user = getCurrentUser(principal);
        LibraryEntryResponse response = libraryService.updateLibraryEntry(user, entryId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Removes a media item from the user's library.
     *
     * @param mediaId   the iTunes ID of the media to remove
     * @param principal the authenticated user
     * @return 204 No Content on success
     */
    @Override
    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> removeFromLibrary(
            @PathVariable String mediaId,
            Principal principal) {
        User user = getCurrentUser(principal);
        libraryService.removeFromLibrary(user, mediaId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all entries in the user's library.
     *
     * @param principal the authenticated user
     * @return list of all library entries
     */
    @Override
    @GetMapping
    public ResponseEntity<List<LibraryEntryResponse>> getUserLibrary(Principal principal) {
        User user = getCurrentUser(principal);
        List<LibraryEntryResponse> response = libraryService.getUserLibrary(user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all favorites from the user's library.
     *
     * @param principal the authenticated user
     * @return list of library entries marked as favorite
     */
    @Override
    @GetMapping("/favorites")
    public ResponseEntity<List<LibraryEntryResponse>> getUserFavorites(Principal principal) {
        User user = getCurrentUser(principal);
        List<LibraryEntryResponse> response = libraryService.getUserFavorites(user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a specific library entry for a media item.
     *
     * @param mediaId   the iTunes ID of the media
     * @param principal the authenticated user
     * @return the library entry if found, 404 otherwise
     */
    @Override
    @GetMapping("/{mediaId}")
    public ResponseEntity<LibraryEntryResponse> getLibraryEntry(
            @PathVariable String mediaId,
            Principal principal) {
        User user = getCurrentUser(principal);
        LibraryEntryResponse response = libraryService.getLibraryEntry(user, mediaId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the current authenticated user from the principal.
     */
    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found: " + principal.getName()));
    }
}
