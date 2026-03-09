package io.github.tawdi.jobboard.candidate_profile.controller;

import io.github.tawdi.jobboard.candidate_profile.dto.ApiResponseDTO;
import io.github.tawdi.jobboard.candidate_profile.dto.FormationDTO;
import io.github.tawdi.jobboard.candidate_profile.service.FormationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles/user/{userId}/formations")
@RequiredArgsConstructor
public class FormationController {

    private final FormationService formationService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<FormationDTO>>> getAll(@PathVariable String userId) {
        List<FormationDTO> list = formationService.getAllByUserId(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Formations retrieved", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<FormationDTO>> create(
            @PathVariable String userId,
            @Valid @RequestBody FormationDTO dto) {
        FormationDTO created = formationService.create(userId, dto);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Formation created", created), HttpStatus.CREATED);
    }

    @PutMapping("/{formationId}")
    public ResponseEntity<ApiResponseDTO<FormationDTO>> update(
            @PathVariable String userId,
            @PathVariable Long formationId,
            @Valid @RequestBody FormationDTO dto) {
        FormationDTO updated = formationService.update(userId, formationId, dto);
        return ResponseEntity.ok(ApiResponseDTO.success("Formation updated", updated));
    }

    @DeleteMapping("/{formationId}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(
            @PathVariable String userId,
            @PathVariable Long formationId) {
        formationService.delete(userId, formationId);
        return ResponseEntity.ok(ApiResponseDTO.success("Formation deleted", null));
    }
}

