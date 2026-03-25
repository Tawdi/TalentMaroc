package io.github.tawdi.jobboard.applications.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateApplicationMessageRequest {

    @NotNull
    private Long applicationId;

    @NotBlank
    private String senderUserId;

    @NotBlank
    private String content;
}

