package io.github.tawdi.jobboard.auth_user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationEvent {
    private String recipientUserId;
    private String title;
    private String message;
    private String type; // INFO, SUCCESS, WARNING, ERROR
    private boolean sendEmail;
    private String recipientEmail;
}
