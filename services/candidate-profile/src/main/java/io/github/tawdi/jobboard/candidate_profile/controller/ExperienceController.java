package io.github.tawdi.jobboard.candidate_profile.controller;

import io.github.tawdi.jobboard.candidate_profile.dto.ApiResponseDTO;
import io.github.tawdi.jobboard.candidate_profile.dto.ExperienceDTO;
import io.github.tawdi.jobboard.candidate_profile.service.ExperienceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles/user/{userId}/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ExperienceDTO>>> getAll(@PathVariable String userId) {
        List<ExperienceDTO> list = experienceService.getAllByUserId(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Experiences retrieved", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ExperienceDTO>> create(
            @PathVariable String userId,
            @Valid @RequestBody ExperienceDTO dto) {
        ExperienceDTO created = experienceService.create(userId, dto);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Experience created", created), HttpStatus.CREATED);
    }

    @PutMapping("/{experienceId}")
    public ResponseEntity<ApiResponseDTO<ExperienceDTO>> update(
            @PathVariable String userId,
            @PathVariable Long experienceId,
            @Valid @RequestBody ExperienceDTO dto) {
        ExperienceDTO updated = experienceService.update(userId, experienceId, dto);
        return ResponseEntity.ok(ApiResponseDTO.success("Experience updated", updated));
    }

    @DeleteMapping("/{experienceId}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(
            @PathVariable String userId,
            @PathVariable Long experienceId) {
        experienceService.delete(userId, experienceId);
        return ResponseEntity.ok(ApiResponseDTO.success("Experience deleted", null));
    }
}

