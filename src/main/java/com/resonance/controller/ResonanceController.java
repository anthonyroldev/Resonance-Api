package com.resonance.controller;

import com.resonance.client.AudioDBClient;
import com.resonance.client.ReccoBeatsClient;
import com.resonance.controller.doc.ResonanceControllerDoc;
import com.resonance.dto.audiodb.AudioDbArtistResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ResonanceController implements ResonanceControllerDoc {

    private final AudioDBClient audioDBClient;

    private final ReccoBeatsClient reccoBeatsClient;

    @GetMapping(value = "/music", version = "1")
    public AudioDbArtistResponseDTO searchAlbum(@RequestParam String artistQuery) {
        return reccoBeatsClient.searchAlbum(artistQuery);
    }
}
