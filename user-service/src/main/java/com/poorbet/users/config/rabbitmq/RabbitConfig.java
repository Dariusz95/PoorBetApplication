package com.poorbet.users.config.rabbitmq;

import com.poorbet.commons.rabbit.EventDefinition;
import com.poorbet.commons.rabbit.events.wallet.WalletEvents;
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
    public Declarables declarables(MessagingProperties properties) {

        List<Declarable> declarables = new ArrayList<>();
        Map<String, TopicExchange> exchanges = new HashMap<>();

        Map<String, EventDefinition<?>> eventMap = Map.of(
                "wallet-created", WalletEvents.WALLET_CREATED
        );

        properties.getConsumers().forEach((name, consumer) -> {

            EventDefinition<?> event = eventMap.get(name);

            if (event == null) {
                throw new IllegalStateException("No event definition for consumer: " + name);
            }

            TopicExchange exchange = exchanges.computeIfAbsent(
                    event.exchange(),
                    ex -> {
                        TopicExchange e = new TopicExchange(ex, true, false);
                        declarables.add(e);
                        return e;
                    }
            );

            Queue queue = new Queue(consumer.getQueue(), true);

            Binding binding = BindingBuilder
                    .bind(queue)
                    .to(exchange)
                    .with(event.routingKey());

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
