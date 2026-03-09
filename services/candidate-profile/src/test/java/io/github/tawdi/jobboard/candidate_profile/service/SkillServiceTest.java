package io.github.tawdi.jobboard.candidate_profile.service;

import io.github.tawdi.jobboard.candidate_profile.dto.SkillDTO;
import io.github.tawdi.jobboard.candidate_profile.entity.CandidateProfile;
import io.github.tawdi.jobboard.candidate_profile.entity.Skill;
import io.github.tawdi.jobboard.candidate_profile.entity.SkillLevel;
import io.github.tawdi.jobboard.candidate_profile.exception.ResourceNotFoundException;
import io.github.tawdi.jobboard.candidate_profile.mapper.ProfileMapper;
import io.github.tawdi.jobboard.candidate_profile.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SkillService Tests")
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private CandidateProfileService profileService;

    @Mock
    private ProfileMapper mapper;

    @InjectMocks
    private SkillService skillService;

    private CandidateProfile testProfile;
    private Skill testSkill;
    private SkillDTO skillDTO;
    private final String TEST_USER_ID = "user-123-456";
    private final Long TEST_PROFILE_ID = 1L;
    private final Long TEST_SKILL_ID = 1L;

    @BeforeEach
    void setUp() {
        testProfile = CandidateProfile.builder()
                .id(TEST_PROFILE_ID)
                .userId(TEST_USER_ID)
                .firstName("John")
                .lastName("Doe")
                .build();

        testSkill = Skill.builder()
                .id(TEST_SKILL_ID)
                .name("Java")
                .level(SkillLevel.EXPERT)
                .profile(testProfile)
                .build();

        skillDTO = SkillDTO.builder()
                .id(TEST_SKILL_ID)
                .name("Java")
                .level(SkillLevel.EXPERT)
                .build();
    }

    @Nested
    @DisplayName("Get Skills Operations")
    class GetSkillsOperations {

        @Test
        @DisplayName("Should get all skills by user ID successfully")
        void shouldGetAllSkillsByUserId_WhenSkillsExist() {
            // Given
            List<Skill> skills = Arrays.asList(testSkill);
            List<SkillDTO> expectedSkillDTOs = Arrays.asList(skillDTO);

            when(profileService.findProfileByUserId(TEST_USER_ID)).thenReturn(testProfile);
            when(skillRepository.findByProfileId(TEST_PROFILE_ID)).thenReturn(skills);
            when(mapper.toSkillDTOs(skills)).thenReturn(expectedSkillDTOs);

            // When
            List<SkillDTO> result = skillService.getAllByUserId(TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Java");
            assertThat(result.get(0).getLevel()).isEqualTo(SkillLevel.EXPERT);

            verify(profileService).findProfileByUserId(TEST_USER_ID);
            verify(skillRepository).findByProfileId(TEST_PROFILE_ID);
            verify(mapper).toSkillDTOs(skills);
        }

        @Test
        @DisplayName("Should return empty list when no skills exist")
        void shouldReturnEmptyList_WhenNoSkillsExist() {
            // Given
            List<Skill> emptySkills = Arrays.asList();
            List<SkillDTO> emptySkillDTOs = Arrays.asList();

            when(profileService.findProfileByUserId(TEST_USER_ID)).thenReturn(testProfile);
            when(skillRepository.findByProfileId(TEST_PROFILE_ID)).thenReturn(emptySkills);
            when(mapper.toSkillDTOs(emptySkills)).thenReturn(emptySkillDTOs);

            // When
            List<SkillDTO> result = skillService.getAllByUserId(TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();

            verify(profileService).findProfileByUserId(TEST_USER_ID);
            verify(skillRepository).findByProfileId(TEST_PROFILE_ID);
            verify(mapper).toSkillDTOs(emptySkills);
        }

        @Test
        @DisplayName("Should throw exception when profile not found")
        void shouldThrowException_WhenProfileNotFound() {
            // Given
            when(profileService.findProfileByUserId(TEST_USER_ID))
                    .thenThrow(new ResourceNotFoundException("Profile not found"));

            // When & Then
            assertThatThrownBy(() -> skillService.getAllByUserId(TEST_USER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Profile not found");

            verify(profileService).findProfileByUserId(TEST_USER_ID);
            verify(skillRepository, never()).findByProfileId(any());
            verify(mapper, never()).toSkillDTOs(any());
        }
    }

    @Nested
    @DisplayName("Create Skill Operations")
    class CreateSkillOperations {

        @Test
        @DisplayName("Should create skill successfully")
        void shouldCreateSkill_WhenValidData() {
            // Given
            SkillDTO newSkillDTO = SkillDTO.builder()
                    .name("Spring Boot")
                    .level(SkillLevel.ADVANCED)
                    .build();

            Skill newSkill = Skill.builder()
                    .name("Spring Boot")
                    .level(SkillLevel.ADVANCED)
                    .build();

            Skill savedSkill = Skill.builder()
                    .id(2L)
                    .name("Spring Boot")
                    .level(SkillLevel.ADVANCED)
                    .profile(testProfile)
                    .build();

            SkillDTO expectedResponse = SkillDTO.builder()
                    .id(2L)
                    .name("Spring Boot")
                    .level(SkillLevel.ADVANCED)
                    .build();

            when(profileService.findProfileByUserId(TEST_USER_ID)).thenReturn(testProfile);
            when(mapper.toSkill(newSkillDTO)).thenReturn(newSkill);
            when(skillRepository.save(newSkill)).thenReturn(savedSkill);
            when(mapper.toSkillDTO(savedSkill)).thenReturn(expectedResponse);

            // When
            SkillDTO result = skillService.create(TEST_USER_ID, newSkillDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getName()).isEqualTo("Spring Boot");
            assertThat(result.getLevel()).isEqualTo(SkillLevel.ADVANCED);

            verify(profileService).findProfileByUserId(TEST_USER_ID);
            verify(mapper).toSkill(newSkillDTO);
            verify(skillRepository).save(newSkill);
            verify(mapper).toSkillDTO(savedSkill);

            // Verify that the profile is set on the skill
            verify(skillRepository).save(argThat(skill ->
                skill.getProfile().equals(testProfile)
            ));
        }

        @Test
        @DisplayName("Should throw exception when profile not found during creation")
        void shouldThrowException_WhenProfileNotFoundDuringCreation() {
            // Given
            SkillDTO newSkillDTO = SkillDTO.builder()
                    .name("React")
                    .level(SkillLevel.INTERMEDIATE)
                    .build();

            when(profileService.findProfileByUserId(TEST_USER_ID))
                    .thenThrow(new ResourceNotFoundException("Profile not found"));

            // When & Then
            assertThatThrownBy(() -> skillService.create(TEST_USER_ID, newSkillDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Profile not found");

            verify(profileService).findProfileByUserId(TEST_USER_ID);
            verify(mapper, never()).toSkill(any());
            verify(skillRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update Skill Operations")
    class UpdateSkillOperations {

        @Test
        @DisplayName("Should update skill successfully")
        void shouldUpdateSkill_WhenSkillExists() {
            // Given
            SkillDTO updateDTO = SkillDTO.builder()
                    .name("Java")
                    .level(SkillLevel.EXPERT)
                    .build();

            Skill updatedSkill = Skill.builder()
                    .id(TEST_SKILL_ID)
                    .name("Java")
                    .level(SkillLevel.EXPERT)
                    .profile(testProfile)
                    .build();

            when(profileService.findProfileByUserId(TEST_USER_ID)).thenReturn(testProfile);
            when(skillRepository.findByIdAndProfileId(TEST_SKILL_ID, TEST_PROFILE_ID))
                    .thenReturn(Optional.of(testSkill));
            when(skillRepository.save(testSkill)).thenReturn(updatedSkill);
            when(mapper.toSkillDTO(updatedSkill)).thenReturn(skillDTO);

            // When
            SkillDTO result = skillService.update(TEST_USER_ID, TEST_SKILL_ID, updateDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Java");
            assertThat(result.getLevel()).isEqualTo(SkillLevel.EXPERT);

            verify(profileService).findProfileByUserId(TEST_USER_ID);
            verify(skillRepository).findByIdAndProfileId(TEST_SKILL_ID, TEST_PROFILE_ID);
            verify(mapper).updateSkill(testSkill, updateDTO);
            verify(skillRepository).save(testSkill);
            verify(mapper).toSkillDTO(updatedSkill);
        }

        @Test
        @DisplayName("Should throw exception when skill not found for update")
        void shouldThrowException_WhenSkillNotFoundForUpdate() {
            // Given
            SkillDTO updateDTO = SkillDTO.builder()
                    .name("Java")
                    .level(SkillLevel.EXPERT)
                    .build();

            when(profileService.findProfileByUserId(TEST_USER_ID)).thenReturn(testProfile);
            when(skillRepository.findByIdAndProfileId(TEST_SKILL_ID, TEST_PROFILE_ID))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> skillService.update(TEST_USER_ID, TEST_SKILL_ID, updateDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Skill not found with id: " + TEST_SKILL_ID);

            verify(profileService).findProfileByUserId(TEST_USER_ID);
            verify(skillRepository).findByIdAndProfileId(TEST_SKILL_ID, TEST_PROFILE_ID);
            verify(mapper, never()).updateSkill(any(), any());
            verify(skillRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when profile not found during update")
        void shouldThrowException_WhenProfileNotFoundDuringUpdate() {
            // Given
            SkillDTO updateDTO = SkillDTO.builder()
                    .name("Java")
                    .level(SkillLevel.EXPERT)
                    .build();

            when(profileService.findProfileByUserId(TEST_USER_ID))
                    .thenThrow(new ResourceNotFoundException("Profile not found"));

            // When & Then
            assertThatThrownBy(() -> skillService.update(TEST_USER_ID, TEST_SKILL_ID, updateDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Profile not found");

            verify(profileService).findProfileByUserId(TEST_USER_ID);
            verify(skillRepository, never()).findByIdAndProfileId(any(), any());
            verify(mapper, never()).updateSkill(any(), any());
        }
    }

    @Nested
    @DisplayName("Delete Skill Operations")
    class DeleteSkillOperations {

        @Test
        @DisplayName("Should delete skill successfully")
        void shouldDeleteSkill_WhenSkillExists() {
            // Given
            when(profileService.findProfileByUserId(TEST_USER_ID)).thenReturn(testProfile);
            when(skillRepository.findByIdAndProfileId(TEST_SKILL_ID, TEST_PROFILE_ID))
                    .thenReturn(Optional.of(testSkill));

            // When
            skillService.delete(TEST_USER_ID, TEST_SKILL_ID);

            // Then
            verify(profileService).findProfileByUserId(TEST_USER_ID);
            verify(skillRepository).findByIdAndProfileId(TEST_SKILL_ID, TEST_PROFILE_ID);
            verify(skillRepository).delete(testSkill);
        }

        @Test
        @DisplayName("Should throw exception when skill not found for deletion")
        void shouldThrowException_WhenSkillNotFoundForDeletion() {
            // Given
            when(profileService.findProfileByUserId(TEST_USER_ID)).thenReturn(testProfile);
            when(skillRepository.findByIdAndProfileId(TEST_SKILL_ID, TEST_PROFILE_ID))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> skillService.delete(TEST_USER_ID, TEST_SKILL_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Skill not found with id: " + TEST_SKILL_ID);

            verify(profileService).findProfileByUserId(TEST_USER_ID);
            verify(skillRepository).findByIdAndProfileId(TEST_SKILL_ID, TEST_PROFILE_ID);
            verify(skillRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should throw exception when profile not found during deletion")
        void shouldThrowException_WhenProfileNotFoundDuringDeletion() {
            // Given
            when(profileService.findProfileByUserId(TEST_USER_ID))
                    .thenThrow(new ResourceNotFoundException("Profile not found"));

            // When & Then
            assertThatThrownBy(() -> skillService.delete(TEST_USER_ID, TEST_SKILL_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Profile not found");

            verify(profileService).findProfileByUserId(TEST_USER_ID);
            verify(skillRepository, never()).findByIdAndProfileId(any(), any());
            verify(skillRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandling {

        @Test
        @DisplayName("Should handle multiple skills with different levels")
        void shouldHandleMultipleSkillsWithDifferentLevels() {
            // Given
            Skill skill1 = Skill.builder()
                    .id(1L)
                    .name("Java")
                    .level(SkillLevel.EXPERT)
                    .profile(testProfile)
                    .build();

            Skill skill2 = Skill.builder()
                    .id(2L)
                    .name("Python")
                    .level(SkillLevel.INTERMEDIATE)
                    .profile(testProfile)
                    .build();

            List<Skill> skills = Arrays.asList(skill1, skill2);
            List<SkillDTO> skillDTOs = Arrays.asList(
                    SkillDTO.builder().id(1L).name("Java").level(SkillLevel.EXPERT).build(),
                    SkillDTO.builder().id(2L).name("Python").level(SkillLevel.INTERMEDIATE).build()
            );

            when(profileService.findProfileByUserId(TEST_USER_ID)).thenReturn(testProfile);
            when(skillRepository.findByProfileId(TEST_PROFILE_ID)).thenReturn(skills);
            when(mapper.toSkillDTOs(skills)).thenReturn(skillDTOs);

            // When
            List<SkillDTO> result = skillService.getAllByUserId(TEST_USER_ID);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getLevel()).isEqualTo(SkillLevel.EXPERT);
            assertThat(result.get(1).getLevel()).isEqualTo(SkillLevel.INTERMEDIATE);
        }

        @Test
        @DisplayName("Should validate skill level enum values")
        void shouldValidateSkillLevelEnumValues() {
            // Given
            for (SkillLevel level : SkillLevel.values()) {
                SkillDTO skillDTO = SkillDTO.builder()
                        .name("TestSkill")
                        .level(level)
                        .build();

                Skill skill = Skill.builder()
                        .name("TestSkill")
                        .level(level)
                        .build();

                when(profileService.findProfileByUserId(TEST_USER_ID)).thenReturn(testProfile);
                when(mapper.toSkill(skillDTO)).thenReturn(skill);
                when(skillRepository.save(any(Skill.class))).thenReturn(skill);
                when(mapper.toSkillDTO(skill)).thenReturn(skillDTO);

                // When & Then - Should not throw exception for any valid enum value
                assertThatCode(() -> skillService.create(TEST_USER_ID, skillDTO))
                        .doesNotThrowAnyException();
            }
        }
    }
}
