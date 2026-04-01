package com.poorbet.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class RabbitConfig {

    @Bean
    public TopicExchange walletExchange(MessagingProperties properties) {
        return new TopicExchange(properties.getWalletExchange(), true, false);
    }

    @Bean
    public Queue walletQueue(MessagingProperties properties) {
        return new Queue(properties.getWalletQueue(), true);
    }

    @Bean
    public Binding walletBinding(Queue walletQueue, TopicExchange walletExchange, MessagingProperties properties) {
        return BindingBuilder.bind(walletQueue).to(walletExchange).with(properties.getWalletRoutingKey());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
