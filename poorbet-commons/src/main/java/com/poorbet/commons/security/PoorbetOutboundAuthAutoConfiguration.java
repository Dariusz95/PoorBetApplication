package com.poorbet.commons.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

@AutoConfiguration
@ConditionalOnClass({ ExchangeFilterFunction.class, ClientRequest.class })
@ConditionalOnProperty(prefix = "poorbet.outbound-auth", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(PoorbetOutboundAuthProperties.class)
public class PoorbetOutboundAuthAutoConfiguration {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @org.springframework.context.annotation.Bean("poorbetOutboundAuthFilter")
    @ConditionalOnMissingBean(name = "poorbetOutboundAuthFilter")
    public ExchangeFilterFunction poorbetOutboundAuthFilter(PoorbetOutboundAuthProperties properties) {
        return (request, next) -> {
            ClientRequest.Builder builder = ClientRequest.from(request);

            String relayedAuthorization = properties.isRelayIncomingAuthorization() ? resolveAuthorizationHeader() : null;
            if (StringUtils.hasText(relayedAuthorization)) {
                builder.headers(headers -> headers.set(AUTHORIZATION_HEADER, relayedAuthorization));
            } else if (StringUtils.hasText(properties.getServiceToken())) {
                builder.headers(headers -> headers.setBearerAuth(properties.getServiceToken()));
            }

            return next.exchange(builder.build());
        };
    }

    private String resolveAuthorizationHeader() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader(AUTHORIZATION_HEADER);
        }
        return null;
    }
}
