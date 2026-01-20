package com.poorbet.couponservice.config;

import io.netty.channel.ChannelOption;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;


@Configuration
@AllArgsConstructor
public class WebClientConfig {

    private final MatchProperties matchProperties;


    @Bean
    public WebClient matchServiceWebClientBuilder() {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(matchProperties.timeout().read())
                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        (int) matchProperties.timeout().connect().toMillis()
                );

        return WebClient.builder()
                .baseUrl(matchProperties.url())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
