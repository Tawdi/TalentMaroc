package io.github.tawdi.jobboard.candidate_profile.controller;

import io.github.tawdi.jobboard.candidate_profile.dto.ApiResponseDTO;
import io.github.tawdi.jobboard.candidate_profile.dto.ProjectDTO;
import io.github.tawdi.jobboard.candidate_profile.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles/user/{userId}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ProjectDTO>>> getAll(@PathVariable String userId) {
        List<ProjectDTO> list = projectService.getAllByUserId(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Projects retrieved", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ProjectDTO>> create(
            @PathVariable String userId,
            @Valid @RequestBody ProjectDTO dto) {
        ProjectDTO created = projectService.create(userId, dto);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Project created", created), HttpStatus.CREATED);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponseDTO<ProjectDTO>> update(
            @PathVariable String userId,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectDTO dto) {
        ProjectDTO updated = projectService.update(userId, projectId, dto);
        return ResponseEntity.ok(ApiResponseDTO.success("Project updated", updated));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(
            @PathVariable String userId,
            @PathVariable Long projectId) {
        projectService.delete(userId, projectId);
        return ResponseEntity.ok(ApiResponseDTO.success("Project deleted", null));
    }
}

