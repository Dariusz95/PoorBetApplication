package com.poorbet.couponservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.authstarter.auth.webclient.ServiceJwtForwardingInterceptor;
import com.poorbet.commons.commons.error.ErrorResponse;
import com.poorbet.couponservice.client.wallet.WalletErrorMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@AllArgsConstructor
public class RestClientConfig {

    private final MatchProperties matchProperties;
    private final WalletProperties walletProperties;
    private final ServiceJwtForwardingInterceptor serviceJwtForwardingInterceptor;
    private final ObjectMapper objectMapper;


    @Bean
    @Qualifier("matchRestClient")
    public RestClient matchRestClient() {
        return builder(matchProperties.url(), matchProperties.timeout().connect(), matchProperties.timeout().read())
                .build();
    }

    @Bean
    @Qualifier("walletRestClient")
    public RestClient walletRestClient() {
        return builder(walletProperties.url(), walletProperties.timeout().connect(), walletProperties.timeout().read())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    ErrorResponse errorResponse = objectMapper.readValue(response.getBody(), ErrorResponse.class);
                    throw WalletErrorMapper.map(errorResponse);
                })
                .build();
    }

    private RestClient.Builder builder(String baseUrl, Duration connect, Duration read) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) connect.toMillis());
        factory.setReadTimeout((int) read.toMillis());

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor(serviceJwtForwardingInterceptor);
    }
}
