package io.github.tawdi.jobboard.candidate_profile.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class SavedJobDTO {
    Long id;
    Long offerId;
    Instant savedAt;
}

