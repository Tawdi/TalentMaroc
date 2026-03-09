package io.github.tawdi.jobboard.candidate_profile.repository;

import io.github.tawdi.jobboard.candidate_profile.entity.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, Long> {

    Optional<CandidateProfile> findByUserId(String userId);

    boolean existsByUserId(String userId);
}

