package com.resonance.service;

import com.resonance.dto.auth.AuthResponse;
import com.resonance.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public AuthResponse getUser(UserDetails user) {
        var foundUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return AuthResponse.builder()
                .email(foundUser.getEmail())
                .username(foundUser.getUsername())
                .build();
    }
}
