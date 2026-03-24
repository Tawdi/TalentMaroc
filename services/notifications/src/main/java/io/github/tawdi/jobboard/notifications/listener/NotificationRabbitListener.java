package io.github.tawdi.jobboard.notifications.listener;

import io.github.tawdi.jobboard.notifications.config.RabbitMQConfig;
import io.github.tawdi.jobboard.notifications.dto.SendNotificationRequest;
import io.github.tawdi.jobboard.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRabbitListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void consumeNotificationEvent(SendNotificationRequest request) {
        log.info("Received RabbitMQ event for user {}: {}", request.getRecipientUserId(), request.getTitle());
        try {
            notificationService.sendNotification(request);
            log.info("Successfully processed RabbitMQ event for user {}", request.getRecipientUserId());
        } catch (Exception e) {
            log.error("Error processing RabbitMQ notification event for user {}", request.getRecipientUserId(), e);

        }
    }
}
