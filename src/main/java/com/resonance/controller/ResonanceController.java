package com.resonance.controller;

import com.resonance.controller.doc.ResonanceControllerDoc;
import com.resonance.dto.library.AddToLibraryRequest;
import com.resonance.dto.library.LibraryEntryResponse;
import com.resonance.dto.library.UpdateLibraryEntryRequest;
import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.media.SearchResponse;
import com.resonance.entities.User;
import com.resonance.repository.UserRepository;
import com.resonance.service.LibraryService;
import com.resonance.service.MediaService;
import com.resonance.service.SearchService;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ResonanceController implements ResonanceControllerDoc {

    private final SearchService searchService;
    private final MediaService mediaService;
    private final LibraryService libraryService;
    private final UserRepository userRepository;

    @Override
    @GetMapping("/search/albums")
    public ResponseEntity<SearchResponse<MediaResponse>> searchAlbums(@RequestParam String q) {
        SearchResponse<MediaResponse> response = searchService.searchAlbums(q);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/search/artists")
    public ResponseEntity<SearchResponse<MediaResponse>> searchArtists(@RequestParam String q) {
        SearchResponse<MediaResponse> response = searchService.searchArtists(q);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/search/tracks")
    public ResponseEntity<SearchResponse<MediaResponse>> searchTracks(@RequestParam String q) {
        SearchResponse<MediaResponse> response = searchService.searchTracks(q);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/albums/{id}")
    public ResponseEntity<MediaResponse> getAlbumById(@PathVariable String id) {
        MediaResponse response = mediaService.getAlbumById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/artists/{id}")
    public ResponseEntity<MediaResponse> getArtistById(@PathVariable String id) {
        MediaResponse response = mediaService.getArtistById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/tracks/{id}")
    public ResponseEntity<MediaResponse> getTrackById(@PathVariable String id) {
        MediaResponse response = mediaService.getTrackById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/library")
    public ResponseEntity<LibraryEntryResponse> addToLibrary(
            @Valid @RequestBody AddToLibraryRequest request,
            Principal principal) {
        User user = getCurrentUser(principal);
        LibraryEntryResponse response = libraryService.addToLibrary(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PutMapping("/library/{entryId}")
    public ResponseEntity<LibraryEntryResponse> updateLibraryEntry(
            @PathVariable UUID entryId,
            @Valid @RequestBody UpdateLibraryEntryRequest request,
            Principal principal) {
        User user = getCurrentUser(principal);
        LibraryEntryResponse response = libraryService.updateLibraryEntry(user, entryId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/library/{mediaId}")
    public ResponseEntity<Void> removeFromLibrary(
            @PathVariable String mediaId,
            Principal principal) {
        User user = getCurrentUser(principal);
        libraryService.removeFromLibrary(user, mediaId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/library")
    public ResponseEntity<List<LibraryEntryResponse>> getUserLibrary(Principal principal) {
        User user = getCurrentUser(principal);
        List<LibraryEntryResponse> response = libraryService.getUserLibrary(user.getId());
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/library/favorites")
    public ResponseEntity<List<LibraryEntryResponse>> getUserFavorites(Principal principal) {
        User user = getCurrentUser(principal);
        List<LibraryEntryResponse> response = libraryService.getUserFavorites(user.getId());
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/library/{mediaId}")
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

    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found: " + principal.getName()));
    }
}
