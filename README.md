# Notification service

Consumes JSON OrderCreatedEvent messages from durable order.created.queue, bound to order.exchange
with routing key order.created. The DTO accepts backend events even though Java package names differ.
The event includes orderId, userId, email, totalAmount, items and timestamp.

Default deployment is explicit log-only demo mode (NOTIFICATION_EMAIL_ENABLED=false). It logs the
order ID and total without pretending an email was sent. SMTP mode uses JavaMailSender, event email,
and NOTIFICATION_EMAIL_FROM. SMTP_HOST, SMTP_PORT, SMTP_USERNAME and SMTP_PASSWORD configure the
provider. Authenticated STARTTLS is required. Do not commit SMTP credentials.

The listener uses Spring Boot's configured factory: automatic acknowledgment after success,
prefetch 1, and 3 processing attempts. Errors propagate instead of being swallowed. The recovery
handler republishes exhausted failures with publisher confirms to durable notification.failed.queue
via notification.failed.exchange. Existing order queue arguments are unchanged. Inspect failed
messages through a private RabbitMQ management port-forward and replay only after fixing the cause.
Messages may be delivered more than once; this is not an exactly-once email service.

Runtime: Java 21, HTTP 8084, actuator liveness/readiness. No database or PVC. Required broker settings:
RABBITMQ_HOST, RABBITMQ_PORT, RABBITMQ_USERNAME, RABBITMQ_PASSWORD. Kubernetes reuses rabbitmq-client
and ocir-pull-secret in online-store. Health/API endpoints are internal, with no public HTTPRoute.

```bash
mvn verify
```

GitHub prod environment variables: OCIR_REGISTRY, OCIR_NAMESPACE, OCIR_REPOSITORY
(online-store/notification-service). Secrets: OCIR_USERNAME and OCIR_AUTH_TOKEN. Manual image
publishing builds linux/arm64 and runs tests. Promote its digest using the GitOps script/workflow.
See platform-gitops/FINISH-PROJECT.md for the coordinated rollout and full verification journey.
