package com.poorbet.matchservice.match.config;

import com.poorbet.commons.auth.webclient.ServiceJwtForwardingFilter;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
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

    private final TeamsServiceProperties teamsServiceProperties;
    private final SimulationServiceProperties simulationServiceProperties;
    private final OddsServiceProperties oddsServiceProperties;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient teamsWebClient(ServiceJwtForwardingFilter serviceJwtForwardingFilter) {
        return WebClient.builder()
                .baseUrl(teamsServiceProperties.url())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(teamsServiceProperties.timeout().read())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) teamsServiceProperties.timeout().connect().toMillis())
                ))
                .filter(serviceJwtForwardingFilter)
                .build();
    }

    @Bean
    public WebClient oddsWebClient(ServiceJwtForwardingFilter serviceJwtForwardingFilter) {
        return WebClient.builder()
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

    @Bean
    public WebClient simulationWebClient(ServiceJwtForwardingFilter serviceJwtForwardingFilter) {
        return WebClient.builder()
                .baseUrl(simulationServiceProperties.url())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(simulationServiceProperties.timeout().read())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) simulationServiceProperties.timeout().connect().toMillis())
                ))
                .filter(serviceJwtForwardingFilter)
                .build();
    }
}
