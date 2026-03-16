package io.github.tawdi.jobboard.applications.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSummaryDTO {

    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String city;
}
