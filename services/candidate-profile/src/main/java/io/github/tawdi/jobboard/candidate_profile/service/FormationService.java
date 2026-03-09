package io.github.tawdi.jobboard.candidate_profile.service;

import io.github.tawdi.jobboard.candidate_profile.dto.FormationDTO;
import io.github.tawdi.jobboard.candidate_profile.entity.CandidateProfile;
import io.github.tawdi.jobboard.candidate_profile.entity.Formation;
import io.github.tawdi.jobboard.candidate_profile.exception.ResourceNotFoundException;
import io.github.tawdi.jobboard.candidate_profile.mapper.ProfileMapper;
import io.github.tawdi.jobboard.candidate_profile.repository.FormationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FormationService {

    private final FormationRepository formationRepository;
    private final CandidateProfileService profileService;
    private final ProfileMapper mapper;

    @Transactional(readOnly = true)
    public List<FormationDTO> getAllByUserId(String userId) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        return mapper.toFormationDTOs(
                formationRepository.findByProfileIdOrderByStartDateDesc(profile.getId()));
    }

    public FormationDTO create(String userId, FormationDTO dto) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        Formation formation = mapper.toFormation(dto);
        formation.setProfile(profile);
        Formation saved = formationRepository.save(formation);
        log.info("Created formation id={} for userId={}", saved.getId(), userId);
        return mapper.toFormationDTO(saved);
    }

    public FormationDTO update(String userId, Long formationId, FormationDTO dto) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        Formation formation = formationRepository.findByIdAndProfileId(formationId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Formation not found with id: " + formationId));
        mapper.updateFormation(formation, dto);
        Formation saved = formationRepository.save(formation);
        log.info("Updated formation id={} for userId={}", formationId, userId);
        return mapper.toFormationDTO(saved);
    }

    public void delete(String userId, Long formationId) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        Formation formation = formationRepository.findByIdAndProfileId(formationId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Formation not found with id: " + formationId));
        formationRepository.delete(formation);
        log.info("Deleted formation id={} for userId={}", formationId, userId);
    }
}

