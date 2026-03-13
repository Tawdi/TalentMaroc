package io.github.tawdi.jobboard.company_offers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCompanyRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must be less than 255 characters")
    private String companyName;

    @NotBlank(message = "Sector is required")
    @Size(max = 100, message = "Sector must be less than 100 characters")
    private String sector;

    @Size(max = 2000, message = "Description must be less than 2000 characters")
    private String description;

    private String website;
    private String phone;
    private AddressDTO address;
}

