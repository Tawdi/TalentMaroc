package io.github.tawdi.jobboard.candidate_profile.controller;

import io.github.tawdi.jobboard.candidate_profile.dto.ApiResponseDTO;
import io.github.tawdi.jobboard.candidate_profile.dto.SavedJobDTO;
import io.github.tawdi.jobboard.candidate_profile.service.SavedJobService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles/user/{userId}/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<SavedJobDTO>>> getSavedJobs(@PathVariable String userId) {
        List<SavedJobDTO> jobs = savedJobService.getSavedJobs(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Saved jobs retrieved successfully", jobs));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<SavedJobDTO>> saveJob(
            @PathVariable String userId,
            @RequestParam @NotNull Long offerId) {
        SavedJobDTO saved = savedJobService.saveJob(userId, offerId);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Job saved successfully", saved),
                HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponseDTO<Void>> removeJob(
            @PathVariable String userId,
            @RequestParam @NotNull Long offerId) {
        savedJobService.removeJob(userId, offerId);
        return ResponseEntity.ok(ApiResponseDTO.success("Saved job removed", null));
    }
}

