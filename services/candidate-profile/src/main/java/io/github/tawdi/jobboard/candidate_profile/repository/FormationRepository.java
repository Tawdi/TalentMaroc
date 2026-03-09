package io.github.tawdi.jobboard.candidate_profile.repository;

import io.github.tawdi.jobboard.candidate_profile.entity.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {

    List<Formation> findByProfileIdOrderByStartDateDesc(Long profileId);

    Optional<Formation> findByIdAndProfileId(Long id, Long profileId);
}

