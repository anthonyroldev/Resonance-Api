package com.resonance.client;

import com.resonance.dto.reccobeats.ReccoBeatsAlbumDTO;
import com.resonance.dto.reccobeats.ReccoBeatsArtistDTO;
import com.resonance.dto.reccobeats.ReccoBeatsSearchResponseDTO;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ReccoBeatsClient {

    @Value("${reccobeats.url}")
    private String baseUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Search artists by name query.
     * Endpoint: /v1/artist/search?name={query}
     */
    public ReccoBeatsSearchResponseDTO<ReccoBeatsArtistDTO> searchArtist(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/artist/search")
                        .queryParam("name", query)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ReccoBeatsSearchResponseDTO<ReccoBeatsArtistDTO>>() {})
                .block();
    }

    /**
     * Get artist by ReccoBeats ID.
     * Endpoint: /v1/artist/{id}
     */
    public ReccoBeatsArtistDTO getArtistById(String id) {
        return webClient.get()
                .uri("/artist/{id}", id)
                .retrieve()
                .bodyToMono(ReccoBeatsArtistDTO.class)
                .block();
    }

    // ==================== ALBUM ENDPOINTS ====================

    /**
     * Search albums by name query.
     * Endpoint: /v1/album/search?name={query}
     */
    public ReccoBeatsSearchResponseDTO<ReccoBeatsAlbumDTO> searchAlbum(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/album/search")
                        .queryParam("name", query)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ReccoBeatsSearchResponseDTO<ReccoBeatsAlbumDTO>>() {})
                .block();
    }

    /**
     * Get album by ReccoBeats ID.
     * Endpoint: /v1/album/{id}
     */
    public ReccoBeatsAlbumDTO getAlbumById(String id) {
        return webClient.get()
                .uri("/album/{id}", id)
                .retrieve()
                .bodyToMono(ReccoBeatsAlbumDTO.class)
                .block();
    }
}
