package io.github.tawdi.jobboard.applications.dto;

import io.github.tawdi.jobboard.applications.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateApplicationStatusRequest {

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    @Size(max = 2000, message = "Company note must be less than 2000 characters")
    private String companyNote;
}
