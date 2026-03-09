package io.github.tawdi.jobboard.candidate_profile.service;

import io.github.tawdi.jobboard.candidate_profile.dto.SpokenLanguageDTO;
import io.github.tawdi.jobboard.candidate_profile.entity.CandidateProfile;
import io.github.tawdi.jobboard.candidate_profile.entity.SpokenLanguage;
import io.github.tawdi.jobboard.candidate_profile.exception.ResourceNotFoundException;
import io.github.tawdi.jobboard.candidate_profile.mapper.ProfileMapper;
import io.github.tawdi.jobboard.candidate_profile.repository.SpokenLanguageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpokenLanguageService {

    private final SpokenLanguageRepository spokenLanguageRepository;
    private final CandidateProfileService profileService;
    private final ProfileMapper mapper;

    @Transactional(readOnly = true)
    public List<SpokenLanguageDTO> getAllByUserId(String userId) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        return mapper.toSpokenLanguageDTOs(spokenLanguageRepository.findByProfileId(profile.getId()));
    }

    public SpokenLanguageDTO create(String userId, SpokenLanguageDTO dto) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        SpokenLanguage lang = mapper.toSpokenLanguage(dto);
        lang.setProfile(profile);
        SpokenLanguage saved = spokenLanguageRepository.save(lang);
        log.info("Created spoken language id={} for userId={}", saved.getId(), userId);
        return mapper.toSpokenLanguageDTO(saved);
    }

    public SpokenLanguageDTO update(String userId, Long languageId, SpokenLanguageDTO dto) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        SpokenLanguage lang = spokenLanguageRepository.findByIdAndProfileId(languageId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Spoken language not found with id: " + languageId));
        mapper.updateSpokenLanguage(lang, dto);
        SpokenLanguage saved = spokenLanguageRepository.save(lang);
        log.info("Updated spoken language id={} for userId={}", languageId, userId);
        return mapper.toSpokenLanguageDTO(saved);
    }

    public void delete(String userId, Long languageId) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        SpokenLanguage lang = spokenLanguageRepository.findByIdAndProfileId(languageId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Spoken language not found with id: " + languageId));
        spokenLanguageRepository.delete(lang);
        log.info("Deleted spoken language id={} for userId={}", languageId, userId);
    }
}

