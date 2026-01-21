package com.resonance.controller.doc;

import com.resonance.dto.auth.AuthResponse;
import com.resonance.dto.auth.LoginRequest;
import com.resonance.dto.auth.RegisterRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Schema(description = "Authentification par email et mot de passe.")
public interface AuthControllerDoc {
    @Schema(description = "Inscrit un nouvel utilisateur et place le token dans un cookie AUTH_TOKEN.")
    ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response);

    @Schema(description = "Authentifie un utilisateur existant et place le token dans un cookie AUTH_TOKEN.")
    ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response);
}
