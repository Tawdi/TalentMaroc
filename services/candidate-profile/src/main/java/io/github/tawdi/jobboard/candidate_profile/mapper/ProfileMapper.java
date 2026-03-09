package io.github.tawdi.jobboard.candidate_profile.mapper;

import io.github.tawdi.jobboard.candidate_profile.dto.*;
import io.github.tawdi.jobboard.candidate_profile.entity.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProfileMapper {

    // ======================== PROFILE ========================

    public CandidateProfile toEntity(CreateProfileRequest req) {
        CandidateProfile profile = CandidateProfile.builder()
                .userId(req.getUserId())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .headline(req.getHeadline())
                .about(req.getAbout())
                .phone(req.getPhone())
                .dateOfBirth(req.getDateOfBirth())
                .build();

        if (req.getAddress() != null) {
            profile.setAddress(toAddress(req.getAddress()));
        }
        return profile;
    }

    public void updateEntity(CandidateProfile profile, UpdateProfileRequest req) {
        if (req.getFirstName() != null) profile.setFirstName(req.getFirstName());
        if (req.getLastName() != null) profile.setLastName(req.getLastName());
        if (req.getHeadline() != null) profile.setHeadline(req.getHeadline());
        if (req.getAbout() != null) profile.setAbout(req.getAbout());
        if (req.getPhone() != null) profile.setPhone(req.getPhone());
        if (req.getDateOfBirth() != null) profile.setDateOfBirth(req.getDateOfBirth());
        if (req.getAddress() != null) profile.setAddress(toAddress(req.getAddress()));
    }

    public ProfileResponse toResponse(CandidateProfile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .headline(profile.getHeadline())
                .about(profile.getAbout())
                .phone(profile.getPhone())
                .dateOfBirth(profile.getDateOfBirth())
                .photoUrl(profile.getPhotoUrl())
                .cvOriginalName(profile.getCvOriginalName())
                .address(profile.getAddress() != null ? toAddressDTO(profile.getAddress()) : null)
                .experiences(toExperienceDTOs(profile.getExperiences()))
                .formations(toFormationDTOs(profile.getFormations()))
                .skills(toSkillDTOs(profile.getSkills()))
                .projects(toProjectDTOs(profile.getProjects()))
                .spokenLanguages(toSpokenLanguageDTOs(profile.getSpokenLanguages()))
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    // ======================== ADDRESS ========================

    public Address toAddress(AddressDTO dto) {
        return Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode())
                .country(dto.getCountry())
                .build();
    }

    public AddressDTO toAddressDTO(Address addr) {
        return AddressDTO.builder()
                .street(addr.getStreet())
                .city(addr.getCity())
                .state(addr.getState())
                .zipCode(addr.getZipCode())
                .country(addr.getCountry())
                .build();
    }

    // ======================== EXPERIENCE ========================

    public Experience toExperience(ExperienceDTO dto) {
        return Experience.builder()
                .title(dto.getTitle())
                .company(dto.getCompany())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .contractType(dto.getContractType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .currentJob(dto.isCurrentJob())
                .build();
    }

    public void updateExperience(Experience entity, ExperienceDTO dto) {
        entity.setTitle(dto.getTitle());
        entity.setCompany(dto.getCompany());
        entity.setLocation(dto.getLocation());
        entity.setDescription(dto.getDescription());
        entity.setContractType(dto.getContractType());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setCurrentJob(dto.isCurrentJob());
    }

    public ExperienceDTO toExperienceDTO(Experience exp) {
        return ExperienceDTO.builder()
                .id(exp.getId())
                .title(exp.getTitle())
                .company(exp.getCompany())
                .location(exp.getLocation())
                .description(exp.getDescription())
                .contractType(exp.getContractType())
                .startDate(exp.getStartDate())
                .endDate(exp.getEndDate())
                .currentJob(exp.isCurrentJob())
                .build();
    }

    public List<ExperienceDTO> toExperienceDTOs(List<Experience> list) {
        if (list == null) return Collections.emptyList();
        return list.stream().map(this::toExperienceDTO).collect(Collectors.toList());
    }

    // ======================== FORMATION ========================

    public Formation toFormation(FormationDTO dto) {
        return Formation.builder()
                .institution(dto.getInstitution())
                .degree(dto.getDegree())
                .fieldOfStudy(dto.getFieldOfStudy())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .currentlyStudying(dto.isCurrentlyStudying())
                .build();
    }

    public void updateFormation(Formation entity, FormationDTO dto) {
        entity.setInstitution(dto.getInstitution());
        entity.setDegree(dto.getDegree());
        entity.setFieldOfStudy(dto.getFieldOfStudy());
        entity.setDescription(dto.getDescription());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setCurrentlyStudying(dto.isCurrentlyStudying());
    }

    public FormationDTO toFormationDTO(Formation f) {
        return FormationDTO.builder()
                .id(f.getId())
                .institution(f.getInstitution())
                .degree(f.getDegree())
                .fieldOfStudy(f.getFieldOfStudy())
                .description(f.getDescription())
                .startDate(f.getStartDate())
                .endDate(f.getEndDate())
                .currentlyStudying(f.isCurrentlyStudying())
                .build();
    }

    public List<FormationDTO> toFormationDTOs(List<Formation> list) {
        if (list == null) return Collections.emptyList();
        return list.stream().map(this::toFormationDTO).collect(Collectors.toList());
    }

    // ======================== SKILL ========================

    public Skill toSkill(SkillDTO dto) {
        return Skill.builder()
                .name(dto.getName())
                .level(dto.getLevel())
                .build();
    }

    public void updateSkill(Skill entity, SkillDTO dto) {
        entity.setName(dto.getName());
        entity.setLevel(dto.getLevel());
    }

    public SkillDTO toSkillDTO(Skill s) {
        return SkillDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .level(s.getLevel())
                .build();
    }

    public List<SkillDTO> toSkillDTOs(List<Skill> list) {
        if (list == null) return Collections.emptyList();
        return list.stream().map(this::toSkillDTO).collect(Collectors.toList());
    }

    // ======================== PROJECT ========================

    public Project toProject(ProjectDTO dto) {
        return Project.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .url(dto.getUrl())
                .technologies(dto.getTechnologies())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
    }

    public void updateProject(Project entity, ProjectDTO dto) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setUrl(dto.getUrl());
        entity.setTechnologies(dto.getTechnologies());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
    }

    public ProjectDTO toProjectDTO(Project p) {
        return ProjectDTO.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .url(p.getUrl())
                .technologies(p.getTechnologies())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .build();
    }

    public List<ProjectDTO> toProjectDTOs(List<Project> list) {
        if (list == null) return Collections.emptyList();
        return list.stream().map(this::toProjectDTO).collect(Collectors.toList());
    }

    // ======================== SPOKEN LANGUAGE ========================

    public SpokenLanguage toSpokenLanguage(SpokenLanguageDTO dto) {
        return SpokenLanguage.builder()
                .language(dto.getLanguage())
                .proficiency(dto.getProficiency())
                .build();
    }

    public void updateSpokenLanguage(SpokenLanguage entity, SpokenLanguageDTO dto) {
        entity.setLanguage(dto.getLanguage());
        entity.setProficiency(dto.getProficiency());
    }

    public SpokenLanguageDTO toSpokenLanguageDTO(SpokenLanguage sl) {
        return SpokenLanguageDTO.builder()
                .id(sl.getId())
                .language(sl.getLanguage())
                .proficiency(sl.getProficiency())
                .build();
    }

    public List<SpokenLanguageDTO> toSpokenLanguageDTOs(List<SpokenLanguage> list) {
        if (list == null) return Collections.emptyList();
        return list.stream().map(this::toSpokenLanguageDTO).collect(Collectors.toList());
    }
}

