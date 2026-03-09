package io.github.tawdi.jobboard.candidate_profile.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    private String firstName;
    private String lastName;
    private String headline;
    private String about;
    private String phone;
    private LocalDate dateOfBirth;

    @Valid
    private AddressDTO address;
}

