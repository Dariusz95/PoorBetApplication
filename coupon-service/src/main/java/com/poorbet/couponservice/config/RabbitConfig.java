package com.poorbet.couponservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

rab    public static final String MATCH_EVENTS_EXCHANGE = "match.events";
    public static final String MATCH_FINISHED_QUEUE =
            "match.finished.coupon.queue";

    @Bean
    public Queue matchFinishedQueue() {
        return new Queue(MATCH_FINISHED_QUEUE, true);
    }


    @Bean
    public FanoutExchange matchEventsExchange() {
        return new FanoutExchange(MATCH_EVENTS_EXCHANGE, true, false);
    }


    @Bean
    public Binding bindCouponQueueToExchange(
            Queue matchFinishedQueue,
            FanoutExchange matchEventsExchange) {

        return BindingBuilder
                .bind(matchFinishedQueue)
                .to(matchEventsExchange);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
