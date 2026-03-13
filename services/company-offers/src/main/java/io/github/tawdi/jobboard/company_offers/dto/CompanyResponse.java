package io.github.tawdi.jobboard.company_offers.dto;

import io.github.tawdi.jobboard.company_offers.entity.CompanyStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {
    private Long id;
    private String userId;
    private String companyName;
    private String sector;
    private String description;
    private String website;
    private String phone;
    private String logoUrl;
    private CompanyStatus status;
    private LocalDateTime validatedAt;
    private AddressDTO address;
    private List<OfferResponse> offers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

