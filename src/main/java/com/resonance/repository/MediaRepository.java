package com.resonance.repository;

import com.resonance.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, String> {

    /**
     * Batch fetch media entities by their IDs.
     * Used for eager caching to check which entities already exist in the database.
     *
     * @param ids list of iTunes IDs to lookup
     * @return list of existing Media entities
     */
    List<Media> findAllByIdIn(List<String> ids);
}