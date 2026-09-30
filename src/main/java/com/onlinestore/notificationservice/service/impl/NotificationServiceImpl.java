package com.onlinestore.notificationservice.service.impl;

import com.onlinestore.notificationservice.dto.OrderCreatedEvent;
import com.onlinestore.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendOrderConfirmationEmail(OrderCreatedEvent event) {
        log.info("Preparing to send email to user {}", event.getUserId());
        
        String itemsList = event.getItems() != null ? event.getItems().stream()
                .map(item -> String.format("Product ID: %d, Qty: %d, Unit Price: $%s", 
                        item.getProductId(), item.getQuantity(), item.getUnitPrice()))
                .collect(Collectors.joining("\n- ")) : "No items";

        String emailBody = String.format("""
                To: user %s
                Subject: Order Confirmation #%d
                
                Thank you for your order!
                
                Order Details:
                - %s
                
                Total Amount: $%s
                """,
                event.getUserId(),
                event.getOrderId(),
                itemsList,
                event.getTotalAmount()
        );

        log.info("Simulating email send:\n{}", emailBody);
        
        try {
            // Simulate delay for email API call
            Thread.sleep(100);
            log.info("Email sent successfully for Order #{}", event.getOrderId());
        } catch (InterruptedException e) {
            log.error("Email sending simulation interrupted for Order #{}", event.getOrderId(), e);
            Thread.currentThread().interrupt();
        }
    }
}
