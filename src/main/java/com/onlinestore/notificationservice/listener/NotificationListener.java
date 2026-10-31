package com.onlinestore.notificationservice.listener;

import com.onlinestore.notificationservice.dto.OrderCreatedEvent;
import com.onlinestore.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.order-created}")
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received order event: orderId={}", event.getOrderId());
        // Let failures reach the listener retry/recovery interceptor.
        notificationService.sendOrderConfirmationEmail(event);
    }
}
