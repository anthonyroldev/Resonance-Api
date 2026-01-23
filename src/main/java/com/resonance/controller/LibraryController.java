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
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/library")
public class LibraryController implements LibraryControllerDoc {

    private final LibraryService libraryService;
    private final UserRepository userRepository;

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

    @Override
    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> removeFromLibrary(
            @PathVariable String mediaId,
            Principal principal) {
        User user = getCurrentUser(principal);
        libraryService.removeFromLibrary(user, mediaId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping
    public ResponseEntity<List<LibraryEntryResponse>> getUserLibrary(Principal principal) {
        User user = getCurrentUser(principal);
        List<LibraryEntryResponse> response = libraryService.getUserLibrary(user.getId());
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/favorites")
    public ResponseEntity<List<LibraryEntryResponse>> getUserFavorites(Principal principal) {
        User user = getCurrentUser(principal);
        List<LibraryEntryResponse> response = libraryService.getUserFavorites(user.getId());
        return ResponseEntity.ok(response);
    }

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

    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found: " + principal.getName()));
    }
}
