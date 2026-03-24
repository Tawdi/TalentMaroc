package io.github.tawdi.jobboard.auth_user_service.service;

import io.github.tawdi.jobboard.auth_user_service.config.RabbitMQConfig;
import io.github.tawdi.jobboard.auth_user_service.dto.SendNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${job-board.frontend-base-url:http://localhost:4200}")
    private String frontendBaseUrl;

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

        log.info("Publishing Welcome Notification event to RabbitMQ for user: {}", userId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                event
        );
    }

    public void sendVerificationEmail(String userId, String email, String token) {
        String verifyUrl = String.format("%s/auth/verify-email?token=%s", frontendBaseUrl, token);

        String message = "Your verification token is: " + token + "\n\n" +
                "You can verify your email by clicking the link below:\n" +
                verifyUrl;

        SendNotificationEvent event = SendNotificationEvent.builder()
                .recipientUserId(userId)
                .recipientEmail(email)
                .title("Verify your email")
                .message(message)
                .type("INFO")
                .sendEmail(true)
                .build();

        log.info("Publishing Verification Notification event to RabbitMQ for user: {}", userId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                event
        );
    }

    public void sendPasswordResetEmail(String userId, String email, String token) {
        String resetUrl = String.format("%s/auth/reset-password?token=%s", frontendBaseUrl, token);

        String message = "Your password reset token is: " + token + "\n\n" +
                "You can reset your password by clicking the link below:\n" +
                resetUrl;

        SendNotificationEvent event = SendNotificationEvent.builder()
                .recipientUserId(userId)
                .recipientEmail(email)
                .title("Password Reset Request")
                .message(message)
                .type("WARNING")
                .sendEmail(true)
                .build();

        log.info("Publishing Password Reset Notification event to RabbitMQ for user: {}", userId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                event
        );
    }
}