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

    public static final String QUEUE_COUPON_MATCHES_FINISHED =
            "coupon.matches.finished";

    @Bean
    public Queue matchesFinishedQueue() {
        return new Queue(QUEUE_COUPON_MATCHES_FINISHED, true);
    }

    @Bean
    public FanoutExchange matchPoolExchange() {
        return new FanoutExchange("match.events", true, false);
    }

    @Bean
    public Binding bindCouponQueueToPoolExchange(
            FanoutExchange matchPoolExchange,
            Queue matchesFinishedQueue) {

        return BindingBuilder
                .bind(matchesFinishedQueue)
                .to(matchPoolExchange);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
