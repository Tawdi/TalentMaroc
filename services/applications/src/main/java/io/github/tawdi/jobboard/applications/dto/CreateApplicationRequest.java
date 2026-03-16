package io.github.tawdi.jobboard.applications.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateApplicationRequest {

    @NotNull(message = "Offer ID is required")
    private Long offerId;

    @NotBlank(message = "CV URL is required")
    private String cvUrl;

    @Size(max = 5000, message = "Cover letter must be less than 5000 characters")
    private String coverLetter;
}
