package com.onlinestore.notificationservice.service.impl;

import com.onlinestore.notificationservice.dto.OrderCreatedEvent;
import com.onlinestore.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender mailSender;
    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;
    @Value("${notification.email.from}")
    private String from;

    @Override
    public void sendOrderConfirmationEmail(OrderCreatedEvent event) {
        if (!emailEnabled) {
            log.info("DEMO notification processed: orderId={}, total={}. Email delivery is disabled.",
                    event.getOrderId(), event.getTotalAmount());
            return;
        }
        if (event.getEmail() == null || event.getEmail().isBlank() || event.getEmail().endsWith("@users.invalid") || event.getEmail().equals("unknown@example.com")) {
            throw new IllegalArgumentException("Order event has no deliverable email address");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.getEmail());
        message.setSubject("Order confirmation #" + event.getOrderId());
        message.setText("Thank you for your order #" + event.getOrderId() + ". Total: " + event.getTotalAmount());
        mailSender.send(message);
        log.info("Order confirmation sent: orderId={}", event.getOrderId());
    }
}
