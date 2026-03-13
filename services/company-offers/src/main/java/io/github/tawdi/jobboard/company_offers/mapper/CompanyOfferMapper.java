package io.github.tawdi.jobboard.company_offers.mapper;

import io.github.tawdi.jobboard.company_offers.dto.*;
import io.github.tawdi.jobboard.company_offers.entity.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompanyOfferMapper {

    // ======================== COMPANY ========================

    public Company toEntity(CreateCompanyRequest req) {
        Company company = Company.builder()
                .userId(req.getUserId())
                .companyName(req.getCompanyName())
                .sector(req.getSector())
                .description(req.getDescription())
                .website(req.getWebsite())
                .phone(req.getPhone())
                .status(CompanyStatus.PENDING)
                .build();

        if (req.getAddress() != null) {
            company.setAddress(toAddress(req.getAddress()));
        }
        return company;
    }

    public void updateEntity(Company company, UpdateCompanyRequest req) {
        if (req.getCompanyName() != null) company.setCompanyName(req.getCompanyName());
        if (req.getSector() != null) company.setSector(req.getSector());
        if (req.getDescription() != null) company.setDescription(req.getDescription());
        if (req.getWebsite() != null) company.setWebsite(req.getWebsite());
        if (req.getPhone() != null) company.setPhone(req.getPhone());
        if (req.getAddress() != null) company.setAddress(toAddress(req.getAddress()));
    }

    public CompanyResponse toCompanyResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .userId(company.getUserId())
                .companyName(company.getCompanyName())
                .sector(company.getSector())
                .description(company.getDescription())
                .website(company.getWebsite())
                .phone(company.getPhone())
                .logoUrl(company.getLogoUrl())
                .status(company.getStatus())
                .validatedAt(company.getValidatedAt())
                .address(company.getAddress() != null ? toAddressDTO(company.getAddress()) : null)
                .offers(toOfferResponses(company.getOffers()))
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }

    public CompanySummaryResponse toCompanySummary(Company company) {
        return CompanySummaryResponse.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .sector(company.getSector())
                .logoUrl(company.getLogoUrl())
                .status(company.getStatus())
                .address(company.getAddress() != null ? toAddressDTO(company.getAddress()) : null)
                .createdAt(company.getCreatedAt())
                .build();
    }

    // ======================== OFFER ========================

    public Offer toEntity(CreateOfferRequest req) {
        return Offer.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .contractType(req.getContractType())
                .location(req.getLocation())
                .salaryRange(req.getSalaryRange())
                .requirements(req.getRequirements())
                .benefits(req.getBenefits())
                .status(OfferStatus.DRAFT)
                .expiresAt(req.getExpiresAt())
                .build();
    }

    public void updateEntity(Offer offer, UpdateOfferRequest req) {
        if (req.getTitle() != null) offer.setTitle(req.getTitle());
        if (req.getDescription() != null) offer.setDescription(req.getDescription());
        if (req.getContractType() != null) offer.setContractType(req.getContractType());
        if (req.getLocation() != null) offer.setLocation(req.getLocation());
        if (req.getSalaryRange() != null) offer.setSalaryRange(req.getSalaryRange());
        if (req.getRequirements() != null) offer.setRequirements(req.getRequirements());
        if (req.getBenefits() != null) offer.setBenefits(req.getBenefits());
        if (req.getStatus() != null) offer.setStatus(req.getStatus());
        if (req.getExpiresAt() != null) offer.setExpiresAt(req.getExpiresAt());
    }

    public OfferResponse toOfferResponse(Offer offer) {
        return OfferResponse.builder()
                .id(offer.getId())
                .title(offer.getTitle())
                .description(offer.getDescription())
                .contractType(offer.getContractType())
                .location(offer.getLocation())
                .salaryRange(offer.getSalaryRange())
                .requirements(offer.getRequirements())
                .benefits(offer.getBenefits())
                .status(offer.getStatus())
                .expiresAt(offer.getExpiresAt())
                .viewsCount(offer.getViewsCount())
                .applicationsCount(offer.getApplicationsCount())
                .company(toCompanySummary(offer.getCompany()))
                .createdAt(offer.getCreatedAt())
                .updatedAt(offer.getUpdatedAt())
                .build();
    }

    public List<OfferResponse> toOfferResponses(List<Offer> offers) {
        if (offers == null) return Collections.emptyList();
        return offers.stream()
                .map(this::toOfferResponse)
                .collect(Collectors.toList());
    }

    // ======================== ADDRESS ========================

    public Address toAddress(AddressDTO dto) {
        return Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode())
                .country(dto.getCountry())
                .build();
    }

    public AddressDTO toAddressDTO(Address addr) {
        return AddressDTO.builder()
                .street(addr.getStreet())
                .city(addr.getCity())
                .state(addr.getState())
                .zipCode(addr.getZipCode())
                .country(addr.getCountry())
                .build();
    }
}

