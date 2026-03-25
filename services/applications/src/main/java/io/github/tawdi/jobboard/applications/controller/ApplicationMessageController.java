package io.github.tawdi.jobboard.applications.controller;

import io.github.tawdi.jobboard.applications.dto.ApiResponseDTO;
import io.github.tawdi.jobboard.applications.dto.ApplicationMessageDTO;
import io.github.tawdi.jobboard.applications.dto.CreateApplicationMessageRequest;
import io.github.tawdi.jobboard.applications.service.ApplicationMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications/{applicationId}/messages")
@RequiredArgsConstructor
public class ApplicationMessageController {

    private final ApplicationMessageService messageService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ApplicationMessageDTO>>> getMessages(
            @PathVariable Long applicationId) {
        List<ApplicationMessageDTO> messages = messageService.getMessagesForApplication(applicationId);
        return ResponseEntity.ok(ApiResponseDTO.success("Messages retrieved successfully", messages));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ApplicationMessageDTO>> sendMessage(
            @PathVariable Long applicationId,
            @Valid @RequestBody CreateApplicationMessageRequest request) {

        // Ensure path and body are consistent
        request.setApplicationId(applicationId);

        ApplicationMessageDTO message = messageService.sendMessage(request);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Message sent successfully", message),
                HttpStatus.CREATED);
    }
}

