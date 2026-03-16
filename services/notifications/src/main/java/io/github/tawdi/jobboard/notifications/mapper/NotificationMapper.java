package io.github.tawdi.jobboard.notifications.mapper;

import io.github.tawdi.jobboard.notifications.dto.NotificationResponse;
import io.github.tawdi.jobboard.notifications.dto.SendNotificationRequest;
import io.github.tawdi.jobboard.notifications.entity.Notification;
import io.github.tawdi.jobboard.notifications.entity.NotificationStatus;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toEntity(SendNotificationRequest request) {
        return Notification.builder()
                .recipientUserId(request.getRecipientUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .status(NotificationStatus.UNREAD)
                .build();
    }

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .recipientUserId(notification.getRecipientUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
