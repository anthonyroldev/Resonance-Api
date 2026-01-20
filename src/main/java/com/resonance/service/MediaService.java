package com.resonance.service;

import com.resonance.client.AudioDBClient;
import com.resonance.dto.audiodb.AudioDbAlbumDTO;
import com.resonance.dto.audiodb.AudioDbAlbumResponseDTO;
import com.resonance.dto.audiodb.AudioDbArtistDTO;
import com.resonance.dto.audiodb.AudioDbArtistResponseDTO;
import com.resonance.dto.audiodb.AudioDbTrackDTO;
import com.resonance.dto.audiodb.AudioDbTrackResponseDTO;
import com.resonance.dto.media.MediaResponse;
import com.resonance.entities.Media;
import com.resonance.entities.enums.MediaType;
import com.resonance.entities.media.Album;
import com.resonance.entities.media.Artist;
import com.resonance.entities.media.Track;
import com.resonance.mapper.MediaMapper;
import com.resonance.repository.MediaRepository;

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
    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;

    /**
     * Get album by Spotify ID. Checks cache first.
     */
    @Transactional(readOnly = true)
    public MediaResponse getAlbumById(String spotifyId) {
        log.debug("Getting album by ID: {}", spotifyId);

        Optional<Media> cached = mediaRepository.findById(spotifyId);
        if (cached.isPresent() && cached.get() instanceof Album album) {
            log.debug("Album found in cache: {}", spotifyId);
            return mediaMapper.albumToResponse(album);
        }

        log.debug("Album not found in cache: {}", spotifyId);
        return null;
    }

    /**
     * Get artist by Spotify ID. Checks cache first.
     */
    @Transactional(readOnly = true)
    public MediaResponse getArtistById(String spotifyId) {
        log.debug("Getting artist by ID: {}", spotifyId);

        Optional<Media> cached = mediaRepository.findById(spotifyId);
        if (cached.isPresent() && cached.get() instanceof Artist artist) {
            log.debug("Artist found in cache: {}", spotifyId);
            return mediaMapper.artistToResponse(artist);
        }

        log.debug("Artist not found in cache: {}", spotifyId);
        return null;
    }

    /**
     * Get track by Spotify ID. Checks cache first.
     */
    @Transactional(readOnly = true)
    public MediaResponse getTrackById(String spotifyId) {
        log.debug("Getting track by ID: {}", spotifyId);

        Optional<Media> cached = mediaRepository.findById(spotifyId);
        if (cached.isPresent() && cached.get() instanceof Track track) {
            log.debug("Track found in cache: {}", spotifyId);
            return mediaMapper.trackToResponse(track);
        }

        log.debug("Track not found in cache: {}", spotifyId);
        return null;
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

    // ==================== PRIVATE HELPER METHODS ====================

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
