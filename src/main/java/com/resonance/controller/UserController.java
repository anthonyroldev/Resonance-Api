package com.resonance.controller;

import com.resonance.dto.auth.AuthResponse;
import com.resonance.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<AuthResponse> getUser(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(userService.getUser(user));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetails user) {
        userService.deleteUser(user);
        return ResponseEntity.ok()
                .build();
    }
}
