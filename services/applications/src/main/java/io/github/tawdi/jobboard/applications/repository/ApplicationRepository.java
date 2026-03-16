package io.github.tawdi.jobboard.applications.repository;

import io.github.tawdi.jobboard.applications.entity.Application;
import io.github.tawdi.jobboard.applications.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /** All applications by a candidate (paginated, newest first) */
    Page<Application> findByCandidateUserIdOrderByCreatedAtDesc(String candidateUserId, Pageable pageable);

    /** All applications for a specific offer (paginated) */
    Page<Application> findByOfferIdOrderByCreatedAtDesc(Long offerId, Pageable pageable);

    /** All applications for a specific offer filtered by status */
    Page<Application> findByOfferIdAndStatusOrderByCreatedAtDesc(
            Long offerId, ApplicationStatus status, Pageable pageable);

    /** Check if a candidate has already applied to an offer */
    boolean existsByCandidateUserIdAndOfferId(String candidateUserId, Long offerId);

    /** Count applications for a specific offer */
    long countByOfferId(Long offerId);

    /** Find all applications for a list of offer IDs (for company dashboard) */
    List<Application> findByOfferIdIn(List<Long> offerIds);
}
