package com.poorbet.matchservice.match.stream.config.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitQueuesConfig {

    @Bean
    public Queue notificationPoolQueue() {
        return new Queue("notification.pool.events", true);
    }
    }