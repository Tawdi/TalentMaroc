package io.github.tawdi.jobboard.applications.dto;

import io.github.tawdi.jobboard.applications.entity.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {

    private Long id;
    private String candidateUserId;
    private Long offerId;
    private String cvUrl;
    private String coverLetter;
    private ApplicationStatus status;
    private String companyNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enriched data from inter-service calls (nullable)
    private OfferSummaryDTO offer;
    private CandidateSummaryDTO candidate;
}
