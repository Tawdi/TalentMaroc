package io.github.tawdi.jobboard.applications.client;

import io.github.tawdi.jobboard.applications.dto.ApiResponseWrapper;
import io.github.tawdi.jobboard.applications.dto.OfferSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with the Company-Offers Service.
 * Uses Eureka service discovery — the name matches the spring.application.name of the target service.
 */
@FeignClient(name = "COMPANY-OFFERS-SERVICE", path = "/api/v1/offers")
public interface OfferServiceClient {

    @GetMapping("/{offerId}")
    ApiResponseWrapper<OfferSummaryDTO> getOfferById(@PathVariable("offerId") Long offerId);

    @PatchMapping("/{offerId}/increment-applications")
    void incrementApplicationCount(@PathVariable("offerId") Long offerId);
}
