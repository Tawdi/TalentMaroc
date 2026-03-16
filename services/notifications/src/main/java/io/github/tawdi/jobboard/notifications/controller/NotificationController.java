package io.github.tawdi.jobboard.notifications.controller;

import io.github.tawdi.jobboard.notifications.dto.ApiResponseWrapper;
import io.github.tawdi.jobboard.notifications.dto.NotificationResponse;
import io.github.tawdi.jobboard.notifications.dto.SendNotificationRequest;
import io.github.tawdi.jobboard.notifications.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Internal API for other microservices (via Feign) to trigger a new notification.
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponseWrapper<NotificationResponse>> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {

        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.ok(ApiResponseWrapper.success("Notification sent successfully", response));
    }

    /**
     * Get paginated notifications for a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseWrapper<Page<NotificationResponse>>> getUserNotifications(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(ApiResponseWrapper.success("Notifications fetched successfully", notifications));
    }

    /**
     * Get the count of unread notifications for a user.
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<ApiResponseWrapper<Long>> getUnreadCount(@PathVariable String userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponseWrapper.success("Unread count fetched", count));
    }

    /**
     * Mark a specific notification as read.
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponseWrapper<NotificationResponse>> markAsRead(@PathVariable Long id) {
        NotificationResponse response = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponseWrapper.success("Notification marked as read", response));
    }

    /**
     * Mark all notifications as read for a specific user.
     */
    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponseWrapper<Void>> markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponseWrapper.success("All notifications marked as read", null));
    }
}
