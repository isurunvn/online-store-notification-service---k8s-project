package com.onlinestore.notificationservice;
import com.onlinestore.notificationservice.dto.OrderCreatedEvent;
import com.onlinestore.notificationservice.listener.NotificationListener;
import com.onlinestore.notificationservice.service.NotificationService;
import com.onlinestore.notificationservice.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class NotificationTest {
    @Test void listenerPropagatesFailureForRetryInsteadOfAcknowledgingIt() {
        var service = mock(NotificationService.class);
        var event = OrderCreatedEvent.builder().orderId(1L).build();
        doThrow(new IllegalStateException("SMTP unavailable")).when(service).sendOrderConfirmationEmail(event);
        assertThrows(IllegalStateException.class, () -> new NotificationListener(service).handleOrderCreatedEvent(event));
    }
    @Test void demoModeDoesNotSendMail() {
        var sender = mock(JavaMailSender.class);
        new NotificationServiceImpl(sender).sendOrderConfirmationEmail(OrderCreatedEvent.builder().orderId(1L).build());
        verifyNoInteractions(sender);
    }
    @Test void enabledEmailUsesEventRecipient() {
        var sender = mock(JavaMailSender.class);
        var service = new NotificationServiceImpl(sender);
        ReflectionTestUtils.setField(service, "emailEnabled", true);
        ReflectionTestUtils.setField(service, "from", "store@example.com");
        service.sendOrderConfirmationEmail(OrderCreatedEvent.builder().orderId(1L).email("customer@example.com").build());
        verify(sender).send(argThat((SimpleMailMessage m) -> m.getTo()[0].equals("customer@example.com")));
    }
    @Test void enabledEmailRejectsMissingRecipient() {
        var service = new NotificationServiceImpl(mock(JavaMailSender.class));
        ReflectionTestUtils.setField(service, "emailEnabled", true);
        assertThrows(IllegalArgumentException.class, () -> service.sendOrderConfirmationEmail(OrderCreatedEvent.builder().orderId(1L).build()));
    }
    @Test void convertsBackendEventDespiteDifferentJavaPackage() {
        var properties = new org.springframework.amqp.core.MessageProperties();
        properties.setContentType("application/json");
        properties.setHeader("__TypeId__", "com.onlinestore.orderservice.dto.OrderCreatedEvent");
        properties.setInferredArgumentType(OrderCreatedEvent.class);
        var body = "{\"orderId\":5,\"userId\":\"customer\",\"items\":[{\"id\":9,\"productId\":2,\"quantity\":1,\"unitPrice\":10}],\"timestamp\":\"2026-10-30T12:00:00\"}";
        var message = new org.springframework.amqp.core.Message(body.getBytes(java.nio.charset.StandardCharsets.UTF_8), properties);
        var converter = new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter();
        var event = (OrderCreatedEvent) converter.fromMessage(message);
        assertEquals(5L, event.getOrderId());
        assertEquals(2L, event.getItems().get(0).getProductId());
    }
}
