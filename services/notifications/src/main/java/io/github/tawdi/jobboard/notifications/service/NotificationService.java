package io.github.tawdi.jobboard.notifications.service;

import io.github.tawdi.jobboard.notifications.dto.NotificationResponse;
import io.github.tawdi.jobboard.notifications.dto.SendNotificationRequest;
import io.github.tawdi.jobboard.notifications.entity.Notification;
import io.github.tawdi.jobboard.notifications.entity.NotificationStatus;
import io.github.tawdi.jobboard.notifications.exceptions.ResourceNotFoundException;
import io.github.tawdi.jobboard.notifications.mapper.NotificationMapper;
import io.github.tawdi.jobboard.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;
    private final EmailService emailService;

    /**
     * Send a notification to a user.
     * Persists it to the database for in-app viewing, and optionally sends an email.
     */
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        // 1. Save to database for in-app notifications
        Notification notification = mapper.toEntity(request);
        Notification saved = notificationRepository.save(notification);
        log.info("In-App Notification saved for user {}: {}", request.getRecipientUserId(), request.getTitle());

        // 2. Conditionally send email
        if (request.isSendEmail() && request.getRecipientEmail() != null && !request.getRecipientEmail().isBlank()) {
            // Build a simple HTML template
            String htmlBody = String.format("""
                    <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                        <h2>%s</h2>
                        <p>%s</p>
                        <br/>
                        <p style="font-size: 12px; color: #888;">This is an automated message from the Job Board Platform.</p>
                    </div>
                    """, request.getTitle(), request.getMessage());

            // Fire and forget email (exceptions are caught in EmailService)
            emailService.sendEmail(request.getRecipientEmail(), request.getTitle(), htmlBody);
        }

        return mapper.toResponse(saved);
    }

    /**
     * Get paginated notifications for a user.
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(String userId, Pageable pageable) {
        return notificationRepository
                .findByRecipientUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(mapper::toResponse);
    }

    /**
     * Get count of unread notifications for a user.
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return notificationRepository.countByRecipientUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    /**
     * Mark a specific notification as read.
     */
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found for id: " + notificationId));

        notification.setStatus(NotificationStatus.READ);
        return mapper.toResponse(notificationRepository.save(notification));
    }

    /**
     * Mark all unread notifications as read for a user.
     */
    public void markAllAsRead(String userId) {
        int updated = notificationRepository.markAllAsReadForUser(userId);
        log.info("Marked {} notifications as READ for user {}", updated, userId);
    }
}
