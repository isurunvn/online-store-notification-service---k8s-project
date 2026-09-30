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
        try {
            log.info("Received OrderCreatedEvent: Order ID: {}, User ID: {}, Total Amount: {}, Item Count: {}",
                    event.getOrderId(),
                    event.getUserId(),
                    event.getTotalAmount(),
                    event.getItems() != null ? event.getItems().size() : 0);

            notificationService.sendOrderConfirmationEmail(event);
        } catch (Exception e) {
            log.error("Failed to process OrderCreatedEvent for Order ID: {}", event.getOrderId(), e);
        }
    }
}
