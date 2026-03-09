package io.github.tawdi.jobboard.candidate_profile.dto;

import io.github.tawdi.jobboard.candidate_profile.entity.SkillLevel;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillDTO {

    private Long id;

    @NotBlank(message = "Skill name is required")
    private String name;

    private SkillLevel level;
}

