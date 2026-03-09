package io.github.tawdi.jobboard.candidate_profile.service;

import io.github.tawdi.jobboard.candidate_profile.dto.SkillDTO;
import io.github.tawdi.jobboard.candidate_profile.entity.CandidateProfile;
import io.github.tawdi.jobboard.candidate_profile.entity.Skill;
import io.github.tawdi.jobboard.candidate_profile.exception.ResourceNotFoundException;
import io.github.tawdi.jobboard.candidate_profile.mapper.ProfileMapper;
import io.github.tawdi.jobboard.candidate_profile.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;
    private final CandidateProfileService profileService;
    private final ProfileMapper mapper;

    @Transactional(readOnly = true)
    public List<SkillDTO> getAllByUserId(String userId) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        return mapper.toSkillDTOs(skillRepository.findByProfileId(profile.getId()));
    }

    public SkillDTO create(String userId, SkillDTO dto) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        Skill skill = mapper.toSkill(dto);
        skill.setProfile(profile);
        Skill saved = skillRepository.save(skill);
        log.info("Created skill id={} for userId={}", saved.getId(), userId);
        return mapper.toSkillDTO(saved);
    }

    public SkillDTO update(String userId, Long skillId, SkillDTO dto) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        Skill skill = skillRepository.findByIdAndProfileId(skillId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));
        mapper.updateSkill(skill, dto);
        Skill saved = skillRepository.save(skill);
        log.info("Updated skill id={} for userId={}", skillId, userId);
        return mapper.toSkillDTO(saved);
    }

    public void delete(String userId, Long skillId) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        Skill skill = skillRepository.findByIdAndProfileId(skillId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));
        skillRepository.delete(skill);
        log.info("Deleted skill id={} for userId={}", skillId, userId);
    }
}

