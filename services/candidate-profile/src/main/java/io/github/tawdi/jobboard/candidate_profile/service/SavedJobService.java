package io.github.tawdi.jobboard.candidate_profile.service;

import io.github.tawdi.jobboard.candidate_profile.dto.SavedJobDTO;
import io.github.tawdi.jobboard.candidate_profile.entity.SavedJob;
import io.github.tawdi.jobboard.candidate_profile.repository.SavedJobRepository;
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
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;

    @Transactional(readOnly = true)
    public List<SavedJobDTO> getSavedJobs(String userId) {
        return savedJobRepository.findByUserIdOrderBySavedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SavedJobDTO saveJob(String userId, Long offerId) {
        if (savedJobRepository.existsByUserIdAndOfferId(userId, offerId)) {
            log.info("Job {} already saved for user {}", offerId, userId);
            return savedJobRepository.findByUserIdAndOfferId(userId, offerId)
                    .map(this::toDto)
                    .orElseGet(() -> toDto(savedJobRepository.save(
                            SavedJob.builder().userId(userId).offerId(offerId).build())));
        }

        SavedJob saved = savedJobRepository.save(
                SavedJob.builder().userId(userId).offerId(offerId).build());
        log.info("Saved job {} for user {}", offerId, userId);
        return toDto(saved);
    }

    public void removeJob(String userId, Long offerId) {
        savedJobRepository.findByUserIdAndOfferId(userId, offerId)
                .ifPresent(job -> {
                    savedJobRepository.delete(job);
                    log.info("Removed saved job {} for user {}", offerId, userId);
                });
    }

    private SavedJobDTO toDto(SavedJob entity) {
        return SavedJobDTO.builder()
                .id(entity.getId())
                .offerId(entity.getOfferId())
                .savedAt(entity.getSavedAt())
                .build();
    }
}

