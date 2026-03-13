package io.github.tawdi.jobboard.company_offers.dto;

import io.github.tawdi.jobboard.company_offers.entity.CompanyStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Lightweight company summary for public listing (no offers embedded).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanySummaryResponse {
    private Long id;
    private String companyName;
    private String sector;
    private String logoUrl;
    private CompanyStatus status;
    private AddressDTO address;
    private LocalDateTime createdAt;
}

