package com.poorbet.matchservice.match.config;

import com.poorbet.commons.auth.webclient.JwtForwardingFilter;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final TeamServiceProperties teamServiceProperties;
    private final SimulationServiceProperties simulationServiceProperties;
    private final OddsServiceProperties oddsServiceProperties;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient teamsWebClient(JwtForwardingFilter jwtForwardingFilter) {
        return WebClient.builder()
                .baseUrl(teamServiceProperties.url())
                .filter(jwtForwardingFilter)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(teamServiceProperties.timeout().read())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) teamServiceProperties.timeout().connect().toMillis())
                ))
                .build();
    }

    @Bean
    public WebClient oddsWebClient() {
        return WebClient.builder()
                .baseUrl(oddsServiceProperties.url())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(oddsServiceProperties.timeout().read())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) oddsServiceProperties.timeout().connect().toMillis())
                ))
                .build();
    }


    @Bean
    public WebClient simulationWebClient() {
        return WebClient.builder()
                .baseUrl(simulationServiceProperties.url())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(simulationServiceProperties.timeout().read())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) simulationServiceProperties.timeout().connect().toMillis())
                ))
                .build();
    }
}
