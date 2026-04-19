package com.poorbet.walletservice.config;

import com.poorbet.commons.rabbit.EventDefinition;
import com.poorbet.commons.rabbit.MessagingProperties;
import com.poorbet.commons.rabbit.events.user.UserEvents;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class RabbitConfig {

    @Bean
    public Declarables declarables(MessagingProperties properties) {

        List<Declarable> declarables = new ArrayList<>();
        Set<String> declaredExchanges = new HashSet<>();

        Map<String, EventDefinition<?>> eventMap = Map.of(
                "user-created", UserEvents.USER_EVENTS
        );

        properties.getConsumers().forEach((name, consumer) -> {

            EventDefinition<?> event = eventMap.get(name);

            Queue queue = new Queue(consumer.getQueue(), true);

            if (declaredExchanges.add(event.exchange())) {
                declarables.add(new TopicExchange(event.exchange(), true, false));
            }

            Binding binding = BindingBuilder
                    .bind(queue)
                    .to(new TopicExchange(event.exchange()))
                    .with(event.routingKey());

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
