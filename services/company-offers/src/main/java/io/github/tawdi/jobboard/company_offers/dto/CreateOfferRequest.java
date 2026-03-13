package io.github.tawdi.jobboard.company_offers.dto;

import io.github.tawdi.jobboard.company_offers.entity.ContractType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOfferRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must be less than 5000 characters")
    private String description;

    @NotNull(message = "Contract type is required")
    private ContractType contractType;

    private String location;
    private String salaryRange;

    @Size(max = 3000, message = "Requirements must be less than 3000 characters")
    private String requirements;

    @Size(max = 2000, message = "Benefits must be less than 2000 characters")
    private String benefits;

    private LocalDate expiresAt;
}

