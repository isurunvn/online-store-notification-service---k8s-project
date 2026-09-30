package com.onlinestore.notificationservice.service;

import com.onlinestore.notificationservice.dto.OrderCreatedEvent;

public interface NotificationService {
    void sendOrderConfirmationEmail(OrderCreatedEvent event);
}
