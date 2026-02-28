package com.poorbet.commons.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@AutoConfiguration
@ConditionalOnClass({ HttpSecurity.class, NimbusJwtDecoder.class })
@ConditionalOnProperty(prefix = "poorbet.security", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableMethodSecurity
@EnableConfigurationProperties(PoorbetSecurityProperties.class)
public class PoorbetSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, PoorbetSecurityProperties properties,
            JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    if (!properties.getProtectedPaths().isEmpty()) {
                        auth.requestMatchers(properties.getProtectedPaths().toArray(new String[0])).authenticated();
                    }
                    auth.anyRequest().permitAll();
                })
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtDecoder jwtDecoder(PoorbetSecurityProperties properties) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(properties.getJwkSetUri()).build();
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(properties.getIssuer());
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer));
        return decoder;
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new PermissionClaimConverter());
        return converter;
    }

    static class PermissionClaimConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            List<GrantedAuthority> authorities = new ArrayList<>();

            List<String> permissions = jwt.getClaimAsStringList("permissions");
            if (permissions != null) {
                permissions.stream().map(SimpleGrantedAuthority::new).forEach(authorities::add);
            }

            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                roles.stream().map(role -> "ROLE_" + role).map(SimpleGrantedAuthority::new).forEach(authorities::add);
            }

            return authorities;
        }
    }
}
