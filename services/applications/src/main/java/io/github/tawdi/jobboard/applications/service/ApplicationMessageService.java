package io.github.tawdi.jobboard.applications.service;

import io.github.tawdi.jobboard.applications.dto.ApplicationMessageDTO;
import io.github.tawdi.jobboard.applications.dto.CreateApplicationMessageRequest;
import io.github.tawdi.jobboard.applications.entity.Application;
import io.github.tawdi.jobboard.applications.entity.ApplicationMessage;
import io.github.tawdi.jobboard.applications.exceptions.ResourceNotFoundException;
import io.github.tawdi.jobboard.applications.repository.ApplicationMessageRepository;
import io.github.tawdi.jobboard.applications.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplicationMessageService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationMessageRepository messageRepository;

    @Transactional(readOnly = true)
    public List<ApplicationMessageDTO> getMessagesForApplication(Long applicationId) {
        return messageRepository.findByApplicationIdOrderBySentAtAsc(applicationId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ApplicationMessageDTO sendMessage(CreateApplicationMessageRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + request.getApplicationId()));

        ApplicationMessage message = ApplicationMessage.builder()
                .application(application)
                .senderUserId(request.getSenderUserId())
                .content(request.getContent())
                .read(false)
                .build();

        ApplicationMessage saved = messageRepository.save(message);
        log.info("New message on application {} from user {}", request.getApplicationId(), request.getSenderUserId());
        return toDto(saved);
    }

    private ApplicationMessageDTO toDto(ApplicationMessage entity) {
        return ApplicationMessageDTO.builder()
                .id(entity.getId())
                .applicationId(entity.getApplication().getId())
                .senderUserId(entity.getSenderUserId())
                .content(entity.getContent())
                .sentAt(entity.getSentAt())
                .read(entity.isRead())
                .build();
    }
}

