package com.poorbet.commons.auth.config;

import com.poorbet.commons.auth.token.ServiceTokenProvider;
import com.poorbet.commons.auth.webclient.JwtForwardingFilter;
import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(AuthServiceProperties.class)
public class AuthWebClientConfiguration {

    @Bean(name = "authWebClient")
    public WebClient authWebClient(AuthServiceProperties authServiceProperties) {
        return WebClient.builder()
                .baseUrl(authServiceProperties.url())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(authServiceProperties.timeout().read())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) authServiceProperties.timeout().connect().toMillis())
                ))
                .build();
    }

    @Bean
    public JwtForwardingFilter jwtForwardingFilter(ServiceTokenProvider serviceTokenProvider) {
        return new JwtForwardingFilter(serviceTokenProvider);
    }

    @Bean
    public ServiceTokenProvider serviceTokenProvider(@Qualifier("authWebClient") WebClient authWebClient,
                                                     AuthServiceProperties authServiceProperties) {
        return new ServiceTokenProvider(authWebClient, authServiceProperties);
    }
}
