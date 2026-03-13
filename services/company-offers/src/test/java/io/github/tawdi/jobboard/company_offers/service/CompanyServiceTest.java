package io.github.tawdi.jobboard.company_offers.service;

import io.github.tawdi.jobboard.company_offers.dto.*;
import io.github.tawdi.jobboard.company_offers.entity.Address;
import io.github.tawdi.jobboard.company_offers.entity.Company;
import io.github.tawdi.jobboard.company_offers.entity.CompanyStatus;
import io.github.tawdi.jobboard.company_offers.exceptions.CompanyAlreadyExistsException;
import io.github.tawdi.jobboard.company_offers.exceptions.ResourceNotFoundException;
import io.github.tawdi.jobboard.company_offers.mapper.CompanyOfferMapper;
import io.github.tawdi.jobboard.company_offers.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompanyService Tests")
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyOfferMapper mapper;

    @InjectMocks
    private CompanyService companyService;

    private Company testCompany;
    private CreateCompanyRequest createRequest;
    private UpdateCompanyRequest updateRequest;
    private ValidateCompanyRequest approveRequest;
    private ValidateCompanyRequest rejectRequest;
    private CompanyResponse companyResponse;
    private CompanySummaryResponse companySummary;
    private AddressDTO addressDTO;

    private final String TEST_USER_ID = "user-001";
    private final Long TEST_COMPANY_ID = 1L;

    @BeforeEach
    void setUp() {
        addressDTO = AddressDTO.builder()
                .street("123 Boulevard Mohammed V")
                .city("Casablanca")
                .state("Casablanca-Settat")
                .zipCode("20000")
                .country("Morocco")
                .build();

        testCompany = Company.builder()
                .id(TEST_COMPANY_ID)
                .userId(TEST_USER_ID)
                .companyName("TechCorp Morocco")
                .sector("Technology")
                .description("Leading tech company")
                .website("https://techcorp.ma")
                .phone("+212 600 000 000")
                .status(CompanyStatus.PENDING)
                .address(Address.builder()
                        .street("123 Boulevard Mohammed V")
                        .city("Casablanca")
                        .state("Casablanca-Settat")
                        .zipCode("20000")
                        .country("Morocco")
                        .build())
                .build();

        createRequest = CreateCompanyRequest.builder()
                .userId(TEST_USER_ID)
                .companyName("TechCorp Morocco")
                .sector("Technology")
                .description("Leading tech company")
                .website("https://techcorp.ma")
                .phone("+212 600 000 000")
                .address(addressDTO)
                .build();

        updateRequest = UpdateCompanyRequest.builder()
                .companyName("TechCorp Updated")
                .sector("Information Technology")
                .description("Updated description")
                .website("https://techcorp-updated.ma")
                .phone("+212 611 111 111")
                .address(addressDTO)
                .build();

        approveRequest = ValidateCompanyRequest.builder()
                .status(CompanyStatus.APPROVED)
                .build();

        rejectRequest = ValidateCompanyRequest.builder()
                .status(CompanyStatus.REJECTED)
                .reason("Incomplete information")
                .build();

        companyResponse = CompanyResponse.builder()
                .id(TEST_COMPANY_ID)
                .userId(TEST_USER_ID)
                .companyName("TechCorp Morocco")
                .sector("Technology")
                .description("Leading tech company")
                .website("https://techcorp.ma")
                .phone("+212 600 000 000")
                .status(CompanyStatus.PENDING)
                .address(addressDTO)
                .offers(Collections.emptyList())
                .build();

        companySummary = CompanySummaryResponse.builder()
                .id(TEST_COMPANY_ID)
                .companyName("TechCorp Morocco")
                .sector("Technology")
                .status(CompanyStatus.APPROVED)
                .address(addressDTO)
                .build();
    }

    // ======================== CREATE ========================

    @Nested
    @DisplayName("Create Company")
    class CreateCompany {

        @Test
        @DisplayName("Should create company successfully when user has no company")
        void shouldCreateCompany_WhenUserHasNoCompany() {
            when(companyRepository.existsByUserId(TEST_USER_ID)).thenReturn(false);
            when(mapper.toEntity(createRequest)).thenReturn(testCompany);
            when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
            when(mapper.toCompanyResponse(testCompany)).thenReturn(companyResponse);

            CompanyResponse result = companyService.createCompany(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(result.getCompanyName()).isEqualTo("TechCorp Morocco");
            verify(companyRepository).existsByUserId(TEST_USER_ID);
            verify(companyRepository).save(any(Company.class));
        }

        @Test
        @DisplayName("Should throw CompanyAlreadyExistsException when user already has a company")
        void shouldThrowException_WhenUserAlreadyHasCompany() {
            when(companyRepository.existsByUserId(TEST_USER_ID)).thenReturn(true);

            assertThatThrownBy(() -> companyService.createCompany(createRequest))
                    .isInstanceOf(CompanyAlreadyExistsException.class)
                    .hasMessageContaining("A company profile already exists for user: " + TEST_USER_ID);

            verify(companyRepository).existsByUserId(TEST_USER_ID);
            verify(companyRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create company with minimal fields (no optional fields)")
        void shouldCreateCompany_WithMinimalFields() {
            CreateCompanyRequest minimalRequest = CreateCompanyRequest.builder()
                    .userId(TEST_USER_ID)
                    .companyName("Startup XYZ")
                    .sector("E-commerce")
                    .build();

            Company minimalCompany = Company.builder()
                    .id(2L)
                    .userId(TEST_USER_ID)
                    .companyName("Startup XYZ")
                    .sector("E-commerce")
                    .status(CompanyStatus.PENDING)
                    .build();

            CompanyResponse minimalResponse = CompanyResponse.builder()
                    .id(2L)
                    .userId(TEST_USER_ID)
                    .companyName("Startup XYZ")
                    .sector("E-commerce")
                    .status(CompanyStatus.PENDING)
                    .offers(Collections.emptyList())
                    .build();

            when(companyRepository.existsByUserId(TEST_USER_ID)).thenReturn(false);
            when(mapper.toEntity(minimalRequest)).thenReturn(minimalCompany);
            when(companyRepository.save(any(Company.class))).thenReturn(minimalCompany);
            when(mapper.toCompanyResponse(minimalCompany)).thenReturn(minimalResponse);

            CompanyResponse result = companyService.createCompany(minimalRequest);

            assertThat(result).isNotNull();
            assertThat(result.getCompanyName()).isEqualTo("Startup XYZ");
            assertThat(result.getDescription()).isNull();
            assertThat(result.getWebsite()).isNull();
        }
    }

    // ======================== READ ========================

    @Nested
    @DisplayName("Read Company")
    class ReadCompany {

        @Test
        @DisplayName("Should get company by user ID successfully")
        void shouldGetCompanyByUserId() {
            when(companyRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testCompany));
            when(mapper.toCompanyResponse(testCompany)).thenReturn(companyResponse);

            CompanyResponse result = companyService.getCompanyByUserId(TEST_USER_ID);

            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
            verify(companyRepository).findByUserId(TEST_USER_ID);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when company not found by user ID")
        void shouldThrowException_WhenCompanyNotFoundByUserId() {
            when(companyRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.getCompanyByUserId(TEST_USER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Company not found for user: " + TEST_USER_ID);
        }

        @Test
        @DisplayName("Should get company by ID successfully")
        void shouldGetCompanyById() {
            when(companyRepository.findById(TEST_COMPANY_ID)).thenReturn(Optional.of(testCompany));
            when(mapper.toCompanyResponse(testCompany)).thenReturn(companyResponse);

            CompanyResponse result = companyService.getCompanyById(TEST_COMPANY_ID);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(TEST_COMPANY_ID);
            verify(companyRepository).findById(TEST_COMPANY_ID);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when company not found by ID")
        void shouldThrowException_WhenCompanyNotFoundById() {
            when(companyRepository.findById(TEST_COMPANY_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.getCompanyById(TEST_COMPANY_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Company not found with id: " + TEST_COMPANY_ID);
        }

        @Test
        @DisplayName("Should return paginated approved companies")
        void shouldReturnApprovedCompanies() {
            Pageable pageable = PageRequest.of(0, 20);
            Company approvedCompany = Company.builder()
                    .id(TEST_COMPANY_ID)
                    .companyName("TechCorp Morocco")
                    .sector("Technology")
                    .status(CompanyStatus.APPROVED)
                    .build();
            Page<Company> page = new PageImpl<>(List.of(approvedCompany));

            when(companyRepository.findByStatus(CompanyStatus.APPROVED, pageable)).thenReturn(page);
            when(mapper.toCompanySummary(approvedCompany)).thenReturn(companySummary);

            Page<CompanySummaryResponse> result = companyService.getApprovedCompanies(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getCompanyName()).isEqualTo("TechCorp Morocco");
            verify(companyRepository).findByStatus(CompanyStatus.APPROVED, pageable);
        }
    }

    // ======================== UPDATE ========================

    @Nested
    @DisplayName("Update Company")
    class UpdateCompany {

        @Test
        @DisplayName("Should update company successfully")
        void shouldUpdateCompany() {
            Company updatedCompany = Company.builder()
                    .id(TEST_COMPANY_ID)
                    .userId(TEST_USER_ID)
                    .companyName("TechCorp Updated")
                    .sector("Information Technology")
                    .status(CompanyStatus.PENDING)
                    .build();

            CompanyResponse updatedResponse = CompanyResponse.builder()
                    .id(TEST_COMPANY_ID)
                    .userId(TEST_USER_ID)
                    .companyName("TechCorp Updated")
                    .sector("Information Technology")
                    .status(CompanyStatus.PENDING)
                    .offers(Collections.emptyList())
                    .build();

            when(companyRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testCompany));
            doNothing().when(mapper).updateEntity(testCompany, updateRequest);
            when(companyRepository.save(testCompany)).thenReturn(updatedCompany);
            when(mapper.toCompanyResponse(updatedCompany)).thenReturn(updatedResponse);

            CompanyResponse result = companyService.updateCompany(TEST_USER_ID, updateRequest);

            assertThat(result).isNotNull();
            assertThat(result.getCompanyName()).isEqualTo("TechCorp Updated");
            verify(mapper).updateEntity(testCompany, updateRequest);
            verify(companyRepository).save(testCompany);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existent company")
        void shouldThrowException_WhenUpdatingNonExistentCompany() {
            when(companyRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.updateCompany(TEST_USER_ID, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Company not found for user: " + TEST_USER_ID);

            verify(companyRepository, never()).save(any());
        }
    }

    // ======================== DELETE ========================

    @Nested
    @DisplayName("Delete Company")
    class DeleteCompany {

        @Test
        @DisplayName("Should delete company successfully")
        void shouldDeleteCompany() {
            when(companyRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testCompany));

            companyService.deleteCompany(TEST_USER_ID);

            verify(companyRepository).delete(testCompany);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existent company")
        void shouldThrowException_WhenDeletingNonExistentCompany() {
            when(companyRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.deleteCompany(TEST_USER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Company not found for user: " + TEST_USER_ID);

            verify(companyRepository, never()).delete(any());
        }
    }

    // ======================== ADMIN VALIDATION ========================

    @Nested
    @DisplayName("Admin Validation")
    class AdminValidation {

        @Test
        @DisplayName("Should return list of pending companies")
        void shouldReturnPendingCompanies() {
            List<Company> pendingCompanies = List.of(testCompany);
            when(companyRepository.findByStatus(CompanyStatus.PENDING)).thenReturn(pendingCompanies);
            when(mapper.toCompanySummary(testCompany)).thenReturn(companySummary);

            List<CompanySummaryResponse> result = companyService.getPendingCompanies();

            assertThat(result).hasSize(1);
            verify(companyRepository).findByStatus(CompanyStatus.PENDING);
        }

        @Test
        @DisplayName("Should return empty list when no pending companies")
        void shouldReturnEmptyList_WhenNoPendingCompanies() {
            when(companyRepository.findByStatus(CompanyStatus.PENDING)).thenReturn(Collections.emptyList());

            List<CompanySummaryResponse> result = companyService.getPendingCompanies();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should approve a PENDING company")
        void shouldApproveCompany() {
            when(companyRepository.findById(TEST_COMPANY_ID)).thenReturn(Optional.of(testCompany));
            when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

            CompanyResponse approvedResponse = CompanyResponse.builder()
                    .id(TEST_COMPANY_ID)
                    .status(CompanyStatus.APPROVED)
                    .build();
            when(mapper.toCompanyResponse(any(Company.class))).thenReturn(approvedResponse);

            CompanyResponse result = companyService.validateCompany(TEST_COMPANY_ID, approveRequest);

            assertThat(result.getStatus()).isEqualTo(CompanyStatus.APPROVED);
            verify(companyRepository).save(testCompany);
            assertThat(testCompany.getStatus()).isEqualTo(CompanyStatus.APPROVED);
            assertThat(testCompany.getValidatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should reject a PENDING company")
        void shouldRejectCompany() {
            when(companyRepository.findById(TEST_COMPANY_ID)).thenReturn(Optional.of(testCompany));
            when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

            CompanyResponse rejectedResponse = CompanyResponse.builder()
                    .id(TEST_COMPANY_ID)
                    .status(CompanyStatus.REJECTED)
                    .build();
            when(mapper.toCompanyResponse(any(Company.class))).thenReturn(rejectedResponse);

            CompanyResponse result = companyService.validateCompany(TEST_COMPANY_ID, rejectRequest);

            assertThat(result.getStatus()).isEqualTo(CompanyStatus.REJECTED);
            assertThat(testCompany.getStatus()).isEqualTo(CompanyStatus.REJECTED);
            assertThat(testCompany.getValidatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should throw IllegalStateException when validating already validated company")
        void shouldThrowException_WhenCompanyAlreadyValidated() {
            testCompany.setStatus(CompanyStatus.APPROVED);
            when(companyRepository.findById(TEST_COMPANY_ID)).thenReturn(Optional.of(testCompany));

            assertThatThrownBy(() -> companyService.validateCompany(TEST_COMPANY_ID, approveRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Company has already been validated with status: APPROVED");

            verify(companyRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when validation status is PENDING")
        void shouldThrowException_WhenStatusIsPending() {
            ValidateCompanyRequest pendingRequest = ValidateCompanyRequest.builder()
                    .status(CompanyStatus.PENDING)
                    .build();

            when(companyRepository.findById(TEST_COMPANY_ID)).thenReturn(Optional.of(testCompany));

            assertThatThrownBy(() -> companyService.validateCompany(TEST_COMPANY_ID, pendingRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Validation status must be APPROVED or REJECTED");

            verify(companyRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when company not found for validation")
        void shouldThrowException_WhenCompanyNotFoundForValidation() {
            when(companyRepository.findById(TEST_COMPANY_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.validateCompany(TEST_COMPANY_ID, approveRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Company not found with id: " + TEST_COMPANY_ID);
        }
    }

    // ======================== INTERNAL HELPERS ========================

    @Nested
    @DisplayName("Internal Helpers")
    class InternalHelpers {

        @Test
        @DisplayName("Should find entity by user ID")
        void shouldFindEntityByUserId() {
            when(companyRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testCompany));

            Company result = companyService.findEntityByUserId(TEST_USER_ID);

            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when entity not found by user ID")
        void shouldThrowException_WhenEntityNotFoundByUserId() {
            when(companyRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.findEntityByUserId(TEST_USER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Company not found for user: " + TEST_USER_ID);
        }
    }
}

