package io.github.tawdi.jobboard.candidate_profile.service;

import io.github.tawdi.jobboard.candidate_profile.dto.*;
import io.github.tawdi.jobboard.candidate_profile.entity.CandidateProfile;
import io.github.tawdi.jobboard.candidate_profile.exception.ProfileAlreadyExistsException;
import io.github.tawdi.jobboard.candidate_profile.exception.ResourceNotFoundException;
import io.github.tawdi.jobboard.candidate_profile.mapper.ProfileMapper;
import io.github.tawdi.jobboard.candidate_profile.repository.CandidateProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CandidateProfileService {

    private final CandidateProfileRepository profileRepository;
    private final ProfileMapper mapper;
    private final CvStorageService cvStorageService;

    // ======================== PROFILE CRUD ========================

    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUserId(String userId) {
        CandidateProfile profile = findProfileByUserId(userId);
        return mapper.toResponse(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileById(Long profileId) {
        CandidateProfile profile = findProfileById(profileId);
        return mapper.toResponse(profile);
    }

    public ProfileResponse createProfile(CreateProfileRequest request) {
        if (profileRepository.existsByUserId(request.getUserId())) {
            throw new ProfileAlreadyExistsException(
                    "Profile already exists for user: " + request.getUserId());
        }

        CandidateProfile profile = mapper.toEntity(request);
        CandidateProfile saved = profileRepository.save(profile);
        log.info("Created profile for userId={}", request.getUserId());
        return mapper.toResponse(saved);
    }

    public ProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        CandidateProfile profile = findProfileByUserId(userId);
        mapper.updateEntity(profile, request);
        CandidateProfile saved = profileRepository.save(profile);
        log.info("Updated profile for userId={}", userId);
        return mapper.toResponse(saved);
    }

    public void deleteProfile(String userId) {
        CandidateProfile profile = findProfileByUserId(userId);
        // Delete CV file if exists
        if (profile.getCvFilePath() != null) {
            cvStorageService.deleteFile(profile.getCvFilePath());
        }
        profileRepository.delete(profile);
        log.info("Deleted profile for userId={}", userId);
    }

    // ======================== CV MANAGEMENT ========================

    public ProfileResponse uploadCv(String userId, MultipartFile file) {
        CandidateProfile profile = findProfileByUserId(userId);

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Only PDF files are accepted for CV upload");
        }

        // Delete old CV if exists
        if (profile.getCvFilePath() != null) {
            cvStorageService.deleteFile(profile.getCvFilePath());
        }

        String filePath = cvStorageService.storeFile(file, userId);
        profile.setCvFilePath(filePath);
        profile.setCvOriginalName(file.getOriginalFilename());
        CandidateProfile saved = profileRepository.save(profile);
        log.info("Uploaded CV for userId={}", userId);
        return mapper.toResponse(saved);
    }

    public void deleteCv(String userId) {
        CandidateProfile profile = findProfileByUserId(userId);
        if (profile.getCvFilePath() != null) {
            cvStorageService.deleteFile(profile.getCvFilePath());
            profile.setCvFilePath(null);
            profile.setCvOriginalName(null);
            profileRepository.save(profile);
            log.info("Deleted CV for userId={}", userId);
        }
    }

    public byte[] downloadCv(String userId) {
        CandidateProfile profile = findProfileByUserId(userId);
        if (profile.getCvFilePath() == null) {
            throw new ResourceNotFoundException("No CV found for user: " + userId);
        }
        return cvStorageService.loadFile(profile.getCvFilePath());
    }

    public String getCvOriginalName(String userId) {
        CandidateProfile profile = findProfileByUserId(userId);
        return profile.getCvOriginalName();
    }

    // ======================== HELPERS ========================

    public CandidateProfile findProfileByUserId(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));
    }

    public CandidateProfile findProfileById(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + profileId));
    }
}

