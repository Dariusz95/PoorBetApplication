package com.poorbet.oddstraining.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Component
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient simulationWebClient(WebClient.Builder builder, SimulationServiceProperties properties) {
        return builder.clone()
                .baseUrl(properties.url())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(properties.timeout().read())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) properties.timeout().connect().toMillis())
                ))
                .build();
    }
}
