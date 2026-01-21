package com.poorbet.matchservice.match.stream.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue notificationFinishedMatchesQueue() {
        return new Queue("notification.finished.match.events", true);
    }

    @Bean
    public FanoutExchange finishedMatchesExchange() {
        return new FanoutExchange("match.events", true, false);
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter converter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public Binding bindPoolToNotificationBinding(
            FanoutExchange finishedMatchesExchange,
            Queue notificationFinishedMatchesQueue) {
        return BindingBuilder.bind(notificationFinishedMatchesQueue)
                .to(finishedMatchesExchange);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
