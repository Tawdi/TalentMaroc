package io.github.tawdi.jobboard.company_offers.controller;

import io.github.tawdi.jobboard.company_offers.dto.*;
import io.github.tawdi.jobboard.company_offers.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // ======================== COMPANY CRUD ========================

    @PostMapping
    public ResponseEntity<ApiResponseDTO<CompanyResponse>> createCompany(
            @Valid @RequestBody CreateCompanyRequest request) {
        CompanyResponse response = companyService.createCompany(request);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Company created successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<CompanyResponse>> getCompanyByUserId(
            @PathVariable String userId) {
        CompanyResponse response = companyService.getCompanyByUserId(userId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Company retrieved successfully", response));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponseDTO<CompanyResponse>> getCompanyById(
            @PathVariable Long companyId) {
        CompanyResponse response = companyService.getCompanyById(companyId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Company retrieved successfully", response));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<CompanyResponse>> updateCompany(
            @PathVariable String userId,
            @Valid @RequestBody UpdateCompanyRequest request) {
        CompanyResponse response = companyService.updateCompany(userId, request);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Company updated successfully", response));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteCompany(
            @PathVariable String userId) {
        companyService.deleteCompany(userId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Company deleted successfully", null));
    }

    // ======================== PUBLIC LISTING ========================

    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<CompanySummaryResponse>>> getApprovedCompanies(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CompanySummaryResponse> companies = companyService.getApprovedCompanies(pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Companies retrieved successfully", companies));
    }
}

