package io.github.tawdi.jobboard.applications.service;

import io.github.tawdi.jobboard.applications.client.CandidateServiceClient;
import io.github.tawdi.jobboard.applications.client.OfferServiceClient;
import io.github.tawdi.jobboard.applications.dto.*;
import io.github.tawdi.jobboard.applications.entity.Application;
import io.github.tawdi.jobboard.applications.entity.ApplicationStatus;
import io.github.tawdi.jobboard.applications.exceptions.DuplicateApplicationException;
import io.github.tawdi.jobboard.applications.exceptions.InvalidStatusTransitionException;
import io.github.tawdi.jobboard.applications.exceptions.ResourceNotFoundException;
import io.github.tawdi.jobboard.applications.mapper.ApplicationMapper;
import io.github.tawdi.jobboard.applications.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper mapper;
    private final OfferServiceClient offerServiceClient;
    private final CandidateServiceClient candidateServiceClient;

    // ======================== CANDIDATE OPERATIONS ========================

    /**
     * Submit a new application.
     * Validates the offer exists, checks for duplicates, saves, and increments counter.
     */
    public ApplicationResponse apply(String candidateUserId, CreateApplicationRequest request) {
        // 1. Verify offer exists via inter-service call
        OfferSummaryDTO offer = fetchOffer(request.getOfferId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offer not found with id: " + request.getOfferId()));

        // 2. Check for duplicate application
        if (applicationRepository.existsByCandidateUserIdAndOfferId(candidateUserId, request.getOfferId())) {
            throw new DuplicateApplicationException(
                    "You have already applied to this offer");
        }

        // 3. Create and save the application
        Application application = mapper.toEntity(request, candidateUserId);
        Application saved = applicationRepository.save(application);

        // 4. Notify company-offers service to increment application count (fire-and-forget)
        try {
            offerServiceClient.incrementApplicationCount(request.getOfferId());
        } catch (Exception e) {
            log.warn("Failed to increment application count for offer {}", request.getOfferId(), e);
        }

        log.info("Application submitted: candidate '{}' applied to offer '{}' (offerId: {})",
                candidateUserId, offer.getTitle(), request.getOfferId());

        // 5. Build enriched response
        CandidateSummaryDTO candidate = fetchCandidate(candidateUserId).orElse(null);

        return mapper.toResponse(saved, offer, candidate);
    }

    /**
     * Check if a candidate has already applied to a given offer.
     */
    public boolean hasApplied(String candidateUserId, Long offerId) {
        return applicationRepository.existsByCandidateUserIdAndOfferId(candidateUserId, offerId);
    }

    /**
     * Get paginated application history for a candidate.
     */
    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getMyApplications(String candidateUserId, Pageable pageable) {
        return applicationRepository
                .findByCandidateUserIdOrderByCreatedAtDesc(candidateUserId, pageable)
                .map(app -> enrichResponse(app, true, false));
    }

    /**
     * Get a single application detail for a candidate.
     */
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationForCandidate(String candidateUserId, Long applicationId) {
        Application application = findApplicationById(applicationId);

        if (!application.getCandidateUserId().equals(candidateUserId)) {
            throw new IllegalStateException("You do not have permission to view this application");
        }

        return enrichResponse(application, true, false);
    }

    /**
     * Candidate withdraws an application.
     */
    public ApplicationResponse withdrawApplication(String candidateUserId, Long applicationId) {
        Application application = findApplicationById(applicationId);

        if (!application.getCandidateUserId().equals(candidateUserId)) {
            throw new IllegalStateException("You do not have permission to withdraw this application");
        }

        if (!application.canBeWithdrawn()) {
            throw new InvalidStatusTransitionException(
                    String.format("Cannot withdraw application with status: %s. " +
                                    "Only RECEIVED or REVIEWING applications can be withdrawn.",
                            application.getStatus()));
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        Application saved = applicationRepository.save(application);

        log.info("Application withdrawn: id={} by candidate '{}'", applicationId, candidateUserId);

        return enrichResponse(saved, true, false);
    }

    // ======================== COMPANY OPERATIONS ========================

    /**
     * Get paginated applications for a specific offer (company view).
     * Optionally filtered by status.
     */
    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getApplicationsForOffer(
            Long offerId, ApplicationStatus status, Pageable pageable) {

        Page<Application> applications;
        if (status != null) {
            applications = applicationRepository
                    .findByOfferIdAndStatusOrderByCreatedAtDesc(offerId, status, pageable);
        } else {
            applications = applicationRepository
                    .findByOfferIdOrderByCreatedAtDesc(offerId, pageable);
        }

        return applications.map(app -> enrichResponse(app, false, true));
    }

    /**
     * Get a single application detail for a company.
     * Includes candidate info for enriched view.
     */
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationForCompany(Long applicationId) {
        Application application = findApplicationById(applicationId);
        return enrichResponse(application, true, true);
    }

    /**
     * Company updates an application's status (and optionally adds a note).
     */
    public ApplicationResponse updateApplicationStatus(
            Long applicationId, UpdateApplicationStatusRequest request) {

        Application application = findApplicationById(applicationId);

        validateStatusTransition(application.getStatus(), request.getStatus());

        application.setStatus(request.getStatus());

        if (request.getCompanyNote() != null) {
            application.setCompanyNote(request.getCompanyNote());
        }

        Application saved = applicationRepository.save(application);

        log.info("Application status updated: id={}, {} → {}",
                applicationId, application.getStatus(), request.getStatus());

        return enrichResponse(saved, true, true);
    }

    // ======================== INTERNAL HELPERS ========================

    private Application findApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + applicationId));
    }

    /**
     * Enrich an application response with offer and/or candidate data from other services.
     */
    private ApplicationResponse enrichResponse(
            Application application, boolean includeOffer, boolean includeCandidate) {

        OfferSummaryDTO offer = null;
        CandidateSummaryDTO candidate = null;

        if (includeOffer) {
            offer = fetchOffer(application.getOfferId()).orElse(null);
        }
        if (includeCandidate) {
            candidate = fetchCandidate(application.getCandidateUserId()).orElse(null);
        }

        return mapper.toResponse(application, offer, candidate);
    }

    /**
     * Safely fetch offer summary via Feign, returning Optional.
     */
    private java.util.Optional<OfferSummaryDTO> fetchOffer(Long offerId) {
        try {
            var response = offerServiceClient.getOfferById(offerId);
            return response != null && response.getData() != null
                    ? java.util.Optional.of(response.getData())
                    : java.util.Optional.empty();
        } catch (Exception e) {
            log.warn("Failed to fetch offer {} from company-offers service: {}", offerId, e.getMessage());
            return java.util.Optional.empty();
        }
    }

    /**
     * Safely fetch candidate summary via Feign, returning Optional.
     */
    private java.util.Optional<CandidateSummaryDTO> fetchCandidate(String userId) {
        try {
            var response = candidateServiceClient.getCandidateByUserId(userId);
            return response != null && response.getData() != null
                    ? java.util.Optional.of(response.getData())
                    : java.util.Optional.empty();
        } catch (Exception e) {
            log.warn("Failed to fetch candidate {} from candidate-profile service: {}", userId, e.getMessage());
            return java.util.Optional.empty();
        }
    }

    /**
     * Validate that a status transition is allowed.
     * Valid transitions:
     *   RECEIVED → REVIEWING
     *   REVIEWING → ACCEPTED | REJECTED
     *   RECEIVED → REJECTED (fast reject)
     */
    private void validateStatusTransition(ApplicationStatus current, ApplicationStatus requested) {
        boolean valid = switch (current) {
            case RECEIVED -> requested == ApplicationStatus.REVIEWING
                    || requested == ApplicationStatus.REJECTED;
            case REVIEWING -> requested == ApplicationStatus.ACCEPTED
                    || requested == ApplicationStatus.REJECTED;
            case ACCEPTED, REJECTED, WITHDRAWN -> false;
        };

        if (!valid) {
            throw new InvalidStatusTransitionException(
                    String.format("Invalid status transition: %s → %s", current, requested));
        }
    }
}
