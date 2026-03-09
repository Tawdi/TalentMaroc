package io.github.tawdi.jobboard.candidate_profile.controller;

import io.github.tawdi.jobboard.candidate_profile.dto.ApiResponseDTO;
import io.github.tawdi.jobboard.candidate_profile.dto.CreateProfileRequest;
import io.github.tawdi.jobboard.candidate_profile.dto.ProfileResponse;
import io.github.tawdi.jobboard.candidate_profile.dto.UpdateProfileRequest;
import io.github.tawdi.jobboard.candidate_profile.service.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService profileService;

    // ======================== PROFILE CRUD ========================

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ProfileResponse>> createProfile(
            @Valid @RequestBody CreateProfileRequest request) {
        ProfileResponse response = profileService.createProfile(request);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Profile created successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<ProfileResponse>> getProfileByUserId(
            @PathVariable String userId) {
        ProfileResponse response = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Profile retrieved successfully", response));
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ApiResponseDTO<ProfileResponse>> getProfileById(
            @PathVariable Long profileId) {
        ProfileResponse response = profileService.getProfileById(profileId);
        return ResponseEntity.ok(ApiResponseDTO.success("Profile retrieved successfully", response));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<ProfileResponse>> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = profileService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponseDTO.success("Profile updated successfully", response));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteProfile(@PathVariable String userId) {
        profileService.deleteProfile(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Profile deleted successfully", null));
    }

    // ======================== CV MANAGEMENT ========================

    @PostMapping("/user/{userId}/cv")
    public ResponseEntity<ApiResponseDTO<ProfileResponse>> uploadCv(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file) {
        ProfileResponse response = profileService.uploadCv(userId, file);
        return ResponseEntity.ok(ApiResponseDTO.success("CV uploaded successfully", response));
    }

    @GetMapping("/user/{userId}/cv")
    public ResponseEntity<byte[]> downloadCv(@PathVariable String userId) {
        byte[] cvData = profileService.downloadCv(userId);
        String originalName = profileService.getCvOriginalName(userId);
        if (originalName == null) originalName = "cv.pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + originalName + "\"")
                .body(cvData);
    }

    @DeleteMapping("/user/{userId}/cv")
    public ResponseEntity<ApiResponseDTO<Void>> deleteCv(@PathVariable String userId) {
        profileService.deleteCv(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("CV deleted successfully", null));
    }
}

