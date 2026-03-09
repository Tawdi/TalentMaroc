package io.github.tawdi.jobboard.candidate_profile.service;

import io.github.tawdi.jobboard.candidate_profile.dto.*;
import io.github.tawdi.jobboard.candidate_profile.entity.CandidateProfile;
import io.github.tawdi.jobboard.candidate_profile.exception.ProfileAlreadyExistsException;
import io.github.tawdi.jobboard.candidate_profile.exception.ResourceNotFoundException;
import io.github.tawdi.jobboard.candidate_profile.mapper.ProfileMapper;
import io.github.tawdi.jobboard.candidate_profile.repository.CandidateProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CandidateProfileService Tests")
class CandidateProfileServiceTest {

    @Mock
    private CandidateProfileRepository profileRepository;

    @Mock
    private ProfileMapper mapper;

    @Mock
    private CvStorageService cvStorageService;

    @InjectMocks
    private CandidateProfileService candidateProfileService;

    private CandidateProfile testProfile;
    private CreateProfileRequest createRequest;
    private UpdateProfileRequest updateRequest;
    private ProfileResponse profileResponse;
    private final String TEST_USER_ID = "user-123-456";
    private final Long TEST_PROFILE_ID = 1L;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testProfile = CandidateProfile.builder()
                .id(TEST_PROFILE_ID)
                .userId(TEST_USER_ID)
                .firstName("John")
                .lastName("Doe")
                .headline("Software Developer")
                .about("Experienced developer")
                .phone("+1234567890")
                .cvFilePath("/path/to/cv.pdf")
                .cvOriginalName("john_doe_cv.pdf")
                .build();

        AddressDTO addressDTO = AddressDTO.builder()
                .street("123 Main St")
                .city("New York")
                .state("NY")
                .zipCode("10001")
                .country("USA")
                .build();

        createRequest = CreateProfileRequest.builder()
                .userId(TEST_USER_ID)
                .firstName("John")
                .lastName("Doe")
                .headline("Software Developer")
                .about("Experienced developer")
                .phone("+1234567890")
                .address(addressDTO)
                .build();

        updateRequest = UpdateProfileRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .headline("Senior Software Developer")
                .about("Very experienced developer")
                .phone("+9876543210")
                .address(addressDTO)
                .build();

