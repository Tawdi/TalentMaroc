package io.github.tawdi.jobboard.candidate_profile.service;

import io.github.tawdi.jobboard.candidate_profile.dto.ExperienceDTO;
import io.github.tawdi.jobboard.candidate_profile.entity.CandidateProfile;
import io.github.tawdi.jobboard.candidate_profile.entity.Experience;
import io.github.tawdi.jobboard.candidate_profile.exception.ResourceNotFoundException;
import io.github.tawdi.jobboard.candidate_profile.mapper.ProfileMapper;
import io.github.tawdi.jobboard.candidate_profile.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final CandidateProfileService profileService;
    private final ProfileMapper mapper;

    @Transactional(readOnly = true)
    public List<ExperienceDTO> getAllByUserId(String userId) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        return mapper.toExperienceDTOs(
                experienceRepository.findByProfileIdOrderByStartDateDesc(profile.getId()));
    }

    public ExperienceDTO create(String userId, ExperienceDTO dto) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        Experience experience = mapper.toExperience(dto);
        experience.setProfile(profile);
        Experience saved = experienceRepository.save(experience);
        log.info("Created experience id={} for userId={}", saved.getId(), userId);
        return mapper.toExperienceDTO(saved);
    }

    public ExperienceDTO update(String userId, Long experienceId, ExperienceDTO dto) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        Experience experience = experienceRepository.findByIdAndProfileId(experienceId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found with id: " + experienceId));
        mapper.updateExperience(experience, dto);
        Experience saved = experienceRepository.save(experience);
        log.info("Updated experience id={} for userId={}", experienceId, userId);
        return mapper.toExperienceDTO(saved);
    }

    public void delete(String userId, Long experienceId) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        Experience experience = experienceRepository.findByIdAndProfileId(experienceId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found with id: " + experienceId));
        experienceRepository.delete(experience);
        log.info("Deleted experience id={} for userId={}", experienceId, userId);
    }
}

