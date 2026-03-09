package io.github.tawdi.jobboard.candidate_profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {

    private String street;

    @NotBlank(message = "City is required")
    private String city;

    private String state;
    private String zipCode;

    @NotBlank(message = "Country is required")
    private String country;
}

