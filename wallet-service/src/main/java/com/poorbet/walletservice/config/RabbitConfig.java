package com.poorbet.walletservice.config;

import com.poorbet.commons.rabbit.EventDefinition;
import com.poorbet.commons.rabbit.EventKey;
import com.poorbet.commons.rabbit.EventRegistry;
import com.poorbet.commons.rabbit.MessagingProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class RabbitConfig {

    static final String DLX = "poorbet.dlx";

    @Bean
    public Declarables declarables(MessagingProperties properties,
                                   EventRegistry eventRegistry) {

        Map<String, TopicExchange> exchangeCache = new HashMap<>();
        List<Declarable> declarables = new ArrayList<>();

        DirectExchange dlx = new DirectExchange(DLX, true, false);
        declarables.add(dlx);

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

            String queueName = consumer.getQueue();
            String dlqName = queueName + ".dlq";

            Queue queue = QueueBuilder.durable(queueName)
                    .deadLetterExchange(DLX)
                    .deadLetterRoutingKey(dlqName)
                    .build();

            Queue dlq = QueueBuilder.durable(dlqName).build();

            Binding binding = BindingBuilder.bind(queue).to(exchange).with(event.routingKey());
            Binding dlqBinding = BindingBuilder.bind(dlq).to(dlx).with(dlqName);

            declarables.add(exchange);
            declarables.add(queue);
            declarables.add(binding);
            declarables.add(dlq);
            declarables.add(dlqBinding);
        });

        return new Declarables(declarables);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter messageConverter,
            RabbitTemplate rabbitTemplate) {

        RetryOperationsInterceptor retryInterceptor = RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .recoverer(new RepublishMessageRecoverer(rabbitTemplate, DLX))
                .build();

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(retryInterceptor);
        factory.setErrorHandler(new ConditionalRejectingErrorHandler(
                new ConditionalRejectingErrorHandler.DefaultExceptionStrategy()));
        return factory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
