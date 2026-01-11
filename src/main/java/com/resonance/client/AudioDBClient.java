package com.resonance.client;

import com.resonance.dto.AudioDbAlbumResponseDTO;
import com.resonance.dto.AudioDbArtistResponseDTO;
import com.resonance.dto.AudioDbTrackResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AudioDBClient {

    @Value("${audiodb.base-url}")
    private String BASE_URL;

    private final WebClient webClient;

    public AudioDBClient() {
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

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

    public AudioDbAlbumResponseDTO getAlbumById(String audioDbId) {
        return webClient.get()
                .uri("/album.php?m={id}", audioDbId)
                .retrieve()
                .bodyToMono(AudioDbAlbumResponseDTO.class)
                .block();
    }

    public AudioDbTrackResponseDTO getTrackById(String audioDbTrackId) {
        return webClient.get()
                .uri("/track.php?h={id}", audioDbTrackId)
                .retrieve()
                .bodyToMono(AudioDbTrackResponseDTO.class)
                .block();
    }

    public AudioDbArtistResponseDTO getArtistById(String audioDbArtistId) {
        return webClient.get()
                .uri("/artist.php?i={id}", audioDbArtistId)
                .retrieve()
                .bodyToMono(AudioDbArtistResponseDTO.class)
                .block();
    }
}