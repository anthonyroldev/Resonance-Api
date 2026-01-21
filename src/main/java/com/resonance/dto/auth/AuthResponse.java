package com.resonance.dto.auth;

import lombok.Builder;

@Builder
public record AuthResponse(
        String email,
        String username
) {}
