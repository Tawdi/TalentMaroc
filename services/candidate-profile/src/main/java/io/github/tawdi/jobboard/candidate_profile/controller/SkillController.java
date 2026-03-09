package io.github.tawdi.jobboard.candidate_profile.controller;

import io.github.tawdi.jobboard.candidate_profile.dto.ApiResponseDTO;
import io.github.tawdi.jobboard.candidate_profile.dto.SkillDTO;
import io.github.tawdi.jobboard.candidate_profile.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles/user/{userId}/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<SkillDTO>>> getAll(@PathVariable String userId) {
        List<SkillDTO> list = skillService.getAllByUserId(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Skills retrieved", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<SkillDTO>> create(
            @PathVariable String userId,
            @Valid @RequestBody SkillDTO dto) {
        SkillDTO created = skillService.create(userId, dto);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Skill created", created), HttpStatus.CREATED);
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<ApiResponseDTO<SkillDTO>> update(
            @PathVariable String userId,
            @PathVariable Long skillId,
            @Valid @RequestBody SkillDTO dto) {
        SkillDTO updated = skillService.update(userId, skillId, dto);
        return ResponseEntity.ok(ApiResponseDTO.success("Skill updated", updated));
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(
            @PathVariable String userId,
            @PathVariable Long skillId) {
        skillService.delete(userId, skillId);
        return ResponseEntity.ok(ApiResponseDTO.success("Skill deleted", null));
    }
}

