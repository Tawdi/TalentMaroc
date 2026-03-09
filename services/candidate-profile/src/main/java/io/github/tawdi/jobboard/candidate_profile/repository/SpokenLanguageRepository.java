package io.github.tawdi.jobboard.candidate_profile.repository;

import io.github.tawdi.jobboard.candidate_profile.entity.SpokenLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpokenLanguageRepository extends JpaRepository<SpokenLanguage, Long> {

    List<SpokenLanguage> findByProfileId(Long profileId);

    Optional<SpokenLanguage> findByIdAndProfileId(Long id, Long profileId);
}

