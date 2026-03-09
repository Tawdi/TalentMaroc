package io.github.tawdi.jobboard.candidate_profile.service;

import io.github.tawdi.jobboard.candidate_profile.dto.ProjectDTO;
import io.github.tawdi.jobboard.candidate_profile.entity.CandidateProfile;
import io.github.tawdi.jobboard.candidate_profile.entity.Project;
import io.github.tawdi.jobboard.candidate_profile.exception.ResourceNotFoundException;
import io.github.tawdi.jobboard.candidate_profile.mapper.ProfileMapper;
import io.github.tawdi.jobboard.candidate_profile.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CandidateProfileService profileService;
    private final ProfileMapper mapper;

    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllByUserId(String userId) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        return mapper.toProjectDTOs(projectRepository.findByProfileId(profile.getId()));
    }

    public ProjectDTO create(String userId, ProjectDTO dto) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        Project project = mapper.toProject(dto);
        project.setProfile(profile);
        Project saved = projectRepository.save(project);
        log.info("Created project id={} for userId={}", saved.getId(), userId);
        return mapper.toProjectDTO(saved);
    }

    public ProjectDTO update(String userId, Long projectId, ProjectDTO dto) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        Project project = projectRepository.findByIdAndProfileId(projectId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        mapper.updateProject(project, dto);
        Project saved = projectRepository.save(project);
        log.info("Updated project id={} for userId={}", projectId, userId);
        return mapper.toProjectDTO(saved);
    }

    public void delete(String userId, Long projectId) {
        CandidateProfile profile = profileService.findProfileByUserId(userId);
        Project project = projectRepository.findByIdAndProfileId(projectId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        projectRepository.delete(project);
        log.info("Deleted project id={} for userId={}", projectId, userId);
    }
}

