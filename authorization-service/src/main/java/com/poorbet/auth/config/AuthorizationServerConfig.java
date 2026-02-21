package com.poorbet.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository(
            JdbcTemplate jdbcTemplate,
            FrontendClientProperties frontendProps,
            TokenProperties tokenProps) {

        JdbcRegisteredClientRepository repository =
                new JdbcRegisteredClientRepository(jdbcTemplate);

        if (repository.findByClientId(frontendProps.getId()) == null) {

            RegisteredClient frontendClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(frontendProps.getId())
                    .clientName("PoorBet Frontend/Mobile")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUri(frontendProps.getRedirectUri())
                    .scopes(scopes -> scopes.addAll(frontendProps.getScopes()))
                    .clientSettings(ClientSettings.builder()
                            .requireProofKey(true)
                            .requireAuthorizationConsent(false)
                            .build())
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(tokenProps.getAccessTokenTtl())
                            .refreshTokenTimeToLive(tokenProps.getRefreshTokenTtl())
                            .reuseRefreshTokens(tokenProps.isReuseRefreshTokens())
                            .build())
                    .build();

            repository.save(frontendClient);
        }

        return repository;
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings(AuthServerProperties authServerProperties) {
        return AuthorizationServerSettings.builder()
                .issuer(authServerProperties.getIssuer())
                .build();
    }
}
