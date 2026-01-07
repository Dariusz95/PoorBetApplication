package com.poorbet.oddstraining.config;

import io.netty.channel.ChannelOption;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties({SimulationServiceProperties.class})
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
