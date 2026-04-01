package com.poorbet.couponservice.config;

import com.poorbet.commons.auth.webclient.ServiceJwtForwardingFilter;
import io.netty.channel.ChannelOption;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@AllArgsConstructor
public class WebClientConfig {

    private final MatchProperties matchProperties;
    private final WalletProperties walletProperties;

    @Bean
    public WebClient matchServiceWebClientBuilder(ServiceJwtForwardingFilter serviceJwtForwardingFilter) {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(matchProperties.timeout().read())
                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        (int) matchProperties.timeout().connect().toMillis()
                );

        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(matchProperties.url())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(serviceJwtForwardingFilter)
                .build();
    }

    @Bean
    public WebClient walletServiceWebClient(ServiceJwtForwardingFilter serviceJwtForwardingFilter) {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(walletProperties.timeout().read())
                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        (int) walletProperties.timeout().connect().toMillis()
                );

        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(walletProperties.url())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(serviceJwtForwardingFilter)
                .build();
    }
}
