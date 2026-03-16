package io.github.tawdi.jobboard.applications.controller;

import io.github.tawdi.jobboard.applications.dto.ApiResponseDTO;
import io.github.tawdi.jobboard.applications.dto.ApplicationResponse;
import io.github.tawdi.jobboard.applications.dto.UpdateApplicationStatusRequest;
import io.github.tawdi.jobboard.applications.entity.ApplicationStatus;
import io.github.tawdi.jobboard.applications.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for company/recruiter-facing application endpoints.
 */
@RestController
@RequestMapping("/api/v1/applications/company/{userId}")
@RequiredArgsConstructor
public class CompanyApplicationController {

    private final ApplicationService applicationService;

    // ======================== LIST APPLICATIONS FOR OFFER ========================

    @GetMapping("/offers/{offerId}")
    public ResponseEntity<ApiResponseDTO<Page<ApplicationResponse>>> getApplicationsForOffer(
            @PathVariable String userId,
            @PathVariable Long offerId,
            @RequestParam(required = false) ApplicationStatus status,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ApplicationResponse> applications = applicationService
                .getApplicationsForOffer(offerId, status, pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Applications retrieved successfully", applications));
    }

    // ======================== APPLICATION DETAIL ========================

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApiResponseDTO<ApplicationResponse>> getApplicationById(
            @PathVariable String userId,
            @PathVariable Long applicationId) {

        ApplicationResponse response = applicationService.getApplicationForCompany(applicationId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Application retrieved successfully", response));
    }

    // ======================== UPDATE STATUS ========================

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<ApiResponseDTO<ApplicationResponse>> updateApplicationStatus(
            @PathVariable String userId,
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {

        ApplicationResponse response = applicationService.updateApplicationStatus(applicationId, request);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Application status updated successfully", response));
    }
}
