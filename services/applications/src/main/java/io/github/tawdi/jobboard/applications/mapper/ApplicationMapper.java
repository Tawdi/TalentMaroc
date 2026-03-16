package io.github.tawdi.jobboard.applications.mapper;

import io.github.tawdi.jobboard.applications.dto.*;
import io.github.tawdi.jobboard.applications.entity.Application;
import io.github.tawdi.jobboard.applications.entity.ApplicationStatus;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {

    /**
     * Convert a creation request to an Application entity.
     */
    public Application toEntity(CreateApplicationRequest request, String candidateUserId) {
        return Application.builder()
                .candidateUserId(candidateUserId)
                .offerId(request.getOfferId())
                .cvUrl(request.getCvUrl())
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.RECEIVED)
                .build();
    }

    /**
     * Convert an Application entity to a response DTO.
     * Offer and candidate info are enriched separately.
     */
    public ApplicationResponse toResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .candidateUserId(application.getCandidateUserId())
                .offerId(application.getOfferId())
                .cvUrl(application.getCvUrl())
                .coverLetter(application.getCoverLetter())
                .status(application.getStatus())
                .companyNote(application.getCompanyNote())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    /**
     * Convert an Application entity to a response DTO with enriched data.
     */
    public ApplicationResponse toResponse(
            Application application,
            OfferSummaryDTO offer,
            CandidateSummaryDTO candidate) {

        ApplicationResponse response = toResponse(application);
        response.setOffer(offer);
        response.setCandidate(candidate);
        return response;
    }
}
