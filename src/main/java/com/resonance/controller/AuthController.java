package com.resonance.controller;

import com.resonance.controller.doc.AuthControllerDoc;
import com.resonance.dto.auth.AuthResponse;
import com.resonance.dto.auth.LoginRequest;
import com.resonance.dto.auth.RegisterRequest;
import com.resonance.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDoc {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, response));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }
}
