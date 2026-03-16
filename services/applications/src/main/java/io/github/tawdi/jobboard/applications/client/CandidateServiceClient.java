package io.github.tawdi.jobboard.applications.client;

import io.github.tawdi.jobboard.applications.dto.ApiResponseWrapper;
import io.github.tawdi.jobboard.applications.dto.CandidateSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with the Candidate-Profile Service.
 * Uses Eureka service discovery — the name matches the spring.application.name of the target service.
 */
@FeignClient(name = "CANDIDATE-PROFILE-SERVICE", path = "/api/v1/profiles")
public interface CandidateServiceClient {

    @GetMapping("/user/{userId}")
    ApiResponseWrapper<CandidateSummaryDTO> getCandidateByUserId(@PathVariable("userId") String userId);
}
