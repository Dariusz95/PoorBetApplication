package com.poorbet.matchservice.match.config;

import com.poorbet.authstarter.auth.config.AuthServiceProperties;
import com.poorbet.authstarter.auth.token.ServiceTokenProvider;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final OddsServiceProperties oddsServiceProperties;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ServiceJwtForwardingFilter serviceJwtForwardingFilter(ServiceTokenProvider provider) {
        return new ServiceJwtForwardingFilter(provider);
    }


    @Bean
    @Qualifier("oddsEngineWebClient")
    public WebClient oddsEngineWebClient(ServiceJwtForwardingFilter serviceJwtForwardingFilter) {
        return webClientBuilder()
                .baseUrl(oddsServiceProperties.url())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(oddsServiceProperties.timeout().read())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) oddsServiceProperties.timeout().connect().toMillis())
                ))
                .filter(serviceJwtForwardingFilter)
                .build();
    }
}
