package com.resonance.service;

import com.resonance.dto.media.MediaResponse;
import com.resonance.entities.Media;
import com.resonance.entities.enums.MediaType;
import com.resonance.entities.media.Album;
import com.resonance.entities.media.Artist;
import com.resonance.entities.media.Track;
import com.resonance.external.itunes.MusicMetadataProvider;
import com.resonance.mapper.MediaMapper;
import com.resonance.repository.MediaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for managing media entities and external API lookups.
 * Uses iTunes as the external music metadata provider.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MusicMetadataProvider musicMetadataProvider;
    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;

    /**
     * Get album by ID. Checks cache first, then fetches from external API if not found.
     *
     * @param id the album ID (iTunes collection ID)
     * @return MediaResponse for the album, or null if not found
     */
    @Transactional(readOnly = true)
    public MediaResponse getAlbumById(String id) {
        log.debug("Getting album by ID: {}", id);

        // Check cache first
        Optional<Media> cached = mediaRepository.findById(id);
        if (cached.isPresent() && cached.get() instanceof Album album) {
            log.debug("Album found in cache: {}", id);
            return mediaMapper.albumToResponse(album);
        }

        log.debug("Album not in cache, fetching from external API: {}", id);
        return musicMetadataProvider.lookupById(id);
    }

    /**
     * Get artist by ID. Checks cache first, then fetches from external API if not found.
     *
     * @param id the artist ID (iTunes artist ID)
     * @return MediaResponse for the artist, or null if not found
     */
    @Transactional(readOnly = true)
    public MediaResponse getArtistById(String id) {
        log.debug("Getting artist by ID: {}", id);

        // Check cache first
        Optional<Media> cached = mediaRepository.findById(id);
        if (cached.isPresent() && cached.get() instanceof Artist artist) {
            log.debug("Artist found in cache: {}", id);
            return mediaMapper.artistToResponse(artist);
        }

        log.debug("Artist not in cache, fetching from external API: {}", id);
        return musicMetadataProvider.lookupById(id);
    }

    /**
     * Get track by ID. Checks cache first, then fetches from external API if not found.
     *
     * @param id the track ID (iTunes track ID)
     * @return MediaResponse for the track, or null if not found
     */
    @Transactional(readOnly = true)
    public MediaResponse getTrackById(String id) {
        log.debug("Getting track by ID: {}", id);

        // Check cache first
        Optional<Media> cached = mediaRepository.findById(id);
        if (cached.isPresent() && cached.get() instanceof Track track) {
            log.debug("Track found in cache: {}", id);
            return mediaMapper.trackToResponse(track);
        }

        log.debug("Track not in cache, fetching from external API: {}", id);
        return musicMetadataProvider.lookupById(id);
    }

    /**
     * Get or create a media entity. Creates a new entity if not found in cache.
     *
     * @param itunesId   the iTunes ID for the media
     * @param type       the media type (ALBUM, ARTIST, TRACK)
     * @param title      the title of the media
     * @param artistName the artist name
     * @return the existing or newly created Media entity
     */
    @Transactional
    public Media getOrCreateMedia(String itunesId, MediaType type, String title, String artistName) {
        return mediaRepository.findById(itunesId)
                .orElseGet(() -> {
                    log.info("Creating new media entry for iTunes ID: {}", itunesId);
                    return createAndCacheMedia(itunesId, type, title, artistName);
                });
    }

    /**
     * Update media rating statistics.
     *
     * @param media     the media entity to update
     * @param newRating the new rating value
     */
    public void updateMediaRatingStats(Media media, Integer newRating) {
        if (newRating == null) return;

        // This is a simplified implementation - in production you'd want to
        // recalculate based on all ratings for this media
        log.debug("Should update rating stats for media: {}", media.getId());
    }

    private Media createAndCacheMedia(String itunesId, MediaType type, String title, String artistName) {
        Media media = switch (type) {
            case ALBUM -> createAlbum(itunesId, title, artistName);
            case ARTIST -> createArtist(itunesId, artistName);
            case TRACK -> createTrack(itunesId, title, artistName);
        };

        return mediaRepository.save(media);
    }

    private Album createAlbum(String itunesId, String title, String artistName) {
        return Album.builder()
                .id(itunesId)
                .title(title != null ? title : "Unknown Album")
                .artistName(artistName != null ? artistName : "Unknown Artist")
                .build();
    }

    private Artist createArtist(String itunesId, String artistName) {
        return Artist.builder()
                .id(itunesId)
                .title(artistName != null ? artistName : "Unknown Artist")
                .artistName(artistName != null ? artistName : "Unknown Artist")
                .build();
    }

    private Track createTrack(String itunesId, String title, String artistName) {
        return Track.builder()
                .id(itunesId)
                .title(title != null ? title : "Unknown Track")
                .artistName(artistName != null ? artistName : "Unknown Artist")
                .build();
    }
}
