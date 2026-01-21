package com.resonance.service;

import com.resonance.dto.auth.AuthResponse;
import com.resonance.dto.auth.LoginRequest;
import com.resonance.dto.auth.RegisterRequest;
import com.resonance.entities.User;
import com.resonance.entities.enums.OAuthProvider;
import com.resonance.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${spring.security.jwt.cookie-name}")
    private String cookieName;

    @Value("${spring.security.jwt.expiration}")
    private int jwtExpiration;

    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already in use");
        }

        User user = User.builder()
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .oauthProvider(OAuthProvider.LOCAL)
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        addCookie(response, token);
        log.info("New user registered: {}", user.getEmail());
        return new AuthResponse(user.getEmail(), user.getUsername());
    }

    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        String token = jwtService.generateToken(user);
        addCookie(response, token);
        log.info("User logged in: {}", user.getEmail());
        return new AuthResponse(user.getEmail(), user.getUsername());
    }

    private void addCookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie(cookieName, jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // false for dev
        cookie.setPath("/");
        cookie.setMaxAge(jwtExpiration / 1000);
        response.addCookie(cookie);
    }
}
