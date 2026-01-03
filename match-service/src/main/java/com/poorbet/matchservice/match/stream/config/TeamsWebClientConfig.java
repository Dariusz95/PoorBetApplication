package com.poorbet.matchservice.match.stream.config;

import io.netty.channel.ChannelOption;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;


@Configuration
@EnableConfigurationProperties(TeamServiceProperties.class)
public class TeamsWebClientConfig {


    @Bean
    public WebClient teamsWebClient(TeamServiceProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.url())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(properties.timeout().read())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) properties.timeout().connect().toMillis())
                ))
                .build();
    }
}