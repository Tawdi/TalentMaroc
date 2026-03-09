package io.github.tawdi.jobboard.candidate_profile.dto;

import io.github.tawdi.jobboard.candidate_profile.entity.LanguageProficiency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpokenLanguageDTO {

    private Long id;

    @NotBlank(message = "Language name is required")
    private String language;

    @NotNull(message = "Proficiency level is required")
    private LanguageProficiency proficiency;
}

