package com.poorbet.matchservice.match.stream.config.rabbitmq;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitExchangesConfig {

    @Bean
    public FanoutExchange matchPoolExchange() {
        return new FanoutExchange("match-pool.events", true, false);
    }
}
