package io.github.tawdi.jobboard.notifications.repository;

import io.github.tawdi.jobboard.notifications.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Get paginated notifications for a specific user, sorted by creation date descending.
     */
    Page<Notification> findByRecipientUserIdOrderByCreatedAtDesc(String recipientUserId, Pageable pageable);

    /**
     * Count unread notifications for a user.
     */
    long countByRecipientUserIdAndStatus(String recipientUserId, io.github.tawdi.jobboard.notifications.entity.NotificationStatus status);

    /**
     * Mark all unread notifications as read for a specific user.
     */
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ' WHERE n.recipientUserId = :recipientUserId AND n.status = 'UNREAD'")
    int markAllAsReadForUser(String recipientUserId);
}
