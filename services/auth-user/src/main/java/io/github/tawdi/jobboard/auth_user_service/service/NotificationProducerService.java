package io.github.tawdi.jobboard.auth_user_service.service;

import io.github.tawdi.jobboard.auth_user_service.dto.SendNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_NAME = "notification-events";

    public void sendWelcomeEmail(String userId, String email, String role) {
        String roleDisplay = role.replace("ROLE_", "");
        
        SendNotificationEvent event = SendNotificationEvent.builder()
                .recipientUserId(userId)
                .recipientEmail(email)
                .title("Welcome to Job Board Platform!")
                .message("Thank you for registering as a " + roleDisplay + ". Validate your profile to get started.")
                .type("INFO")
                .sendEmail(true)
                .build();

        log.info("Publishing Welcome Notification event to Kafka for user: {}", userId);
        kafkaTemplate.send(TOPIC_NAME, userId, event);
    }

    public void sendVerificationEmail(String userId, String email, String token) {
        SendNotificationEvent event = SendNotificationEvent.builder()
                .recipientUserId(userId)
                .recipientEmail(email)
                .title("Verify your email")
                .message("Your verification token is: " + token)
                .type("INFO")
                .sendEmail(true)
                .build();

        log.info("Publishing Verification Notification event to Kafka for user: {}", userId);
        kafkaTemplate.send(TOPIC_NAME, userId, event);
    }

    public void sendPasswordResetEmail(String userId, String email, String token) {
        SendNotificationEvent event = SendNotificationEvent.builder()
                .recipientUserId(userId)
                .recipientEmail(email)
                .title("Password Reset Request")
                .message("Your password reset token is: " + token)
                .type("WARNING")
                .sendEmail(true)
                .build();

        log.info("Publishing Password Reset Notification event to Kafka for user: {}", userId);
        kafkaTemplate.send(TOPIC_NAME, userId, event);
    }
}
