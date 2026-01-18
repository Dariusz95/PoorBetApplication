package com.poorbet.matchservice.match.stream.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitBindingsConfig {

    @Bean
    public Binding bindPoolToNotification(
            FanoutExchange matchPoolExchange,
            Queue notificationPoolQueue) {

        return BindingBuilder.bind(notificationPoolQueue)
                .to(matchPoolExchange);
    }
}