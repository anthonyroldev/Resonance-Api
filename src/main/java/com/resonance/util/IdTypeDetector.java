package com.resonance.util;

import java.util.regex.Pattern;

/**
 * Utility class for detecting the type of media ID.
 * <p>
 * Spotify IDs: Base62 encoded, exactly 22 alphanumeric characters (e.g., "0f3C3g5HiVbe2znBvdEtno")
 * AudioDB IDs: Numeric only, variable length (e.g., "112024")
 */
public final class IdTypeDetector {

    private static final Pattern ROCCABEATS_ID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final Pattern AUDIODB_ID_PATTERN = Pattern.compile("^\\d+$");

    private IdTypeDetector() {
    }

    public enum IdType {
        SPOTIFY,
        AUDIODB,
        UNKNOWN
    }

    /**
     * Detects the type of the given ID.
     *
     * @param id the ID to detect
     * @return the detected IdType
     */
    public static IdType detectIdType(String id) {
        if (id == null || id.isBlank()) {
            return IdType.UNKNOWN;
        }

        if (isSpotifyId(id)) {
            return IdType.SPOTIFY;
        }

        if (isAudioDbId(id)) {
            return IdType.AUDIODB;
        }

        return IdType.UNKNOWN;
    }

    /**
     * Checks if the given ID is a Spotify ID.
     * Spotify IDs are Base62 encoded strings of exactly 22 characters.
     *
     * @param id the ID to check
     * @return true if the ID matches the Spotify ID pattern
     */
    public static boolean isSpotifyId(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        return ROCCABEATS_ID_PATTERN.matcher(id).matches();
    }

    /**
     * Checks if the given ID is an AudioDB ID.
     * AudioDB IDs are numeric strings of variable length.
     *
     * @param id the ID to check
     * @return true if the ID matches the AudioDB ID pattern
     */
    public static boolean isAudioDbId(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        return AUDIODB_ID_PATTERN.matcher(id).matches();
    }
}
