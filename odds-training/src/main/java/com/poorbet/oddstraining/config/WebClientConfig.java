package com.poorbet.oddstraining.config;

import com.poorbet.commons.auth.webclient.ServiceJwtForwardingFilter;
import com.poorbet.oddstraining.properties.SimulationServiceProperties;
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

    private final SimulationServiceProperties simulationServiceProperties;


    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient simulationWebClient(ServiceJwtForwardingFilter serviceJwtForwardingFilter) {
        return WebClient.builder()
                .baseUrl(simulationServiceProperties.url())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(simulationServiceProperties.timeout().read())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) simulationServiceProperties.timeout().connect().toMillis())
                ))
                .filter(serviceJwtForwardingFilter)
                .build();
    }
}
