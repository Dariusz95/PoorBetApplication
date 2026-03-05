package com.poorbet.teams.security;

import com.poorbet.teams.config.InternalApiAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InternalApiAuthValidator {

    public static final String API_TOKEN_HEADER = "X-API-TOKEN";
    public static final String INTERNAL_CALLER_HEADER = "X-Internal-Caller";

    private final InternalApiAuthProperties authProperties;

    public boolean isAuthorized(String token) {
        return StringUtils.hasText(token) && token.equals(authProperties.getToken());
    }
}
