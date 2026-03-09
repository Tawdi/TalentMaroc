package io.github.tawdi.jobboard.candidate_profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {

    private Long id;

    @NotBlank(message = "Project title is required")
    private String title;

    private String description;
    private String url;
    private String technologies;
    private LocalDate startDate;
    private LocalDate endDate;
}

