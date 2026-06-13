package com.poorbet.matchservice.match.config;

import com.poorbet.authstarter.auth.webclient.ServiceJwtForwardingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final OddsServiceProperties oddsServiceProperties;
    private final ServiceJwtForwardingInterceptor serviceJwtForwardingInterceptor;

    @Bean
    @Qualifier("oddsEngineRestClient")
    public RestClient oddsEngineRestClient() {
        return buildClient(oddsServiceProperties.url(), oddsServiceProperties.timeout().connect(), oddsServiceProperties.timeout().read());
    }

    private RestClient buildClient(String baseUrl, Duration connect, Duration read) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) connect.toMillis());
        factory.setReadTimeout((int) read.toMillis());

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor(serviceJwtForwardingInterceptor)
                .build();
    }
}
