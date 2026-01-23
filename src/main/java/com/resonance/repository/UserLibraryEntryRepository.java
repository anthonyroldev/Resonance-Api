package com.resonance.repository;

import com.resonance.entities.UserLibraryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserLibraryEntryRepository extends JpaRepository<UserLibraryEntry, UUID> {

    Optional<UserLibraryEntry> findByUserIdAndMediaId(UUID userId, String mediaId);

    List<UserLibraryEntry> findByUserIdAndIsFavoriteFalse(UUID userId);

    List<UserLibraryEntry> findByUserIdAndIsFavoriteTrue(UUID userId);

}
