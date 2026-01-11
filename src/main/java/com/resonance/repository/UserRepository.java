package com.resonance.repository;

import com.resonance.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findBySpotifyId(String spotifyId);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

}
