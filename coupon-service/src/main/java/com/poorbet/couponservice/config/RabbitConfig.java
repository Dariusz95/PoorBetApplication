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

    public static final String QUEUE_COUPON_POOL_FINISHED =
            "coupon.pool.finished";

    @Bean
    public Queue couponPoolFinishedQueue() {
        return new Queue(QUEUE_COUPON_POOL_FINISHED, true);
    }

    @Bean
    public FanoutExchange matchPoolExchange() {
        return new FanoutExchange("match-pool.events", true, false);
    }

    @Bean
    public Binding bindCouponQueueToPoolExchange(
            FanoutExchange matchPoolExchange,
            Queue couponPoolFinishedQueue) {

        return BindingBuilder
                .bind(couponPoolFinishedQueue)
                .to(matchPoolExchange);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
