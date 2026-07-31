package com.poorbet.gateway.config;

import com.poorbet.authstarter.security.PoorbetSecurityProperties;
import com.poorbet.authstarter.security.PoorbetTokenTypes;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(PoorbetSecurityProperties.class)
public class GatewaySecurityConfig {

    @Bean
    @ConditionalOnProperty(prefix = "poorbet.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveJwtDecoder jwtDecoder,
                                                         PoorbetSecurityProperties properties) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(auth -> {
                    properties.getUnprotectedPaths()
                            .forEach(path -> auth.pathMatchers(path).permitAll());
                    auth.pathMatchers("/api/**").access(GatewaySecurityConfig::hasUserTokenType);
                    auth.anyExchange().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtDecoder(jwtDecoder))
                );

        return http.build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "poorbet.security", name = "enabled", havingValue = "false")
    public SecurityWebFilterChain permitAllSecurityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(auth -> auth.anyExchange().permitAll());

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(PoorbetSecurityProperties properties) {
        return NimbusReactiveJwtDecoder.withJwkSetUri(properties.getJwkSetUri()).build();
    }

    private static Mono<AuthorizationDecision> hasUserTokenType(Mono<Authentication> authentication,
                                                                  AuthorizationContext context) {
        return authentication
                .map(auth -> auth.getPrincipal() instanceof Jwt jwt
                        && PoorbetTokenTypes.USER.equals(jwt.getClaimAsString("token_type")))
                .defaultIfEmpty(false)
                .map(AuthorizationDecision::new);
    }
}
