package com.poorbet.walletservice.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CurrentUserProvider Unit Tests")
class CurrentUserProviderTest {

    private final CurrentUserProvider currentUserProvider = new CurrentUserProvider();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Jwt jwtWithUid(UUID userId) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("uid", userId.toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
    }

    @Test
    @DisplayName("Should extract userId from the 'uid' claim of the authenticated JWT")
    void shouldExtractUserIdFromJwt() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Jwt jwt = jwtWithUid(userId);
        var authentication = new TestingAuthenticationToken(jwt, null);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        UUID result = currentUserProvider.getUserId();

        // Assert
        assertThat(result).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should throw when there is no authentication in the security context")
    void shouldThrowWhenNoAuthentication() {
        // Act & Assert
        assertThatThrownBy(currentUserProvider::getUserId)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should throw when authentication is present but not authenticated")
    void shouldThrowWhenNotAuthenticated() {
        // Arrange
        Jwt jwt = jwtWithUid(UUID.randomUUID());
        var authentication = new TestingAuthenticationToken(jwt, null);
        authentication.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        assertThatThrownBy(currentUserProvider::getUserId)
                .isInstanceOf(IllegalStateException.class);
    }
}
