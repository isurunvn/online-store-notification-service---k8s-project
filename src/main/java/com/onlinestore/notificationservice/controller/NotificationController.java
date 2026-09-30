package com.onlinestore.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification API", description = "Endpoints for Notification Service operations")
public class NotificationController {

    @GetMapping("/health")
    @Operation(summary = "Check health", description = "Returns the simple health status of the notification service")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running!");
    }
}
