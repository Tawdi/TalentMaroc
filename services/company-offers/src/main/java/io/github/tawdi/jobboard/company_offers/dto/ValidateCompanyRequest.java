package io.github.tawdi.jobboard.company_offers.dto;

import io.github.tawdi.jobboard.company_offers.entity.CompanyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateCompanyRequest {

    @NotNull(message = "Status is required (APPROVED or REJECTED)")
    private CompanyStatus status;

    private String reason;
}

