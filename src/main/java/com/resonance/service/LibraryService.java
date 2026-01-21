package com.resonance.service;

import com.resonance.dto.library.AddToLibraryRequest;
import com.resonance.dto.library.LibraryEntryResponse;
import com.resonance.dto.library.UpdateLibraryEntryRequest;
import com.resonance.entities.Media;
import com.resonance.entities.User;
import com.resonance.entities.UserLibraryEntry;
import com.resonance.mapper.LibraryMapper;
import com.resonance.repository.UserLibraryEntryRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryService {

    private final UserLibraryEntryRepository libraryEntryRepository;
    private final MediaService mediaService;
    private final LibraryMapper libraryMapper;

    /**
     * Add media to user's library (favorites, rating, comment).
     */
    @Transactional
    public LibraryEntryResponse addToLibrary(User user, AddToLibraryRequest request) {
        log.info("Adding media {} to library for user {}", request.mediaId(), user.getId());

        Media media = mediaService.getOrCreateMedia(
                request.mediaId(),
                request.mediaType(),
                null,
                null
        );

        // Check if entry already exists
        Optional<UserLibraryEntry> existingEntry = libraryEntryRepository
                .findByUserIdAndMediaId(user.getId(), request.mediaId());

        if (existingEntry.isPresent()) {
            // Update existing entry
            return updateLibraryEntry(user, existingEntry.get().getId(),
                    UpdateLibraryEntryRequest.builder()
                            .rating(request.rating())
                            .isFavorite(request.isFavorite())
                            .comment(request.comment())
                            .build());
        }

        // Create new entry
        UserLibraryEntry entry = UserLibraryEntry.builder()
                .user(user)
                .media(media)
                .rating(request.rating())
                .isFavorite(request.isFavorite() != null ? request.isFavorite() : false)
                .comment(request.comment())
                .build();

        UserLibraryEntry saved = libraryEntryRepository.save(entry);

        // Update media rating statistics
        mediaService.updateMediaRatingStats(media, request.rating());

        return libraryMapper.toResponse(saved);
    }

    @Transactional
    public LibraryEntryResponse addToFavorites(User user, String mediaId) {
        log.info("Adding media {} to favorites for user {}", mediaId, user.getId());

        Media media = mediaService.getOrCreateMedia(
                mediaId,
                null,
                null,
                null
        );

        // Check if entry already exists
        Optional<UserLibraryEntry> existingEntry = libraryEntryRepository
                .findByUserIdAndMediaId(user.getId(), mediaId);

        if (existingEntry.isPresent()) {
            // Update existing entry to set favorite
            return updateLibraryEntry(user, existingEntry.get().getId(),
                    UpdateLibraryEntryRequest.builder()
                            .isFavorite(true)
                            .build());
        }

        UserLibraryEntry entry = UserLibraryEntry.builder()
                .user(user)
                .media(media)
                .isFavorite(true)
                .build();

        UserLibraryEntry saved = libraryEntryRepository.save(entry);

        return libraryMapper.toResponse(saved);
    }

    /**
     * Update an existing library entry.
     */
    @Transactional
    public LibraryEntryResponse updateLibraryEntry(User user, UUID entryId, UpdateLibraryEntryRequest request) {
        log.info("Updating library entry {} for user {}", entryId, user.getId());

        UserLibraryEntry entry = libraryEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("Library entry not found: " + entryId));

        // Verify ownership
        if (!entry.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You can only update your own library entries");
        }

        // Update fields using builder pattern (since entity uses @Getter only)
        UserLibraryEntry updated = UserLibraryEntry.builder()
                .id(entry.getId())
                .user(entry.getUser())
                .media(entry.getMedia())
                .rating(request.rating() != null ? request.rating() : entry.getRating())
                .isFavorite(request.isFavorite() != null ? request.isFavorite() : entry.isFavorite())
                .comment(request.comment() != null ? request.comment() : entry.getComment())
                .addedAt(entry.getAddedAt())
                .build();

        UserLibraryEntry saved = libraryEntryRepository.save(updated);
        return libraryMapper.toResponse(saved);
    }

    /**
     * Remove media from user's library.
     */
    @Transactional
    public void removeFromLibrary(User user, String mediaId) {
        log.info("Removing media {} from library for user {}", mediaId, user.getId());

        UserLibraryEntry entry = libraryEntryRepository
                .findByUserIdAndMediaId(user.getId(), mediaId)
                .orElseThrow(() -> new EntityNotFoundException("Library entry not found for media: " + mediaId));

        libraryEntryRepository.delete(entry);
    }

    /**
     * Get user's library entry for a specific media.
     */
    @Transactional(readOnly = true)
    public LibraryEntryResponse getLibraryEntry(User user, String mediaId) {
        return libraryEntryRepository.findByUserIdAndMediaId(user.getId(), mediaId)
                .map(libraryMapper::toResponse)
                .orElse(null);
    }

    /**
     * Get all library entries for a user.
     */
    @Transactional(readOnly = true)
    public List<LibraryEntryResponse> getUserLibrary(UUID userId) {
        return libraryMapper.toResponseList(libraryEntryRepository.findByUserIdAndIsFavoriteFalse(userId));
    }

    /**
     * Get user's favorite media.
     */
    @Transactional(readOnly = true)
    public List<LibraryEntryResponse> getUserFavorites(UUID userId) {
        return libraryMapper.toResponseList(libraryEntryRepository.findByUserIdAndIsFavoriteTrue(userId));
    }
}
