package com.poorbet.users.config.rabbitmq;

import com.poorbet.commons.rabbit.EventDefinition;
import com.poorbet.commons.rabbit.EventRegistry;
import com.poorbet.commons.rabbit.MessagingProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class RabbitConfig {

    @Bean
    public Declarables declarables(MessagingProperties properties,
                                   EventRegistry eventRegistry) {

        Map<String, TopicExchange> exchangeCache = new HashMap<>();
        List<Declarable> declarables = new ArrayList<>();

        properties.getConsumers().forEach((eventKey, consumer) -> {

            EventDefinition<?> event = eventRegistry.get(eventKey);

            if (event == null) {
                throw new IllegalStateException("Unknown event key: " + eventKey);
            }

            TopicExchange exchange = exchangeCache.computeIfAbsent(
                    event.exchange(),
                    name -> new TopicExchange(name, true, false)
            );

            Queue queue = QueueBuilder.durable(consumer.getQueue())
                    .build();

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
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
