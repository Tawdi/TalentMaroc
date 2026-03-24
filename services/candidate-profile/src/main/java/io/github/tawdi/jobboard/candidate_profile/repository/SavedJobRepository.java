package io.github.tawdi.jobboard.candidate_profile.repository;

import io.github.tawdi.jobboard.candidate_profile.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    List<SavedJob> findByUserIdOrderBySavedAtDesc(String userId);

    boolean existsByUserIdAndOfferId(String userId, Long offerId);

    Optional<SavedJob> findByUserIdAndOfferId(String userId, Long offerId);
}

