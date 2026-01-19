package com.resonance.dto.auth;

public record AuthResponse(
        String token,
        String email,
        String username
) {}
