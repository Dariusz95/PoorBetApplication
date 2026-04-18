package com.poorbet.notificationservice.config;

import com.poorbet.commons.rabbit.EventDefinition;
import com.poorbet.commons.rabbit.EventRegistry;
import com.poorbet.commons.rabbit.MessagingProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class RabbitConfig {

    @Bean
    public Declarables declarables(MessagingProperties properties,
                                   EventRegistry eventRegistry) {

        List<Declarable> declarables = new ArrayList<>();

        properties.getConsumers().forEach((key, consumer) -> {

            EventDefinition<?> event = eventRegistry.get(key);

            if (event == null) {
                throw new IllegalStateException("Unknown event key: " + key);
            }

            TopicExchange exchange = new TopicExchange(event.exchange(), true, false);
            Queue queue = new Queue(consumer.getQueue(), true);

            Binding binding = BindingBuilder
                    .bind(queue)
                    .to(exchange)
                    .with(event.routingKey());

            declarables.add(exchange);
            declarables.add(queue);
            declarables.add(binding);
        });

        return new Declarables(declarables);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
