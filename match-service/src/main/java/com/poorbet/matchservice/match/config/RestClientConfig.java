package com.poorbet.matchservice.match.config;

import com.poorbet.authstarter.auth.webclient.ServiceJwtForwardingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final TeamsServiceProperties teamsServiceProperties;
    private final OddsServiceProperties oddsServiceProperties;
    private final ServiceJwtForwardingInterceptor serviceJwtForwardingInterceptor;


    @Bean
    @Qualifier("teamsInternalRestClient")
    public RestClient teamsInternalRestClient() {

        return RestClient.builder()
                .baseUrl(teamsServiceProperties.url())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor(serviceJwtForwardingInterceptor)
                .build();
    }

    @Bean
    @Qualifier("oddsEngineRestClient")
    public RestClient oddsEngineRestClient() {

        return RestClient.builder()
                .baseUrl(oddsServiceProperties.url())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor(serviceJwtForwardingInterceptor)
                .build();
    }
}
