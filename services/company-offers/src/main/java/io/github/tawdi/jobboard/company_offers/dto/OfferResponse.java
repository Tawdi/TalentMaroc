package io.github.tawdi.jobboard.company_offers.dto;

import io.github.tawdi.jobboard.company_offers.entity.ContractType;
import io.github.tawdi.jobboard.company_offers.entity.OfferStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferResponse {
    private Long id;
    private String title;
    private String description;
    private ContractType contractType;
    private String location;
    private String salaryRange;
    private String requirements;
    private String benefits;
    private OfferStatus status;
    private LocalDate expiresAt;
    private Long viewsCount;
    private Long applicationsCount;
    private CompanySummaryResponse company;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

