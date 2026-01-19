package com.resonance.client;

import com.resonance.dto.audiodb.AudioDbArtistResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ReccoBeatsClient {

    @Value("${reccobeats.url}")
    private String reccoBeatsUrl;

    private final WebClient webClient;

    public ReccoBeatsClient() {
        this.webClient = WebClient.builder()
                .baseUrl(reccoBeatsUrl)
                .build();
    }

    public AudioDbArtistResponseDTO searchAlbum(String artistQuery) {
        return webClient.get()
                .uri("/artist/search", artistQuery)
                .retrieve()
                .bodyToMono(AudioDbArtistResponseDTO.class)
                .block();
    }
}
