package io.github.tawdi.jobboard.company_offers.service;

import io.github.tawdi.jobboard.company_offers.dto.*;
import io.github.tawdi.jobboard.company_offers.entity.Company;
import io.github.tawdi.jobboard.company_offers.entity.ContractType;
import io.github.tawdi.jobboard.company_offers.entity.Offer;
import io.github.tawdi.jobboard.company_offers.entity.OfferStatus;
import io.github.tawdi.jobboard.company_offers.exceptions.CompanyNotApprovedException;
import io.github.tawdi.jobboard.company_offers.exceptions.ResourceNotFoundException;
import io.github.tawdi.jobboard.company_offers.mapper.CompanyOfferMapper;
import io.github.tawdi.jobboard.company_offers.repository.OfferRepository;
import io.github.tawdi.jobboard.company_offers.repository.OfferSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OfferService {

    private final OfferRepository offerRepository;
    private final CompanyService companyService;
    private final CompanyOfferMapper mapper;

    // ======================== CREATE ========================

    public OfferResponse createOffer(String userId, CreateOfferRequest request) {
        Company company = companyService.findEntityByUserId(userId);

        if (!company.isApproved()) {
            throw new CompanyNotApprovedException(
                    "Your company must be approved before publishing offers. Current status: "
                            + company.getStatus());
        }

        Offer offer = mapper.toEntity(request);
        company.addOffer(offer);
        Offer saved = offerRepository.save(offer);

        log.info("Offer created: '{}' by company '{}' (userId: {})",
                saved.getTitle(), company.getCompanyName(), userId);
        return mapper.toOfferResponse(saved);
    }

    // ======================== READ ========================

    @Transactional(readOnly = true)
    public OfferResponse getOfferById(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offer not found with id: " + offerId));
        return mapper.toOfferResponse(offer);
    }

    /** Get a public offer and increment view count */
    public OfferResponse getPublicOfferById(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offer not found with id: " + offerId));

        if (offer.getStatus() != OfferStatus.ACTIVE || !offer.getCompany().isApproved()) {
            throw new ResourceNotFoundException("Offer not found with id: " + offerId);
        }

        offer.incrementViews();
        offerRepository.save(offer);

        return mapper.toOfferResponse(offer);
    }

    /** Get all offers for a company owner */
    @Transactional(readOnly = true)
    public Page<OfferResponse> getMyOffers(String userId, Pageable pageable) {
        return offerRepository.findByCompanyUserId(userId, pageable)
                .map(mapper::toOfferResponse);
    }

    /** Public listing: all active offers from approved companies */
    @Transactional(readOnly = true)
    public Page<OfferResponse> getActiveOffers(Pageable pageable) {
        return offerRepository.findAllActiveOffers(pageable)
                .map(mapper::toOfferResponse);
    }

    /** Search active offers by keyword */
    @Transactional(readOnly = true)
    public Page<OfferResponse> searchOffers(String keyword, Pageable pageable) {
        return offerRepository.searchActiveOffers(keyword, pageable)
                .map(mapper::toOfferResponse);
    }

    /** Filter offers with multiple criteria */
    @Transactional(readOnly = true)
    public Page<OfferResponse> filterOffers(
            String keyword, String location, ContractType contractType, Pageable pageable) {
        return offerRepository.findAll(
                OfferSpecifications.activeOffersWithFilters(keyword, location, contractType),
                pageable
        ).map(mapper::toOfferResponse);
    }

    // ======================== UPDATE ========================

    public OfferResponse updateOffer(String userId, Long offerId, UpdateOfferRequest request) {
        Offer offer = findOfferOwnedByUser(userId, offerId);

        validateStatusTransition(offer.getStatus(), request.getStatus());
        mapper.updateEntity(offer, request);
        Offer updated = offerRepository.save(offer);

        log.info("Offer updated: '{}' (id: {}) by userId: {}", updated.getTitle(), offerId, userId);
        return mapper.toOfferResponse(updated);
    }

    /** Publish a draft offer (DRAFT → ACTIVE) */
    public OfferResponse publishOffer(String userId, Long offerId) {
        Offer offer = findOfferOwnedByUser(userId, offerId);

        if (offer.getStatus() != OfferStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only DRAFT offers can be published. Current status: " + offer.getStatus());
        }

        offer.setStatus(OfferStatus.ACTIVE);
        Offer saved = offerRepository.save(offer);

        log.info("Offer published: '{}' (id: {})", saved.getTitle(), offerId);
        return mapper.toOfferResponse(saved);
    }

    /** Close an active offer (ACTIVE → CLOSED) */
    public OfferResponse closeOffer(String userId, Long offerId) {
        Offer offer = findOfferOwnedByUser(userId, offerId);

        if (offer.getStatus() != OfferStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Only ACTIVE offers can be closed. Current status: " + offer.getStatus());
        }

        offer.setStatus(OfferStatus.CLOSED);
        Offer saved = offerRepository.save(offer);

        log.info("Offer closed: '{}' (id: {})", saved.getTitle(), offerId);
        return mapper.toOfferResponse(saved);
    }

    /** Archive a closed offer (CLOSED → ARCHIVED) */
    public OfferResponse archiveOffer(String userId, Long offerId) {
        Offer offer = findOfferOwnedByUser(userId, offerId);

        if (offer.getStatus() != OfferStatus.CLOSED) {
            throw new IllegalStateException(
                    "Only CLOSED offers can be archived. Current status: " + offer.getStatus());
        }

        offer.setStatus(OfferStatus.ARCHIVED);
        Offer saved = offerRepository.save(offer);

        log.info("Offer archived: '{}' (id: {})", saved.getTitle(), offerId);
        return mapper.toOfferResponse(saved);
    }

    /** Increment application count (called by Applications Service via REST) */
    public void incrementApplicationCount(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offer not found with id: " + offerId));
        offer.incrementApplications();
        offerRepository.save(offer);
    }

    // ======================== DELETE ========================

    public void deleteOffer(String userId, Long offerId) {
        Offer offer = findOfferOwnedByUser(userId, offerId);
        offerRepository.delete(offer);
        log.info("Offer deleted: '{}' (id: {}) by userId: {}", offer.getTitle(), offerId, userId);
    }

    // ======================== INTERNAL HELPERS ========================

    private Offer findOfferOwnedByUser(String userId, Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offer not found with id: " + offerId));

        if (!offer.getCompany().getUserId().equals(userId)) {
            throw new IllegalStateException(
                    "You do not have permission to modify this offer");
        }
        return offer;
    }

    private void validateStatusTransition(OfferStatus current, OfferStatus requested) {
        if (requested == null) return; // no status change requested

        boolean valid = switch (current) {
            case DRAFT -> requested == OfferStatus.ACTIVE;
            case ACTIVE -> requested == OfferStatus.CLOSED;
            case CLOSED -> requested == OfferStatus.ARCHIVED || requested == OfferStatus.ACTIVE;
            case ARCHIVED -> false;
        };

        if (!valid && current != requested) {
            throw new IllegalStateException(
                    String.format("Invalid status transition: %s → %s", current, requested));
        }
    }
}



