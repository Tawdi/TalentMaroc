package io.github.tawdi.jobboard.candidate_profile.controller;

import io.github.tawdi.jobboard.candidate_profile.dto.ApiResponseDTO;
import io.github.tawdi.jobboard.candidate_profile.dto.SpokenLanguageDTO;
import io.github.tawdi.jobboard.candidate_profile.service.SpokenLanguageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles/user/{userId}/languages")
@RequiredArgsConstructor
public class SpokenLanguageController {

    private final SpokenLanguageService spokenLanguageService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<SpokenLanguageDTO>>> getAll(@PathVariable String userId) {
        List<SpokenLanguageDTO> list = spokenLanguageService.getAllByUserId(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Spoken languages retrieved", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<SpokenLanguageDTO>> create(
            @PathVariable String userId,
            @Valid @RequestBody SpokenLanguageDTO dto) {
        SpokenLanguageDTO created = spokenLanguageService.create(userId, dto);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Spoken language created", created), HttpStatus.CREATED);
    }

    @PutMapping("/{languageId}")
    public ResponseEntity<ApiResponseDTO<SpokenLanguageDTO>> update(
            @PathVariable String userId,
            @PathVariable Long languageId,
            @Valid @RequestBody SpokenLanguageDTO dto) {
        SpokenLanguageDTO updated = spokenLanguageService.update(userId, languageId, dto);
        return ResponseEntity.ok(ApiResponseDTO.success("Spoken language updated", updated));
    }

    @DeleteMapping("/{languageId}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(
            @PathVariable String userId,
            @PathVariable Long languageId) {
        spokenLanguageService.delete(userId, languageId);
        return ResponseEntity.ok(ApiResponseDTO.success("Spoken language deleted", null));
    }
}

