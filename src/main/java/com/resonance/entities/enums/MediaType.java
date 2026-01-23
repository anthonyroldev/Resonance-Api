package com.resonance.entities.enums;

import lombok.Getter;

@Getter
public enum MediaType {
    ALBUM("album"),
    TRACK("musicTrack"),
    ARTIST("musicArtist");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

}