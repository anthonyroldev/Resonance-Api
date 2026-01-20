package com.resonance.client;

import com.resonance.dto.audiodb.AudioDbAlbumResponseDTO;
import com.resonance.dto.audiodb.AudioDbArtistResponseDTO;
import com.resonance.dto.audiodb.AudioDbTrackResponseDTO;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AudioDBClient {

    @Value("${audiodb.base-url}")
    private String baseUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Search artist by name.
     * Endpoint: /search.php?s={artistName}
     */
    public AudioDbArtistResponseDTO searchArtist(String artistName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search.php")
                        .queryParam("s", artistName)
                        .build())
                .retrieve()
                .bodyToMono(AudioDbArtistResponseDTO.class)
                .block();
    }

    /**
     * Search album by artist name and album name.
     * Endpoint: /searchalbum.php?s={artistName}&a={albumName}
     */
    public AudioDbAlbumResponseDTO searchAlbum(String albumName, String artistName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/searchalbum.php")
                        .queryParam("s", artistName)
                        .queryParam("a", albumName)
                        .build())
                .retrieve()
                .bodyToMono(AudioDbAlbumResponseDTO.class)
                .block();
    }

    /**
     * Search track by artist name and track name.
     * Endpoint: /searchtrack.php?s={artistName}&t={trackName}
     */
    public AudioDbTrackResponseDTO searchTrack(String trackName, String artistName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/searchtrack.php")
                        .queryParam("s", artistName)
                        .queryParam("t", trackName)
                        .build())
                .retrieve()
                .bodyToMono(AudioDbTrackResponseDTO.class)
                .block();
    }

    /**
     * Get album by AudioDB ID.
     * Endpoint: /album.php?m={id}
     */
    public AudioDbAlbumResponseDTO getAlbumById(String audioDbId) {
        return webClient.get()
                .uri("/album.php?m={id}", audioDbId)
                .retrieve()
                .bodyToMono(AudioDbAlbumResponseDTO.class)
                .block();
    }

    /**
     * Get track by AudioDB ID.
     * Endpoint: /track.php?h={id}
     */
    public AudioDbTrackResponseDTO getTrackById(String audioDbTrackId) {
        return webClient.get()
                .uri("/track.php?h={id}", audioDbTrackId)
                .retrieve()
                .bodyToMono(AudioDbTrackResponseDTO.class)
                .block();
    }

    /**
     * Get artist by AudioDB ID.
     * Endpoint: /artist.php?i={id}
     */
    public AudioDbArtistResponseDTO getArtistById(String audioDbArtistId) {
        return webClient.get()
                .uri("/artist.php?i={id}", audioDbArtistId)
                .retrieve()
                .bodyToMono(AudioDbArtistResponseDTO.class)
                .block();
    }
}
