package io.github.tawdi.jobboard.applications.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferSummaryDTO {

    private Long id;
    private String title;
    private String contractType;
    private String location;
    private String companyName;
}
