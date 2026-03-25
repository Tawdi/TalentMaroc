package io.github.tawdi.jobboard.applications.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class ApplicationMessageDTO {
    Long id;
    Long applicationId;
    String senderUserId;
    String content;
    Instant sentAt;
    boolean read;
}

