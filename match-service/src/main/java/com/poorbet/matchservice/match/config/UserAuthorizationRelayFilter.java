package com.poorbet.matchservice.match.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

@Component
public class UserAuthorizationRelayFilter {

    public ExchangeFilterFunction relayAuthorization() {
        return (request, next) -> {
            String authorization = resolveAuthorizationHeader();

            if (!StringUtils.hasText(authorization)) {
                return next.exchange(request);
            }

            ClientRequest authorizedRequest = ClientRequest.from(request)
                    .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, authorization))
                    .build();

            return next.exchange(authorizedRequest);
        };
    }

    private String resolveAuthorizationHeader() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader(HttpHeaders.AUTHORIZATION);
        }

        return null;
    }
}
