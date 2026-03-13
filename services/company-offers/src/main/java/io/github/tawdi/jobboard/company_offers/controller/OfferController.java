package io.github.tawdi.jobboard.company_offers.controller;

import io.github.tawdi.jobboard.company_offers.dto.*;
import io.github.tawdi.jobboard.company_offers.entity.ContractType;
import io.github.tawdi.jobboard.company_offers.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    // ======================== MY OFFERS (company owner) ========================

    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<OfferResponse>> createOffer(
            @PathVariable String userId,
            @Valid @RequestBody CreateOfferRequest request) {
        OfferResponse response = offerService.createOffer(userId, request);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Offer created successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<Page<OfferResponse>>> getMyOffers(
            @PathVariable String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OfferResponse> offers = offerService.getMyOffers(userId, pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Offers retrieved successfully", offers));
    }

    @PutMapping("/user/{userId}/{offerId}")
    public ResponseEntity<ApiResponseDTO<OfferResponse>> updateOffer(
            @PathVariable String userId,
            @PathVariable Long offerId,
            @Valid @RequestBody UpdateOfferRequest request) {
        OfferResponse response = offerService.updateOffer(userId, offerId, request);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Offer updated successfully", response));
    }

    @DeleteMapping("/user/{userId}/{offerId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteOffer(
            @PathVariable String userId,
            @PathVariable Long offerId) {
        offerService.deleteOffer(userId, offerId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Offer deleted successfully", null));
    }

    // ======================== STATUS TRANSITIONS ========================

    @PatchMapping("/user/{userId}/{offerId}/publish")
    public ResponseEntity<ApiResponseDTO<OfferResponse>> publishOffer(
            @PathVariable String userId,
            @PathVariable Long offerId) {
        OfferResponse response = offerService.publishOffer(userId, offerId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Offer published successfully", response));
    }

    @PatchMapping("/user/{userId}/{offerId}/close")
    public ResponseEntity<ApiResponseDTO<OfferResponse>> closeOffer(
            @PathVariable String userId,
            @PathVariable Long offerId) {
        OfferResponse response = offerService.closeOffer(userId, offerId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Offer closed successfully", response));
    }

    @PatchMapping("/user/{userId}/{offerId}/archive")
    public ResponseEntity<ApiResponseDTO<OfferResponse>> archiveOffer(
            @PathVariable String userId,
            @PathVariable Long offerId) {
        OfferResponse response = offerService.archiveOffer(userId, offerId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Offer archived successfully", response));
    }

    // ======================== PUBLIC ENDPOINTS ========================

    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<OfferResponse>>> getActiveOffers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OfferResponse> offers = offerService.getActiveOffers(pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Offers retrieved successfully", offers));
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<ApiResponseDTO<OfferResponse>> getOfferById(
            @PathVariable Long offerId) {
        OfferResponse response = offerService.getPublicOfferById(offerId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Offer retrieved successfully", response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<Page<OfferResponse>>> searchOffers(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OfferResponse> offers = offerService.searchOffers(keyword, pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Search results retrieved", offers));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponseDTO<Page<OfferResponse>>> filterOffers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) ContractType contractType,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OfferResponse> offers = offerService.filterOffers(
                keyword, location, contractType, pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Filtered results retrieved", offers));
    }

    // ======================== INTER-SERVICE ========================

    /** Called by Applications Service to increment application count */
    @PatchMapping("/{offerId}/increment-applications")
    public ResponseEntity<ApiResponseDTO<Void>> incrementApplications(
            @PathVariable Long offerId) {
        offerService.incrementApplicationCount(offerId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Application count incremented", null));
    }
}

