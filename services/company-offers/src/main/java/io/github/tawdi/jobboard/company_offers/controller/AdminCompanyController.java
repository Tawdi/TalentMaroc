package io.github.tawdi.jobboard.company_offers.controller;

import io.github.tawdi.jobboard.company_offers.dto.*;
import io.github.tawdi.jobboard.company_offers.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/companies")
@RequiredArgsConstructor
public class AdminCompanyController {

    private final CompanyService companyService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponseDTO<List<CompanySummaryResponse>>> getPendingCompanies() {
        List<CompanySummaryResponse> companies = companyService.getPendingCompanies();
        return ResponseEntity.ok(
                ApiResponseDTO.success("Pending companies retrieved", companies));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<CompanySummaryResponse>>> getAllCompanies(@PageableDefault(size = 20) Pageable pageable) {
        Page<CompanySummaryResponse> companies = companyService.getAllCompanies(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success("Companies retrieved successfully", companies));
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteCompany(@PathVariable Long companyId) {
        companyService.deleteCompanyById(companyId);
        return ResponseEntity.ok(ApiResponseDTO.success("Company deleted successfully", null));
    }

    @PutMapping("/{companyId}/validate")
    public ResponseEntity<ApiResponseDTO<CompanyResponse>> validateCompany(
            @PathVariable Long companyId,
            @Valid @RequestBody ValidateCompanyRequest request) {
        CompanyResponse response = companyService.validateCompany(companyId, request);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Company validated successfully", response));
    }
}
