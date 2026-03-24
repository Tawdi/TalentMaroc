package io.github.tawdi.jobboard.company_offers.service;

import io.github.tawdi.jobboard.company_offers.dto.*;
import io.github.tawdi.jobboard.company_offers.entity.Company;
import io.github.tawdi.jobboard.company_offers.entity.CompanyStatus;
import io.github.tawdi.jobboard.company_offers.exceptions.CompanyAlreadyExistsException;
import io.github.tawdi.jobboard.company_offers.exceptions.ResourceNotFoundException;
import io.github.tawdi.jobboard.company_offers.mapper.CompanyOfferMapper;
import io.github.tawdi.jobboard.company_offers.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyOfferMapper mapper;

    // ======================== CREATE ========================

    public CompanyResponse createCompany(CreateCompanyRequest request) {
        if (companyRepository.existsByUserId(request.getUserId())) {
            throw new CompanyAlreadyExistsException(
                    "A company profile already exists for user: " + request.getUserId());
        }

        Company company = mapper.toEntity(request);
        Company saved = companyRepository.save(company);

        log.info("Company created: {} (userId: {})", saved.getCompanyName(), saved.getUserId());
        return mapper.toCompanyResponse(saved);
    }

    // ======================== READ ========================

    @Transactional(readOnly = true)
    public CompanyResponse getCompanyByUserId(String userId) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found for user: " + userId));
        return mapper.toCompanyResponse(company);
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found with id: " + companyId));
        return mapper.toCompanyResponse(company);
    }

    @Transactional(readOnly = true)
    public Page<CompanySummaryResponse> getApprovedCompanies(Pageable pageable) {
        return companyRepository
                .findByStatus(CompanyStatus.APPROVED, pageable)
                .map(mapper::toCompanySummary);
    }

    @Transactional(readOnly = true)
    public Page<CompanySummaryResponse> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable)
                .map(mapper::toCompanySummary);
    }

    // ======================== UPDATE ========================

    public CompanyResponse updateCompany(String userId, UpdateCompanyRequest request) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found for user: " + userId));

        mapper.updateEntity(company, request);
        Company updated = companyRepository.save(company);

        log.info("Company updated: {} (userId: {})", updated.getCompanyName(), userId);
        return mapper.toCompanyResponse(updated);
    }

    // ======================== DELETE ========================

    public void deleteCompany(String userId) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found for user: " + userId));

        companyRepository.delete(company);
        log.info("Company deleted: {} (userId: {})", company.getCompanyName(), userId);
    }

    public void deleteCompanyById(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with id: " + companyId);
        }
        companyRepository.deleteById(companyId);
        log.info("Company deleted with id: {}", companyId);
    }

    // ======================== ADMIN: VALIDATION ========================

    @Transactional(readOnly = true)
    public List<CompanySummaryResponse> getPendingCompanies() {
        return companyRepository.findByStatus(CompanyStatus.PENDING)
                .stream()
                .map(mapper::toCompanySummary)
                .collect(Collectors.toList());
    }

    public CompanyResponse validateCompany(Long companyId, ValidateCompanyRequest request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found with id: " + companyId));

        if (company.getStatus() != CompanyStatus.PENDING) {
            throw new IllegalStateException(
                    "Company has already been validated with status: " + company.getStatus());
        }

        CompanyStatus newStatus = request.getStatus();
        if (newStatus != CompanyStatus.APPROVED && newStatus != CompanyStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Validation status must be APPROVED or REJECTED");
        }

        company.setStatus(newStatus);
        company.setValidatedAt(LocalDateTime.now());
        Company saved = companyRepository.save(company);

        log.info("Company {} (id: {}) validated with status: {}",
                saved.getCompanyName(), companyId, newStatus);
        return mapper.toCompanyResponse(saved);
    }

    // ======================== INTERNAL HELPERS ========================

    public Company findEntityByUserId(String userId) {
        return companyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found for user: " + userId));
    }
}
