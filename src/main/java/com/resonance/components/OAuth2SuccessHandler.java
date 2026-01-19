package com.resonance.components;

import com.resonance.entities.User;
import com.resonance.entities.enums.OAuthProvider;
import com.resonance.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        OAuth2User oauth2User = oauthToken.getPrincipal();

        OAuthProvider provider = OAuthProvider.valueOf(registrationId.toUpperCase());

        String email = extractEmail(oauth2User, provider);
        String name = extractName(oauth2User, provider);
        String providerId = extractProviderId(oauth2User, provider);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> User.builder()
                        .email(email)
                        .username(name)
                        .oauthProvider(provider)
                        .spotifyId(provider == OAuthProvider.SPOTIFY ? providerId : null)
                        .googleId(provider == OAuthProvider.GOOGLE ? providerId : null)
                        .build());

        userRepository.save(user);

        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/dashboard");
    }

    private String extractEmail(OAuth2User oauth2User, OAuthProvider provider) {
        return oauth2User.getAttribute("email");
    }

    private String extractName(OAuth2User oauth2User, OAuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> oauth2User.getAttribute("name");
            case SPOTIFY -> oauth2User.getAttribute("display_name");
        };
    }

    private String extractProviderId(OAuth2User oauth2User, OAuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> oauth2User.getAttribute("sub");
            case SPOTIFY -> oauth2User.getAttribute("id");
        };
    }
}