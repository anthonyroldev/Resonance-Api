package com.resonance.external.itunes;

import com.resonance.dto.media.MediaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for iTunes music metadata.
 * Implements MusicMetadataProvider using the iTunes Search API.
 */
@Service
public final class ITunesService implements MusicMetadataProvider {

    private static final Logger log = LoggerFactory.getLogger(ITunesService.class);

    private final ITunesClient iTunesClient;
    private final ITunesMediaMapper iTunesMediaMapper;

    public ITunesService(ITunesClient iTunesClient, ITunesMediaMapper iTunesMediaMapper) {
        this.iTunesClient = iTunesClient;
        this.iTunesMediaMapper = iTunesMediaMapper;
    }

    @Override
    public List<MediaResponse> searchAlbums(String query) {
        log.debug("Searching albums via iTunes for query: {}", query);
        ITunesResponse response = iTunesClient.searchAlbums(query);
        return iTunesMediaMapper.toAlbumResponses(response.results());
    }

    @Override
    public List<MediaResponse> searchTracks(String query) {
        log.debug("Searching tracks via iTunes for query: {}", query);
        ITunesResponse response = iTunesClient.searchTracks(query);
        return iTunesMediaMapper.toTrackResponses(response.results());
    }

    @Override
    public List<MediaResponse> searchArtists(String query) {
        log.debug("Searching artists via iTunes for query: {}", query);
        ITunesResponse response = iTunesClient.searchArtists(query);
        return iTunesMediaMapper.toArtistResponses(response.results());
    }

    @Override
    public MediaResponse lookupById(String id) {
        log.debug("Looking up media via iTunes for ID: {}", id);
        try {
            Long itunesId = Long.parseLong(id);
            ITunesResponse response = iTunesClient.lookupById(itunesId);

            if (response.results() == null || response.results().isEmpty()) {
                log.debug("No results found for iTunes ID: {}", id);
                return null;
            }

            ITunesResult result = response.results().getFirst();

            // Determine result type and map accordingly
            return switch (result.wrapperType()) {
                case "collection" -> iTunesMediaMapper.toAlbumResponse(result);
                case "track" -> iTunesMediaMapper.toTrackResponse(result);
                case "artist" -> iTunesMediaMapper.toArtistResponse(result);
                default -> {
                    log.warn("Unknown wrapper type: {}", result.wrapperType());
                    yield iTunesMediaMapper.toAlbumResponse(result);
                }
            };

        } catch (NumberFormatException e) {
            log.warn("Invalid iTunes ID format: {}", id);
            return null;
        }
    }
}
