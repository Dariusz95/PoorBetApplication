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
    WebClient teamsWebClient(
            WebClient.Builder builder,
            TeamServiceProperties properties
    ) {
        HttpClient httpClient = HttpClient.create()
                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        (int) properties.timeout().connect().toMillis()
                ).responseTimeout(properties.timeout().read());

        return builder
                .baseUrl(properties.url())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}