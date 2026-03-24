package io.github.tawdi.jobboard.notifications.listener;

import io.github.tawdi.jobboard.notifications.dto.SendNotificationRequest;
import io.github.tawdi.jobboard.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-events", groupId = "notifications-group")
    public void consumeNotificationEvent(SendNotificationRequest request) {
        log.info("Received Kafka event for user {}: {}", request.getRecipientUserId(), request.getTitle());
        try {
            notificationService.sendNotification(request);
            log.info("Successfully processed Kafka event for user {}", request.getRecipientUserId());
        } catch (Exception e) {
            log.error("Error processing Kafka notification event for user {}", request.getRecipientUserId(), e);
            // Fallback: If it's a transient DB error, spring-kafka can be configured to retry.
            // For now, we just log it.
        }
    }
}
