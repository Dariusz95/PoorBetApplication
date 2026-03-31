package com.poorbet.couponservice.config;

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

//    @Bean
//    public TopicExchange matchFinishedExchange(MessagingProperties messagingProperties) {
//        return new TopicExchange(messagingProperties.getMatchFinished().getExchange(), true, false);
//    }
//
//    @Bean
//    public Queue matchFinishedQueue(MessagingProperties messagingProperties) {
//        return new Queue(messagingProperties.getMatchFinished().getQueue(), true);
//    }
//
//    @Bean
//    public Binding matchFinishedBinding(
//            Queue matchFinishedQueue,
//            TopicExchange matchFinishedExchange) {
//        return BindingBuilder
//                .bind(matchFinishedQueue)
//                .to(matchFinishedExchange)
//                .with(MatchEvents.MATCH_FINISHED.routingKey());
//    }

    @Bean
    public Declarables declarables(MessagingProperties properties) {

        List<Declarable> declarables = new ArrayList<>();

        properties.getConsumers().forEach((name, consumer) -> {

            TopicExchange exchange = new TopicExchange(
                    properties.getExchanges().get(consumer.getExchange()),
                    true,
                    false
            );

            Queue queue = new Queue(consumer.getQueue(), true);

            Binding binding = BindingBuilder
                    .bind(queue)
                    .to(exchange)
                    .with(consumer.getRoutingKey());

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
