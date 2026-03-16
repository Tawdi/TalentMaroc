package io.github.tawdi.jobboard.applications.controller;

import io.github.tawdi.jobboard.applications.dto.ApiResponseDTO;
import io.github.tawdi.jobboard.applications.dto.ApplicationResponse;
import io.github.tawdi.jobboard.applications.dto.CreateApplicationRequest;
import io.github.tawdi.jobboard.applications.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for candidate-facing application endpoints.
 */
@RestController
@RequestMapping("/api/v1/applications/candidate/{userId}")
@RequiredArgsConstructor
public class CandidateApplicationController {

    private final ApplicationService applicationService;

    // ======================== SUBMIT APPLICATION ========================

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ApplicationResponse>> apply(
            @PathVariable String userId,
            @Valid @RequestBody CreateApplicationRequest request) {

        ApplicationResponse response = applicationService.apply(userId, request);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Application submitted successfully", response),
                HttpStatus.CREATED);
    }

    // ======================== MY APPLICATIONS ========================

    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<ApplicationResponse>>> getMyApplications(
            @PathVariable String userId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ApplicationResponse> applications = applicationService.getMyApplications(userId, pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Applications retrieved successfully", applications));
    }

    // ======================== APPLICATION DETAIL ========================

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApiResponseDTO<ApplicationResponse>> getApplicationById(
            @PathVariable String userId,
            @PathVariable Long applicationId) {

        ApplicationResponse response = applicationService.getApplicationForCandidate(userId, applicationId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Application retrieved successfully", response));
    }

    // ======================== WITHDRAW ========================

    @PatchMapping("/{applicationId}/withdraw")
    public ResponseEntity<ApiResponseDTO<ApplicationResponse>> withdrawApplication(
            @PathVariable String userId,
            @PathVariable Long applicationId) {

        ApplicationResponse response = applicationService.withdrawApplication(userId, applicationId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Application withdrawn successfully", response));
    }
}
