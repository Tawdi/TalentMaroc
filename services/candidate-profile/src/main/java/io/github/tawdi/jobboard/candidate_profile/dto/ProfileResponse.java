package io.github.tawdi.jobboard.candidate_profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String headline;
    private String about;
    private String phone;
    private LocalDate dateOfBirth;
    private String photoUrl;
    private String cvOriginalName;

    private AddressDTO address;
    private List<ExperienceDTO> experiences;
    private List<FormationDTO> formations;
    private List<SkillDTO> skills;
    private List<ProjectDTO> projects;
    private List<SpokenLanguageDTO> spokenLanguages;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

