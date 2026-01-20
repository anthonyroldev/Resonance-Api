package com.resonance.service;

import com.resonance.client.AudioDBClient;
import com.resonance.client.ReccoBeatsClient;
import com.resonance.dto.audiodb.*;
import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.reccobeats.ReccoBeatsAlbumDTO;
import com.resonance.dto.reccobeats.ReccoBeatsArtistDTO;
import com.resonance.entities.Media;
import com.resonance.entities.enums.MediaType;
import com.resonance.entities.media.Album;
import com.resonance.entities.media.Artist;
import com.resonance.entities.media.Track;
import com.resonance.mapper.AudioDbMapper;
import com.resonance.mapper.MediaMapper;
import com.resonance.mapper.ReccoBeatsMapper;
import com.resonance.repository.MediaRepository;
import com.resonance.util.IdTypeDetector;
import com.resonance.util.IdTypeDetector.IdType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final AudioDBClient audioDBClient;
    private final ReccoBeatsClient reccoBeatsClient;
    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;
    private final AudioDbMapper audioDbMapper;
    private final ReccoBeatsMapper reccoBeatsMapper;

    /**
     * Get album by ID. Automatically detects ID type and routes to appropriate API.
     * Checks cache first.
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
        return fetchAlbumFromExternalApi(id);
    }

    /**
     * Get artist by ID. Automatically detects ID type and routes to appropriate API.
     * Checks cache first.
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
        return fetchArtistFromExternalApi(id);
    }

    /**
     * Get track by ID. Automatically detects ID type and routes to appropriate API.
     * Checks cache first.
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
        return fetchTrackFromExternalApi(id);
    }

    /**
     * Get or create a media entity, enriching with AudioDB data if available.
     */
    @Transactional
    public Media getOrCreateMedia(String spotifyId, MediaType type, String title, String artistName) {
        return mediaRepository.findById(spotifyId)
                .orElseGet(() -> {
                    log.info("Creating new media entry for Spotify ID: {}", spotifyId);
                    return createAndCacheMedia(spotifyId, type, title, artistName);
                });
    }

    /**
     * Update media rating statistics.
     */
    public void updateMediaRatingStats(Media media, Integer newRating) {
        if (newRating == null) return;

        // This is a simplified implementation - in production you'd want to
        // recalculate based on all ratings for this media
        log.debug("Should update rating stats for media: {}", media.getId());
    }


    private MediaResponse fetchAlbumFromExternalApi(String id) {
        IdType idType = IdTypeDetector.detectIdType(id);

        return switch (idType) {
            case SPOTIFY -> fetchAlbumFromReccoBeats(id);
            case AUDIODB -> fetchAlbumFromAudioDb(id);
            case UNKNOWN -> {
                log.warn("Unknown ID type for album: {}", id);
                yield null;
            }
        };
    }

    private MediaResponse fetchArtistFromExternalApi(String id) {
        IdType idType = IdTypeDetector.detectIdType(id);

        return switch (idType) {
            case SPOTIFY -> fetchArtistFromReccoBeats(id);
            case AUDIODB -> fetchArtistFromAudioDb(id);
            case UNKNOWN -> {
                log.warn("Unknown ID type for artist: {}", id);
                yield null;
            }
        };
    }

    private MediaResponse fetchTrackFromExternalApi(String id) {
        IdType idType = IdTypeDetector.detectIdType(id);

        return switch (idType) {
            case SPOTIFY -> {
                // ReccoBeats doesn't have a track endpoint, return null
                log.debug("ReccoBeats doesn't support track lookup by Spotify ID");
                yield null;
            }
            case AUDIODB -> fetchTrackFromAudioDb(id);
            case UNKNOWN -> {
                log.warn("Unknown ID type for track: {}", id);
                yield null;
            }
        };
    }

    private MediaResponse fetchAlbumFromReccoBeats(String spotifyId) {
        try {
            log.debug("Fetching album from ReccoBeats API: {}", spotifyId);
            ReccoBeatsAlbumDTO dto = reccoBeatsClient.getAlbumById(spotifyId);
            if (dto != null) {
                return reccoBeatsMapper.albumToResponse(dto);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch album from ReccoBeats: {}", e.getMessage());
        }
        return null;
    }

    private MediaResponse fetchArtistFromReccoBeats(String spotifyId) {
        try {
            log.debug("Fetching artist from ReccoBeats API: {}", spotifyId);
            ReccoBeatsArtistDTO dto = reccoBeatsClient.getArtistById(spotifyId);
            if (dto != null) {
                return reccoBeatsMapper.artistToResponse(dto);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch artist from ReccoBeats: {}", e.getMessage());
        }
        return null;
    }

    private MediaResponse fetchAlbumFromAudioDb(String audioDbId) {
        try {
            log.debug("Fetching album from AudioDB API: {}", audioDbId);
            AudioDbAlbumResponseDTO response = audioDBClient.getAlbumById(audioDbId);
            if (response != null && response.album() != null && !response.album().isEmpty()) {
                AudioDbAlbumDTO dto = response.album().getFirst();
                return audioDbMapper.albumToResponse(dto);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch album from AudioDB: {}", e.getMessage());
        }
        return null;
    }

    private MediaResponse fetchArtistFromAudioDb(String audioDbId) {
        try {
            log.debug("Fetching artist from AudioDB API: {}", audioDbId);
            AudioDbArtistResponseDTO response = audioDBClient.getArtistById(audioDbId);
            if (response != null && response.artists() != null && !response.artists().isEmpty()) {
                AudioDbArtistDTO dto = response.artists().getFirst();
                return audioDbMapper.artistToResponse(dto);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch artist from AudioDB: {}", e.getMessage());
        }
        return null;
    }

    private MediaResponse fetchTrackFromAudioDb(String audioDbId) {
        try {
            log.debug("Fetching track from AudioDB API: {}", audioDbId);
            AudioDbTrackResponseDTO response = audioDBClient.getTrackById(audioDbId);
            if (response != null && response.track() != null && !response.track().isEmpty()) {
                AudioDbTrackDTO dto = response.track().getFirst();
                return audioDbMapper.trackToResponse(dto);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch track from AudioDB: {}", e.getMessage());
        }
        return null;
    }


    private Media createAndCacheMedia(String spotifyId, MediaType type, String title, String artistName) {
        Media media = switch (type) {
            case ALBUM -> createAlbum(spotifyId, title, artistName);
            case ARTIST -> createArtist(spotifyId, artistName);
            case TRACK -> createTrack(spotifyId, title, artistName);
        };

        return mediaRepository.save(media);
    }

    private Album createAlbum(String spotifyId, String title, String artistName) {
        Album.AlbumBuilder<?, ?> builder = Album.builder()
                .id(spotifyId)
                .title(title != null ? title : "Unknown Album")
                .artistName(artistName != null ? artistName : "Unknown Artist");

        // Try to enrich with AudioDB data
        if (title != null && artistName != null) {
            try {
                AudioDbAlbumResponseDTO audioDbResponse = audioDBClient.searchAlbum(title, artistName);
                if (audioDbResponse != null && audioDbResponse.album() != null && !audioDbResponse.album().isEmpty()) {
                    AudioDbAlbumDTO audioDbAlbum = audioDbResponse.album().getFirst();
                    builder.audioDbAlbumId(audioDbAlbum.idAlbum())
                            .audioDbArtistId(audioDbAlbum.idArtist())
                            .description(audioDbAlbum.description())
                            .genre(audioDbAlbum.genre())
                            .style(audioDbAlbum.style())
                            .mood(audioDbAlbum.mood())
                            .label(audioDbAlbum.label())
                            .imageUrl(audioDbAlbum.imageUrl())
                            .releaseDate(audioDbAlbum.yearReleased());
                    log.debug("Enriched album with AudioDB data");
                }
            } catch (Exception e) {
                log.warn("Failed to fetch AudioDB data for album: {}", e.getMessage());
            }
        }

        return builder.build();
    }

    private Artist createArtist(String spotifyId, String artistName) {
        Artist.ArtistBuilder<?, ?> builder = Artist.builder()
                .id(spotifyId)
                .title(artistName != null ? artistName : "Unknown Artist")
                .artistName(artistName != null ? artistName : "Unknown Artist");

        // Try to enrich with AudioDB data
        if (artistName != null) {
            try {
                AudioDbArtistResponseDTO audioDbResponse = audioDBClient.searchArtist(artistName);
                if (audioDbResponse != null && audioDbResponse.artists() != null && !audioDbResponse.artists().isEmpty()) {
                    AudioDbArtistDTO audioDbArtist = audioDbResponse.artists().getFirst();
                    builder.audioDbArtistId(audioDbArtist.idArtist())
                            .description(audioDbArtist.biography())
                            .genre(audioDbArtist.genre())
                            .style(audioDbArtist.style())
                            .mood(audioDbArtist.mood())
                            .gender(audioDbArtist.gender())
                            .country(audioDbArtist.country())
                            .formedYear(audioDbArtist.formedYear())
                            .imageUrl(audioDbArtist.imageUrl());
                    log.debug("Enriched artist with AudioDB data");
                }
            } catch (Exception e) {
                log.warn("Failed to fetch AudioDB data for artist: {}", e.getMessage());
            }
        }

        return builder.build();
    }

    private Track createTrack(String spotifyId, String title, String artistName) {
        Track.TrackBuilder<?, ?> builder = Track.builder()
                .id(spotifyId)
                .title(title != null ? title : "Unknown Track")
                .artistName(artistName != null ? artistName : "Unknown Artist");

        // Try to enrich with AudioDB data
        if (title != null && artistName != null) {
            try {
                AudioDbTrackResponseDTO audioDbResponse = audioDBClient.searchTrack(title, artistName);
                if (audioDbResponse != null && audioDbResponse.track() != null && !audioDbResponse.track().isEmpty()) {
                    AudioDbTrackDTO audioDbTrack = audioDbResponse.track().getFirst();
                    builder.audioDbTrackId(audioDbTrack.idTrack())
                            .audioDbAlbumId(audioDbTrack.idAlbum())
                            .audioDbArtistId(audioDbTrack.idArtist())
                            .description(audioDbTrack.description())
                            .genre(audioDbTrack.genre())
                            .thumbnailUrl(audioDbTrack.thumbnailUrl());

                    if (audioDbTrack.duration() != null) {
                        try {
                            builder.duration(Integer.parseInt(audioDbTrack.duration()));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    if (audioDbTrack.trackNumber() != null) {
                        try {
                            builder.trackNumber(Integer.parseInt(audioDbTrack.trackNumber()));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    log.debug("Enriched track with AudioDB data");
                }
            } catch (Exception e) {
                log.warn("Failed to fetch AudioDB data for track: {}", e.getMessage());
            }
        }

        return builder.build();
    }
}
