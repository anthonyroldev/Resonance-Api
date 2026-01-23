package com.resonance.controller;

import com.resonance.controller.doc.MediaControllerDoc;
import com.resonance.dto.media.MediaResponse;
import com.resonance.service.MediaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media")
public class MediaController implements MediaControllerDoc {

    private final MediaService mediaService;

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getMediaById(@PathVariable String id) {
        MediaResponse response = mediaService.getMediaById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
