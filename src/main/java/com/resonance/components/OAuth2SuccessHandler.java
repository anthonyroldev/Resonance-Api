package com.resonance.components;

import com.resonance.entities.User;
import com.resonance.entities.enums.OAuthProvider;
import com.resonance.repository.UserRepository;
import com.resonance.service.JwtService;
import jakarta.servlet.http.Cookie;
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
    private final JwtService jwtService;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${spring.security.jwt.cookie-name}")
    private String cookieName;

    @Value("${spring.security.jwt.expiration}")
    private int jwtExpiration;

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
                .map(existingUser -> updateExistingUser(existingUser, provider, providerId))
                .orElseGet(() -> User.builder()
                        .email(email)
                        .username(name)
                        .oauthProvider(provider)
                        .spotifyId(provider == OAuthProvider.SPOTIFY ? providerId : null)
                        .googleId(provider == OAuthProvider.GOOGLE ? providerId : null)
                        .build());

        userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        addCookie(response, jwt);

        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/");
    }

    private User updateExistingUser(User user, OAuthProvider provider, String providerId) {
        if (provider == OAuthProvider.GOOGLE && user.getGoogleId() == null) {
            user.setGoogleId(providerId);
        } else if (provider == OAuthProvider.SPOTIFY && user.getSpotifyId() == null) {
            user.setSpotifyId(providerId);
        }
        return user;
    }

    private void addCookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie(cookieName, jwt);
        cookie.setHttpOnly(true);
        // false en dev
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(jwtExpiration / 1000);
        response.addCookie(cookie);
    }

    private String extractEmail(OAuth2User oauth2User, OAuthProvider provider) {
        return oauth2User.getAttribute("email");
    }

    private String extractName(OAuth2User oauth2User, OAuthProvider provider) {
        return switch (provider) {
            case GOOGLE, LOCAL -> oauth2User.getAttribute("name");
            case SPOTIFY -> oauth2User.getAttribute("display_name");
        };
    }

    private String extractProviderId(OAuth2User oauth2User, OAuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> oauth2User.getAttribute("sub");
            case LOCAL, SPOTIFY -> oauth2User.getAttribute("id");
        };
    }
}