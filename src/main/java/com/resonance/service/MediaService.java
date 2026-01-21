package com.resonance.service;

import com.resonance.dto.media.MediaResponse;
import com.resonance.entities.Media;
import com.resonance.entities.enums.MediaType;
import com.resonance.entities.media.Album;
import com.resonance.entities.media.Artist;
import com.resonance.entities.media.Track;
import com.resonance.external.itunes.ITunesClient;
import com.resonance.external.itunes.ITunesEntityMapper;
import com.resonance.external.itunes.ITunesResponse;
import com.resonance.external.itunes.ITunesResult;
import com.resonance.mapper.MediaMapper;
import com.resonance.repository.MediaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service for managing media entities with caching strategies.
 * This builds the local database organically as users browse and search.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;
    private final ITunesClient iTunesClient;
    private final ITunesEntityMapper iTunesEntityMapper;

    /**
     * Get media by ID with lazy caching (auto-detects type from iTunes).
     */
    @Transactional
    public MediaResponse getMediaById(String id) {
        log.debug("Getting media by ID: {}", id);

        // 1. Check cache first
        Optional<Media> cached = mediaRepository.findById(id);
        if (cached.isPresent()) {
            log.debug("Media found in cache: {}", id);
            return mapMediaToResponse(cached.get());
        }

        // 2. Fetch from iTunes
        log.debug("Media not in cache, fetching from iTunes: {}", id);
        return fetchAndCacheFromItunes(id);
    }

    /**
     * Get album by ID. Checks cache first, then fetches from external API if not found.
     *
     * @param id the album ID (iTunes collection ID)
     * @return MediaResponse for the album, or null if not found
     */
    @Transactional
    public MediaResponse getAlbumById(String id) {
        log.debug("Getting album by ID: {}", id);

        // Check cache first
        Optional<Media> cached = mediaRepository.findById(id);
        if (cached.isPresent() && cached.get() instanceof Album album) {
            log.debug("Album found in cache: {}", id);
            return mediaMapper.albumToResponse(album);
        }

        log.debug("Album not in cache, fetching from iTunes: {}", id);
        return fetchAndCacheAlbumFromItunes(id);
    }

    /**
     * Get artist by ID. Checks cache first, then fetches from external API if not found.
     *
     * @param id the artist ID (iTunes artist ID)
     * @return MediaResponse for the artist, or null if not found
     */
    @Transactional
    public MediaResponse getArtistById(String id) {
        log.debug("Getting artist by ID: {}", id);

        // Check cache first
        Optional<Media> cached = mediaRepository.findById(id);
        if (cached.isPresent() && cached.get() instanceof Artist artist) {
            log.debug("Artist found in cache: {}", id);
            return mediaMapper.artistToResponse(artist);
        }

        log.debug("Artist not in cache, fetching from iTunes: {}", id);
        return fetchAndCacheArtistFromItunes(id);
    }

    /**
     * Get track by ID. Checks cache first, then fetches from external API if not found.
     *
     * @param id the track ID (iTunes track ID)
     * @return MediaResponse for the track, or null if not found
     */
    @Transactional
    public MediaResponse getTrackById(String id) {
        log.debug("Getting track by ID: {}", id);

        // Check cache first
        Optional<Media> cached = mediaRepository.findById(id);
        if (cached.isPresent() && cached.get() instanceof Track track) {
            log.debug("Track found in cache: {}", id);
            return mediaMapper.trackToResponse(track);
        }

        log.debug("Track not in cache, fetching from iTunes: {}", id);
        return fetchAndCacheTrackFromItunes(id);
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

    // ==================== Eager Caching Methods ====================

    /**
     * Sync albums from iTunes results to local DB (Eager Caching).
     */
    @Transactional
    public List<Media> syncAlbums(List<ITunesResult> results) {
        return syncMedia(
                results,
                ITunesResult::collectionId,
                ITunesResult::isCollection,
                iTunesEntityMapper::toAlbumEntity
        );
    }

    /**
     * Sync tracks from iTunes results to local DB (Eager Caching).
     *
     * @param results list of iTunes results to sync
     * @return list of Media entities (from DB) corresponding to the input results
     */
    @Transactional
    public List<Media> syncTracks(List<ITunesResult> results) {
        return syncMedia(
                results,
                ITunesResult::trackId,
                ITunesResult::isTrack,
                iTunesEntityMapper::toTrackEntity
        );
    }

    /**
     * Sync artists from iTunes results to local DB (Eager Caching).
     *
     * @param results list of iTunes results to sync
     * @return list of Media entities (from DB) corresponding to the input results
     */
    @Transactional
    public List<Media> syncArtists(List<ITunesResult> results) {
        return syncMedia(
                results,
                ITunesResult::artistId,
                ITunesResult::isArtist,
                iTunesEntityMapper::toArtistEntity
        );
    }

    /**
     * Convert a list of Media entities to MediaResponse DTOs.
     *
     * @param mediaList list of Media entities
     * @return list of MediaResponse DTOs
     */
    public List<MediaResponse> toMediaResponses(List<Media> mediaList) {
        if (mediaList == null || mediaList.isEmpty()) {
            return List.of();
        }
        return mediaList.stream()
                .map(this::mapMediaToResponse)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<Media> syncMedia(
            List<ITunesResult> results,
            Function<ITunesResult, Long> idExtractor,
            Predicate<ITunesResult> typeFilter,
            Function<ITunesResult, ? extends Media> entityMapper
    ) {
        if (results == null || results.isEmpty()) {
            return List.of();
        }

        // 1. Filter by type and valid ID
        List<ITunesResult> validResults = results.stream()
                .filter(typeFilter)
                .filter(r -> idExtractor.apply(r) != null)
                .toList();

        if (validResults.isEmpty()) {
            return List.of();
        }

        // 2. Extract IDs
        List<String> ids = validResults.stream()
                .map(r -> String.valueOf(idExtractor.apply(r)))
                .toList();

        // 3. Batch fetch existing entities
        List<Media> existingEntities = mediaRepository.findAllByIdIn(ids);
        Set<String> existingIds = existingEntities.stream()
                .map(Media::getId)
                .collect(Collectors.toSet());

        log.debug("Eager caching: {} results, {} already in DB", validResults.size(), existingIds.size());

        // 4. Filter new results and map to entities
        List<Media> newEntities = validResults.stream()
                .filter(r -> !existingIds.contains(String.valueOf(idExtractor.apply(r))))
                .map(entityMapper)
                .filter(Objects::nonNull)
                .map(entity -> (Media) entity)
                .toList();

        // 5. Batch save new entities
        if (!newEntities.isEmpty()) {
            List<Media> savedEntities = mediaRepository.saveAll(newEntities);
            log.info("Eagerly cached {} new media entities", savedEntities.size());
        }

        // 6. Return all entities (re-fetch to ensure consistent state)
        return mediaRepository.findAllByIdIn(ids);
    }

    /**
     * Fetches media from iTunes and caches it to the database.
     * Auto-detects the media type from the iTunes response.
     */
    private MediaResponse fetchAndCacheFromItunes(String id) {
        try {
            Long itunesId = Long.parseLong(id);
            ITunesResponse response = iTunesClient.lookupById(itunesId);

            if (response.results() == null || response.results().isEmpty()) {
                log.debug("No results from iTunes for ID: {}", id);
                return null;
            }

            ITunesResult result = response.results().getFirst();
            Media media = mapResultToEntity(result);

            if (media == null) {
                log.warn("Could not map iTunes result to entity for ID: {}", id);
                return null;
            }

            media = mediaRepository.save(media);
            log.info("Cached new media from iTunes: {} (type: {})", id, media.getType());

            return mapMediaToResponse(media);

        } catch (NumberFormatException e) {
            log.warn("Invalid iTunes ID format: {}", id);
            return null;
        }
    }

    /**
     * Fetches an album from iTunes and caches it to the database.
     */
    private MediaResponse fetchAndCacheAlbumFromItunes(String id) {
        try {
            Long itunesId = Long.parseLong(id);
            ITunesResponse response = iTunesClient.lookupById(itunesId);

            if (response.results() == null || response.results().isEmpty()) {
                log.debug("No album found in iTunes for ID: {}", id);
                return null;
            }

            ITunesResult result = response.results().getFirst();
            if (!"collection".equals(result.wrapperType())) {
                log.debug("iTunes result for ID {} is not an album (type: {})", id, result.wrapperType());
                return null;
            }

            Album album = iTunesEntityMapper.toAlbumEntity(result);
            if (album == null) {
                return null;
            }

            album = mediaRepository.save(album);
            log.info("Cached new album from iTunes: {}", id);

            return mediaMapper.albumToResponse(album);

        } catch (NumberFormatException e) {
            log.warn("Invalid iTunes ID format: {}", id);
            return null;
        }
    }

    /**
     * Fetches an artist from iTunes and caches it to the database.
     */
    private MediaResponse fetchAndCacheArtistFromItunes(String id) {
        try {
            Long itunesId = Long.parseLong(id);
            ITunesResponse response = iTunesClient.lookupById(itunesId);

            if (response.results() == null || response.results().isEmpty()) {
                log.debug("No artist found in iTunes for ID: {}", id);
                return null;
            }

            ITunesResult result = response.results().getFirst();
            if (!"artist".equals(result.wrapperType())) {
                log.debug("iTunes result for ID {} is not an artist (type: {})", id, result.wrapperType());
                return null;
            }

            Artist artist = iTunesEntityMapper.toArtistEntity(result);
            if (artist == null) {
                return null;
            }

            artist = mediaRepository.save(artist);
            log.info("Cached new artist from iTunes: {}", id);

            return mediaMapper.artistToResponse(artist);

        } catch (NumberFormatException e) {
            log.warn("Invalid iTunes ID format: {}", id);
            return null;
        }
    }

    /**
     * Fetches a track from iTunes and caches it to the database.
     */
    private MediaResponse fetchAndCacheTrackFromItunes(String id) {
        try {
            Long itunesId = Long.parseLong(id);
            ITunesResponse response = iTunesClient.lookupById(itunesId);

            if (response.results() == null || response.results().isEmpty()) {
                log.debug("No track found in iTunes for ID: {}", id);
                return null;
            }

            ITunesResult result = response.results().getFirst();
            if (!"track".equals(result.wrapperType())) {
                log.debug("iTunes result for ID {} is not a track (type: {})", id, result.wrapperType());
                return null;
            }

            Track track = iTunesEntityMapper.toTrackEntity(result);
            if (track == null) {
                return null;
            }

            track = mediaRepository.save(track);
            log.info("Cached new track from iTunes: {}", id);

            return mediaMapper.trackToResponse(track);

        } catch (NumberFormatException e) {
            log.warn("Invalid iTunes ID format: {}", id);
            return null;
        }
    }

    /**
     * Maps an iTunes result to the appropriate entity type based on wrapperType.
     */
    private Media mapResultToEntity(ITunesResult result) {
        return switch (result.wrapperType()) {
            case "collection" -> iTunesEntityMapper.toAlbumEntity(result);
            case "track" -> iTunesEntityMapper.toTrackEntity(result);
            case "artist" -> iTunesEntityMapper.toArtistEntity(result);
            default -> {
                log.warn("Unknown iTunes wrapper type: {}", result.wrapperType());
                yield null;
            }
        };
    }

    /**
     * Maps a Media entity to the appropriate MediaResponse DTO.
     */
    private MediaResponse mapMediaToResponse(Media media) {
        return switch (media) {
            case Album album -> mediaMapper.albumToResponse(album);
            case Track track -> mediaMapper.trackToResponse(track);
            case Artist artist -> mediaMapper.artistToResponse(artist);
            default -> {
                log.warn("Unknown media type: {}", media.getClass().getSimpleName());
                yield null;
            }
        };
    }

    /**
     * Creates a minimal media entity and saves it (legacy method for library entries).
     */
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
