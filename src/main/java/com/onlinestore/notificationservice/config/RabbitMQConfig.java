package com.onlinestore.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.queue.order-created}")
    private String orderCreatedQueueName;

    @Value("${rabbitmq.routing-key.order-created}")
    private String orderCreatedRoutingKey;

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(orderCreatedQueueName);
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with(orderCreatedRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public org.springframework.amqp.core.Declarables failedNotifications() {
        DirectExchange exchange = new DirectExchange("notification.failed.exchange", true, false);
        Queue queue = new Queue("notification.failed.queue", true);
        return new org.springframework.amqp.core.Declarables(exchange, queue,
                BindingBuilder.bind(queue).to(exchange).with("notification.failed"));
    }

    @Bean
    public org.springframework.amqp.rabbit.retry.MessageRecoverer notificationRecoverer(org.springframework.amqp.rabbit.core.RabbitTemplate template) {
        return new org.springframework.amqp.rabbit.retry.RepublishMessageRecovererWithConfirms(
                template, "notification.failed.exchange", "notification.failed",
                org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType.CORRELATED);
    }
}
