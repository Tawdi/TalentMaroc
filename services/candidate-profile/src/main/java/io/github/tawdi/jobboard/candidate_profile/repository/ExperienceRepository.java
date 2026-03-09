package io.github.tawdi.jobboard.candidate_profile.repository;

import io.github.tawdi.jobboard.candidate_profile.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByProfileIdOrderByStartDateDesc(Long profileId);

    Optional<Experience> findByIdAndProfileId(Long id, Long profileId);
}