        profileResponse = ProfileResponse.builder()
                .id(TEST_PROFILE_ID)
                .userId(TEST_USER_ID)
                .firstName("John")
                .lastName("Doe")
                .headline("Software Developer")
                .about("Experienced developer")
                .phone("+1234567890")
                .build();
    }

    @Nested
    @DisplayName("Profile CRUD Operations")
    class ProfileCrudOperations {

        @Test
        @DisplayName("Should create profile successfully when user doesn't have profile")
        void shouldCreateProfile_WhenUserDoesNotHaveProfile() {
            // Given
            when(profileRepository.existsByUserId(TEST_USER_ID)).thenReturn(false);
            when(mapper.toEntity(createRequest)).thenReturn(testProfile);
            when(profileRepository.save(any(CandidateProfile.class))).thenReturn(testProfile);
            when(mapper.toResponse(testProfile)).thenReturn(profileResponse);

            // When
            ProfileResponse result = candidateProfileService.createProfile(createRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
            verify(profileRepository).existsByUserId(TEST_USER_ID);
            verify(profileRepository).save(any(CandidateProfile.class));
            verify(mapper).toEntity(createRequest);
            verify(mapper).toResponse(testProfile);
        }

        @Test
        @DisplayName("Should throw ProfileAlreadyExistsException when user already has profile")
        void shouldThrowException_WhenUserAlreadyHasProfile() {
            // Given
            when(profileRepository.existsByUserId(TEST_USER_ID)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> candidateProfileService.createProfile(createRequest))
                    .isInstanceOf(ProfileAlreadyExistsException.class)
                    .hasMessageContaining("Profile already exists for user: " + TEST_USER_ID);

            verify(profileRepository).existsByUserId(TEST_USER_ID);
            verify(profileRepository, never()).save(any());
            verify(mapper, never()).toEntity(any());
        }

        @Test
        @DisplayName("Should get profile by user ID successfully")
        void shouldGetProfileByUserId_WhenProfileExists() {
            // Given
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));
            when(mapper.toResponse(testProfile)).thenReturn(profileResponse);

            // When
            ProfileResponse result = candidateProfileService.getProfileByUserId(TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
            verify(profileRepository).findByUserId(TEST_USER_ID);
            verify(mapper).toResponse(testProfile);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when profile not found by user ID")
        void shouldThrowException_WhenProfileNotFoundByUserId() {
            // Given
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> candidateProfileService.getProfileByUserId(TEST_USER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Profile not found for userId: " + TEST_USER_ID);

            verify(profileRepository).findByUserId(TEST_USER_ID);
            verify(mapper, never()).toResponse(any());
        }

        @Test
        @DisplayName("Should get profile by profile ID successfully")
        void shouldGetProfileById_WhenProfileExists() {
            // Given
            when(profileRepository.findById(TEST_PROFILE_ID)).thenReturn(Optional.of(testProfile));
            when(mapper.toResponse(testProfile)).thenReturn(profileResponse);

            // When
            ProfileResponse result = candidateProfileService.getProfileById(TEST_PROFILE_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(TEST_PROFILE_ID);
            verify(profileRepository).findById(TEST_PROFILE_ID);
            verify(mapper).toResponse(testProfile);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when profile not found by ID")
        void shouldThrowException_WhenProfileNotFoundById() {
            // Given
            when(profileRepository.findById(TEST_PROFILE_ID)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> candidateProfileService.getProfileById(TEST_PROFILE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Profile not found with id: " + TEST_PROFILE_ID);

            verify(profileRepository).findById(TEST_PROFILE_ID);
            verify(mapper, never()).toResponse(any());
        }

        @Test
        @DisplayName("Should update profile successfully")
        void shouldUpdateProfile_WhenProfileExists() {
            // Given
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));
            when(profileRepository.save(testProfile)).thenReturn(testProfile);
            when(mapper.toResponse(testProfile)).thenReturn(profileResponse);

            // When
            ProfileResponse result = candidateProfileService.updateProfile(TEST_USER_ID, updateRequest);

            // Then
            assertThat(result).isNotNull();
            verify(profileRepository).findByUserId(TEST_USER_ID);
            verify(mapper).updateEntity(testProfile, updateRequest);
            verify(profileRepository).save(testProfile);
            verify(mapper).toResponse(testProfile);
        }

        @Test
        @DisplayName("Should delete profile successfully without CV")
        void shouldDeleteProfile_WhenProfileExistsWithoutCv() {
            // Given
            testProfile.setCvFilePath(null);
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));

            // When
            candidateProfileService.deleteProfile(TEST_USER_ID);

            // Then
            verify(profileRepository).findByUserId(TEST_USER_ID);
            verify(profileRepository).delete(testProfile);
            verify(cvStorageService, never()).deleteFile(any());
        }

        @Test
        @DisplayName("Should delete profile and CV successfully")
        void shouldDeleteProfile_WhenProfileExistsWithCv() {
            // Given
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));

            // When
            candidateProfileService.deleteProfile(TEST_USER_ID);

            // Then
            verify(profileRepository).findByUserId(TEST_USER_ID);
            verify(cvStorageService).deleteFile(testProfile.getCvFilePath());
            verify(profileRepository).delete(testProfile);
        }
    }

    @Nested
    @DisplayName("CV Management")
    class CvManagement {

        private MockMultipartFile mockCvFile;

        @BeforeEach
        void setUp() {
            mockCvFile = new MockMultipartFile(
                    "file",
                    "test-cv.pdf",
                    "application/pdf",
                    "PDF content".getBytes()
            );
        }

        @Test
        @DisplayName("Should upload CV successfully")
        void shouldUploadCv_WhenValidPdfFile() {
            // Given
            String expectedFilePath = "/uploads/cv/test-file.pdf";
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));
            when(cvStorageService.storeFile(mockCvFile, TEST_USER_ID)).thenReturn(expectedFilePath);
            when(profileRepository.save(testProfile)).thenReturn(testProfile);
            when(mapper.toResponse(testProfile)).thenReturn(profileResponse);

            // When
            ProfileResponse result = candidateProfileService.uploadCv(TEST_USER_ID, mockCvFile);

            // Then
            assertThat(result).isNotNull();
            verify(profileRepository).findByUserId(TEST_USER_ID);
            verify(cvStorageService).deleteFile("/path/to/cv.pdf"); // Delete old CV - use the actual path from testProfile
            verify(cvStorageService).storeFile(mockCvFile, TEST_USER_ID);
            verify(profileRepository).save(testProfile);
        }

        @Test
        @DisplayName("Should throw exception when uploading non-PDF file")
        void shouldThrowException_WhenUploadingNonPdfFile() {
            // Given
            MockMultipartFile nonPdfFile = new MockMultipartFile(
                    "file",
                    "test-doc.docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "DOCX content".getBytes()
            );
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));

            // When & Then
            assertThatThrownBy(() -> candidateProfileService.uploadCv(TEST_USER_ID, nonPdfFile))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Only PDF files are accepted for CV upload");

            verify(profileRepository).findByUserId(TEST_USER_ID);
            verify(cvStorageService, never()).storeFile(any(), any());
        }

        @Test
        @DisplayName("Should download CV successfully")
        void shouldDownloadCv_WhenCvExists() {
            // Given
            byte[] expectedContent = "PDF content".getBytes();
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));
            when(cvStorageService.loadFile(testProfile.getCvFilePath())).thenReturn(expectedContent);

            // When
            byte[] result = candidateProfileService.downloadCv(TEST_USER_ID);

            // Then
            assertThat(result).isEqualTo(expectedContent);
            verify(profileRepository).findByUserId(TEST_USER_ID);
            verify(cvStorageService).loadFile(testProfile.getCvFilePath());
        }

        @Test
        @DisplayName("Should throw exception when downloading non-existent CV")
        void shouldThrowException_WhenDownloadingNonExistentCv() {
            // Given
            testProfile.setCvFilePath(null);
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));

            // When & Then
            assertThatThrownBy(() -> candidateProfileService.downloadCv(TEST_USER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No CV found for user: " + TEST_USER_ID);

            verify(profileRepository).findByUserId(TEST_USER_ID);
            verify(cvStorageService, never()).loadFile(any());
        }

        @Test
        @DisplayName("Should delete CV successfully")
        void shouldDeleteCv_WhenCvExists() {
            // Given
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));

            // When
            candidateProfileService.deleteCv(TEST_USER_ID);

            // Then
            verify(profileRepository).findByUserId(TEST_USER_ID);
            verify(cvStorageService).deleteFile("/path/to/cv.pdf"); // Use the actual path from testProfile
            verify(profileRepository).save(testProfile);
        }

        @Test
        @DisplayName("Should do nothing when deleting non-existent CV")
        void shouldDoNothing_WhenDeletingNonExistentCv() {
            // Given
            testProfile.setCvFilePath(null);
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));

            // When
            candidateProfileService.deleteCv(TEST_USER_ID);

            // Then
            verify(profileRepository).findByUserId(TEST_USER_ID);
            verify(cvStorageService, never()).deleteFile(any());
            verify(profileRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should get CV original name successfully")
        void shouldGetCvOriginalName_WhenProfileExists() {
            // Given
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));

            // When
            String result = candidateProfileService.getCvOriginalName(TEST_USER_ID);

            // Then
            assertThat(result).isEqualTo(testProfile.getCvOriginalName());
            verify(profileRepository).findByUserId(TEST_USER_ID);
        }
    }

    @Nested
    @DisplayName("Helper Methods")
    class HelperMethods {

        @Test
        @DisplayName("Should find profile by user ID successfully")
        void shouldFindProfileByUserId_WhenProfileExists() {
            // Given
            when(profileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));

            // When
            CandidateProfile result = candidateProfileService.findProfileByUserId(TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
            verify(profileRepository).findByUserId(TEST_USER_ID);
        }

        @Test
        @DisplayName("Should find profile by ID successfully")
        void shouldFindProfileById_WhenProfileExists() {
            // Given
            when(profileRepository.findById(TEST_PROFILE_ID)).thenReturn(Optional.of(testProfile));

            // When
            CandidateProfile result = candidateProfileService.findProfileById(TEST_PROFILE_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(TEST_PROFILE_ID);
            verify(profileRepository).findById(TEST_PROFILE_ID);
        }
    }
}
