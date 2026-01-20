package com.resonance.service;

import com.resonance.client.AudioDBClient;
import com.resonance.client.ReccoBeatsClient;
import com.resonance.dto.audiodb.AudioDbTrackResponseDTO;
import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.media.SearchResponse;
import com.resonance.dto.reccobeats.ReccoBeatsAlbumDTO;
import com.resonance.dto.reccobeats.ReccoBeatsArtistDTO;
import com.resonance.dto.reccobeats.ReccoBeatsSearchResponseDTO;
import com.resonance.mapper.AudioDbMapper;
import com.resonance.mapper.ReccoBeatsMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ReccoBeatsClient reccoBeatsClient;
    private final AudioDBClient audioDBClient;
    private final ReccoBeatsMapper reccoBeatsMapper;
    private final AudioDbMapper audioDbMapper;

    /**
     * Search albums by query. Returns results from ReccoBeats API.
     */
    public SearchResponse<MediaResponse> searchAlbums(String query) {
        log.debug("Searching albums with query: {}", query);
        ReccoBeatsSearchResponseDTO<ReccoBeatsAlbumDTO> response = reccoBeatsClient.searchAlbum(query);

        List<MediaResponse> albums = reccoBeatsMapper.albumsToResponses(response.content());

        return buildSearchResponse(albums, response.page(), response.size(), response.totalElements(), response.totalPages());
    }

    /**
     * Search artists by query. Returns results from ReccoBeats API.
     */
    public SearchResponse<MediaResponse> searchArtists(String query) {
        log.debug("Searching artists with query: {}", query);
        ReccoBeatsSearchResponseDTO<ReccoBeatsArtistDTO> response = reccoBeatsClient.searchArtist(query);

        List<MediaResponse> artists = reccoBeatsMapper.artistsToResponses(response.content());

        return buildSearchResponse(artists, response.page(), response.size(), response.totalElements(), response.totalPages());
    }

    /**
     * Search tracks by artist name and track name using AudioDB API.
     */
    public SearchResponse<MediaResponse> searchTracks(String artist, String track) {
        log.debug("Searching tracks with artist: {} and track: {}", artist, track);
        AudioDbTrackResponseDTO response = audioDBClient.searchTrack(track, artist);

        if (response == null || response.track() == null) {
            return SearchResponse.<MediaResponse>builder()
                    .content(List.of())
                    .page(0)
                    .size(0)
                    .totalElements(0)
                    .totalPages(0)
                    .build();
        }

        List<MediaResponse> tracks = audioDbMapper.tracksToResponses(response.track());

        return SearchResponse.<MediaResponse>builder()
                .content(tracks)
                .page(0)
                .size(tracks.size())
                .totalElements(tracks.size())
                .totalPages(1)
                .build();
    }

    /**
     * Build a SearchResponse with null-safe pagination values.
     */
    private SearchResponse<MediaResponse> buildSearchResponse(
            List<MediaResponse> content,
            Integer page,
            Integer size,
            Integer totalElements,
            Integer totalPages) {
        return SearchResponse.<MediaResponse>builder()
                .content(content)
                .page(page != null ? page : 0)
                .size(size != null ? size : content.size())
                .totalElements(totalElements != null ? totalElements : content.size())
                .totalPages(totalPages != null ? totalPages : 1)
                .build();
    }
}
