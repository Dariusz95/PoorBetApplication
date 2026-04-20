package com.poorbet.couponservice.config;

import com.poorbet.commons.rabbit.EventDefinition;
import com.poorbet.commons.rabbit.EventKey;
import com.poorbet.commons.rabbit.EventRegistry;
import com.poorbet.commons.rabbit.MessagingProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class RabbitConfig {

    @Bean
    public Declarables declarables(MessagingProperties properties,
                                   EventRegistry eventRegistry) {

        Map<String, TopicExchange> exchangeCache = new HashMap<>();
        List<Declarable> declarables = new ArrayList<>();

        Map<EventKey, MessagingProperties.ConsumerConfig> list = properties.getConsumers();

        if (list == null || list.isEmpty()) {
            log.info("No RabbitMQ consumers configured for this service");
            return new Declarables(declarables);
        }

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
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
