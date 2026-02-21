package com.poorbet.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;

@Configuration
public class TokenCustomizationConfig {

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer(AuthServerProperties authServerProperties) {
        return context -> {
            JwtClaimsSet.Builder claims = context.getClaims();
            List<String> roles = context.getPrincipal().getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                claims
                        .audience(List.of(authServerProperties.getAudience()))
                        .claim("scope", String.join(" ", context.getAuthorizedScopes()))
                        .claim("roles", roles);
            }

            if ("id_token".equals(context.getTokenType().getValue())) {
                claims.claim("roles", roles);
            }
        };
    }
}
