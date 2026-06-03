package com.poorbet.couponservice.config;

import com.poorbet.authstarter.auth.webclient.ServiceJwtForwardingInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
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


    @Bean
    @Qualifier("matchRestClient")
    public RestClient matchRestClient() {
        return buildClient(matchProperties.url(), matchProperties.timeout().connect(), matchProperties.timeout().read());
    }

    @Bean
    @Qualifier("walletRestClient")
    public RestClient walletRestClient() {
        return buildClient(walletProperties.url(), walletProperties.timeout().connect(), walletProperties.timeout().read());
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
