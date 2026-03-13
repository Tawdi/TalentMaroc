package io.github.tawdi.jobboard.company_offers.service;

import io.github.tawdi.jobboard.company_offers.dto.*;
import io.github.tawdi.jobboard.company_offers.entity.*;
import io.github.tawdi.jobboard.company_offers.exceptions.CompanyNotApprovedException;
import io.github.tawdi.jobboard.company_offers.exceptions.ResourceNotFoundException;
import io.github.tawdi.jobboard.company_offers.mapper.CompanyOfferMapper;
import io.github.tawdi.jobboard.company_offers.repository.OfferRepository;
import io.github.tawdi.jobboard.company_offers.repository.OfferSpecifications;
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
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OfferService Tests")
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private CompanyService companyService;

    @Mock
    private CompanyOfferMapper mapper;

    @InjectMocks
    private OfferService offerService;

    private Company approvedCompany;
    private Company pendingCompany;
    private Offer draftOffer;
    private Offer activeOffer;
    private Offer closedOffer;
    private CreateOfferRequest createRequest;
    private UpdateOfferRequest updateRequest;
    private OfferResponse offerResponse;
    private CompanySummaryResponse companySummary;

    private final String TEST_USER_ID = "user-001";
    private final String OTHER_USER_ID = "user-999";
    private final Long TEST_OFFER_ID = 1L;
    private final Long TEST_COMPANY_ID = 1L;

    @BeforeEach
    void setUp() {
        approvedCompany = Company.builder()
                .id(TEST_COMPANY_ID)
                .userId(TEST_USER_ID)
                .companyName("TechCorp Morocco")
                .sector("Technology")
                .status(CompanyStatus.APPROVED)
                .build();

        pendingCompany = Company.builder()
                .id(2L)
                .userId("user-pending")
                .companyName("Pending Corp")
                .sector("Finance")
                .status(CompanyStatus.PENDING)
                .build();

        draftOffer = Offer.builder()
                .id(TEST_OFFER_ID)
                .title("Full-Stack Developer")
                .description("Looking for a developer with Spring Boot and Angular experience.")
                .contractType(ContractType.CDI)
                .location("Casablanca, Morocco")
                .salaryRange("15000-25000 MAD")
                .requirements("3+ years Java experience")
                .benefits("Remote work, health insurance")
                .status(OfferStatus.DRAFT)
                .expiresAt(LocalDate.of(2026, 6, 1))
                .viewsCount(0L)
                .applicationsCount(0L)
                .company(approvedCompany)
                .build();

        activeOffer = Offer.builder()
                .id(2L)
                .title("Backend Developer")
                .description("Java backend position")
                .contractType(ContractType.CDI)
                .location("Rabat, Morocco")
                .status(OfferStatus.ACTIVE)
                .viewsCount(5L)
                .applicationsCount(2L)
                .company(approvedCompany)
                .build();

        closedOffer = Offer.builder()
                .id(3L)
                .title("Old Position")
                .description("Closed position")
                .contractType(ContractType.CDD)
                .status(OfferStatus.CLOSED)
                .viewsCount(50L)
                .applicationsCount(10L)
                .company(approvedCompany)
                .build();

        createRequest = CreateOfferRequest.builder()
                .title("Full-Stack Developer")
                .description("Looking for a developer with Spring Boot and Angular experience.")
                .contractType(ContractType.CDI)
                .location("Casablanca, Morocco")
                .salaryRange("15000-25000 MAD")
                .requirements("3+ years Java experience")
                .benefits("Remote work, health insurance")
                .expiresAt(LocalDate.of(2026, 6, 1))
                .build();

        updateRequest = UpdateOfferRequest.builder()
                .title("Senior Full-Stack Developer")
                .description("Updated description")
                .salaryRange("20000-35000 MAD")
                .build();

        companySummary = CompanySummaryResponse.builder()
                .id(TEST_COMPANY_ID)
                .companyName("TechCorp Morocco")
                .sector("Technology")
                .status(CompanyStatus.APPROVED)
                .build();

        offerResponse = OfferResponse.builder()
                .id(TEST_OFFER_ID)
                .title("Full-Stack Developer")
                .description("Looking for a developer with Spring Boot and Angular experience.")
                .contractType(ContractType.CDI)
                .location("Casablanca, Morocco")
                .salaryRange("15000-25000 MAD")
                .status(OfferStatus.DRAFT)
                .viewsCount(0L)
                .applicationsCount(0L)
                .company(companySummary)
                .build();
    }

    // ======================== CREATE ========================

    @Nested
    @DisplayName("Create Offer")
    class CreateOffer {

        @Test
        @DisplayName("Should create offer successfully for an approved company")
        void shouldCreateOffer_WhenCompanyIsApproved() {
            when(companyService.findEntityByUserId(TEST_USER_ID)).thenReturn(approvedCompany);
            when(mapper.toEntity(createRequest)).thenReturn(draftOffer);
            when(offerRepository.save(any(Offer.class))).thenReturn(draftOffer);
            when(mapper.toOfferResponse(draftOffer)).thenReturn(offerResponse);

            OfferResponse result = offerService.createOffer(TEST_USER_ID, createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Full-Stack Developer");
            assertThat(result.getStatus()).isEqualTo(OfferStatus.DRAFT);
            assertThat(result.getContractType()).isEqualTo(ContractType.CDI);
            verify(companyService).findEntityByUserId(TEST_USER_ID);
            verify(offerRepository).save(any(Offer.class));
        }

        @Test
        @DisplayName("Should throw CompanyNotApprovedException when company is PENDING")
        void shouldThrowException_WhenCompanyIsPending() {
            when(companyService.findEntityByUserId("user-pending")).thenReturn(pendingCompany);

            assertThatThrownBy(() -> offerService.createOffer("user-pending", createRequest))
                    .isInstanceOf(CompanyNotApprovedException.class)
                    .hasMessageContaining("Your company must be approved before publishing offers");

            verify(offerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when company does not exist")
        void shouldThrowException_WhenCompanyDoesNotExist() {
            when(companyService.findEntityByUserId("unknown-user"))
                    .thenThrow(new ResourceNotFoundException("Company not found for user: unknown-user"));

            assertThatThrownBy(() -> offerService.createOffer("unknown-user", createRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ======================== READ ========================

    @Nested
    @DisplayName("Read Offers")
    class ReadOffers {

        @Test
        @DisplayName("Should get offer by ID")
        void shouldGetOfferById() {
            when(offerRepository.findById(TEST_OFFER_ID)).thenReturn(Optional.of(draftOffer));
            when(mapper.toOfferResponse(draftOffer)).thenReturn(offerResponse);

            OfferResponse result = offerService.getOfferById(TEST_OFFER_ID);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(TEST_OFFER_ID);
            verify(offerRepository).findById(TEST_OFFER_ID);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when offer not found by ID")
        void shouldThrowException_WhenOfferNotFoundById() {
            when(offerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> offerService.getOfferById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Offer not found with id: 999");
        }

        @Test
        @DisplayName("Should get public offer and increment view count")
        void shouldGetPublicOffer_AndIncrementViews() {
            when(offerRepository.findById(2L)).thenReturn(Optional.of(activeOffer));
            when(offerRepository.save(activeOffer)).thenReturn(activeOffer);

            OfferResponse activeResponse = OfferResponse.builder()
                    .id(2L)
                    .title("Backend Developer")
                    .status(OfferStatus.ACTIVE)
                    .viewsCount(6L)
                    .company(companySummary)
                    .build();
            when(mapper.toOfferResponse(activeOffer)).thenReturn(activeResponse);

            OfferResponse result = offerService.getPublicOfferById(2L);

            assertThat(result).isNotNull();
            assertThat(activeOffer.getViewsCount()).isEqualTo(6L); // incremented from 5
            verify(offerRepository).save(activeOffer);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when getting public draft offer")
        void shouldThrowException_WhenGettingPublicDraftOffer() {
            when(offerRepository.findById(TEST_OFFER_ID)).thenReturn(Optional.of(draftOffer));

            assertThatThrownBy(() -> offerService.getPublicOfferById(TEST_OFFER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Offer not found with id: " + TEST_OFFER_ID);
        }

        @Test
        @DisplayName("Should get my offers paginated")
        void shouldGetMyOffers() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Offer> page = new PageImpl<>(List.of(draftOffer, activeOffer));
            when(offerRepository.findByCompanyUserId(TEST_USER_ID, pageable)).thenReturn(page);
            when(mapper.toOfferResponse(any(Offer.class))).thenReturn(offerResponse);

            Page<OfferResponse> result = offerService.getMyOffers(TEST_USER_ID, pageable);

            assertThat(result.getContent()).hasSize(2);
            verify(offerRepository).findByCompanyUserId(TEST_USER_ID, pageable);
        }

        @Test
        @DisplayName("Should get active offers paginated")
        void shouldGetActiveOffers() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Offer> page = new PageImpl<>(List.of(activeOffer));
            when(offerRepository.findAllActiveOffers(pageable)).thenReturn(page);

            OfferResponse activeResponse = OfferResponse.builder()
                    .id(2L).title("Backend Developer").status(OfferStatus.ACTIVE).company(companySummary).build();
            when(mapper.toOfferResponse(activeOffer)).thenReturn(activeResponse);

            Page<OfferResponse> result = offerService.getActiveOffers(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getStatus()).isEqualTo(OfferStatus.ACTIVE);
        }

        @Test
        @DisplayName("Should search offers by keyword")
        void shouldSearchOffers() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Offer> page = new PageImpl<>(List.of(activeOffer));
            when(offerRepository.searchActiveOffers("java", pageable)).thenReturn(page);

            OfferResponse activeResponse = OfferResponse.builder()
                    .id(2L).title("Backend Developer").status(OfferStatus.ACTIVE).company(companySummary).build();
            when(mapper.toOfferResponse(activeOffer)).thenReturn(activeResponse);

            Page<OfferResponse> result = offerService.searchOffers("java", pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(offerRepository).searchActiveOffers("java", pageable);
        }

        @Test
        @DisplayName("Should filter offers with multiple criteria")
        @SuppressWarnings("unchecked")
        void shouldFilterOffers() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Offer> page = new PageImpl<>(List.of(activeOffer));
            when(offerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            OfferResponse activeResponse = OfferResponse.builder()
                    .id(2L).title("Backend Developer").status(OfferStatus.ACTIVE)
                    .contractType(ContractType.CDI).location("Rabat, Morocco").company(companySummary).build();
            when(mapper.toOfferResponse(activeOffer)).thenReturn(activeResponse);

            Page<OfferResponse> result = offerService.filterOffers("java", "Rabat", ContractType.CDI, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(offerRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Should filter offers with null parameters")
        @SuppressWarnings("unchecked")
        void shouldFilterOffers_WithNullParams() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Offer> page = new PageImpl<>(List.of(activeOffer));
            when(offerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
            when(mapper.toOfferResponse(any(Offer.class))).thenReturn(offerResponse);

            Page<OfferResponse> result = offerService.filterOffers(null, null, null, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    // ======================== STATUS TRANSITIONS ========================

    @Nested
    @DisplayName("Status Transitions")
    class StatusTransitions {

        @Test
        @DisplayName("Should publish a DRAFT offer (DRAFT → ACTIVE)")
        void shouldPublishDraftOffer() {
            when(offerRepository.findById(TEST_OFFER_ID)).thenReturn(Optional.of(draftOffer));
            when(offerRepository.save(draftOffer)).thenReturn(draftOffer);

            OfferResponse publishedResponse = OfferResponse.builder()
                    .id(TEST_OFFER_ID).title("Full-Stack Developer").status(OfferStatus.ACTIVE).company(companySummary).build();
            when(mapper.toOfferResponse(draftOffer)).thenReturn(publishedResponse);

            OfferResponse result = offerService.publishOffer(TEST_USER_ID, TEST_OFFER_ID);

            assertThat(result.getStatus()).isEqualTo(OfferStatus.ACTIVE);
            assertThat(draftOffer.getStatus()).isEqualTo(OfferStatus.ACTIVE);
            verify(offerRepository).save(draftOffer);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when publishing non-DRAFT offer")
        void shouldThrowException_WhenPublishingNonDraftOffer() {
            when(offerRepository.findById(2L)).thenReturn(Optional.of(activeOffer));

            assertThatThrownBy(() -> offerService.publishOffer(TEST_USER_ID, 2L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only DRAFT offers can be published. Current status: ACTIVE");

            verify(offerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should close an ACTIVE offer (ACTIVE → CLOSED)")
        void shouldCloseActiveOffer() {
            when(offerRepository.findById(2L)).thenReturn(Optional.of(activeOffer));
            when(offerRepository.save(activeOffer)).thenReturn(activeOffer);

            OfferResponse closedResponse = OfferResponse.builder()
                    .id(2L).title("Backend Developer").status(OfferStatus.CLOSED).company(companySummary).build();
            when(mapper.toOfferResponse(activeOffer)).thenReturn(closedResponse);

            OfferResponse result = offerService.closeOffer(TEST_USER_ID, 2L);

            assertThat(result.getStatus()).isEqualTo(OfferStatus.CLOSED);
            assertThat(activeOffer.getStatus()).isEqualTo(OfferStatus.CLOSED);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when closing non-ACTIVE offer")
        void shouldThrowException_WhenClosingNonActiveOffer() {
            when(offerRepository.findById(TEST_OFFER_ID)).thenReturn(Optional.of(draftOffer));

            assertThatThrownBy(() -> offerService.closeOffer(TEST_USER_ID, TEST_OFFER_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only ACTIVE offers can be closed. Current status: DRAFT");
        }

        @Test
        @DisplayName("Should archive a CLOSED offer (CLOSED → ARCHIVED)")
        void shouldArchiveClosedOffer() {
            when(offerRepository.findById(3L)).thenReturn(Optional.of(closedOffer));
            when(offerRepository.save(closedOffer)).thenReturn(closedOffer);

            OfferResponse archivedResponse = OfferResponse.builder()
                    .id(3L).title("Old Position").status(OfferStatus.ARCHIVED).company(companySummary).build();
            when(mapper.toOfferResponse(closedOffer)).thenReturn(archivedResponse);

            OfferResponse result = offerService.archiveOffer(TEST_USER_ID, 3L);

            assertThat(result.getStatus()).isEqualTo(OfferStatus.ARCHIVED);
            assertThat(closedOffer.getStatus()).isEqualTo(OfferStatus.ARCHIVED);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when archiving non-CLOSED offer")
        void shouldThrowException_WhenArchivingNonClosedOffer() {
            when(offerRepository.findById(2L)).thenReturn(Optional.of(activeOffer));

            assertThatThrownBy(() -> offerService.archiveOffer(TEST_USER_ID, 2L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only CLOSED offers can be archived. Current status: ACTIVE");
        }

        @Test
        @DisplayName("Should increment application count")
        void shouldIncrementApplicationCount() {
            when(offerRepository.findById(2L)).thenReturn(Optional.of(activeOffer));
            when(offerRepository.save(activeOffer)).thenReturn(activeOffer);

            offerService.incrementApplicationCount(2L);

            assertThat(activeOffer.getApplicationsCount()).isEqualTo(3L); // incremented from 2
            verify(offerRepository).save(activeOffer);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when incrementing non-existent offer")
        void shouldThrowException_WhenIncrementingNonExistentOffer() {
            when(offerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> offerService.incrementApplicationCount(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Offer not found with id: 999");
        }
    }

    // ======================== UPDATE ========================

    @Nested
    @DisplayName("Update Offer")
    class UpdateOffer {

        @Test
        @DisplayName("Should update offer successfully")
        void shouldUpdateOffer() {
            when(offerRepository.findById(TEST_OFFER_ID)).thenReturn(Optional.of(draftOffer));
            doNothing().when(mapper).updateEntity(draftOffer, updateRequest);
            when(offerRepository.save(draftOffer)).thenReturn(draftOffer);

            OfferResponse updatedResponse = OfferResponse.builder()
                    .id(TEST_OFFER_ID).title("Senior Full-Stack Developer").status(OfferStatus.DRAFT).company(companySummary).build();
            when(mapper.toOfferResponse(draftOffer)).thenReturn(updatedResponse);

            OfferResponse result = offerService.updateOffer(TEST_USER_ID, TEST_OFFER_ID, updateRequest);

            assertThat(result.getTitle()).isEqualTo("Senior Full-Stack Developer");
            verify(mapper).updateEntity(draftOffer, updateRequest);
            verify(offerRepository).save(draftOffer);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existent offer")
        void shouldThrowException_WhenUpdatingNonExistentOffer() {
            when(offerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> offerService.updateOffer(TEST_USER_ID, 999L, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Offer not found with id: 999");
        }

        @Test
        @DisplayName("Should throw IllegalStateException when user is not the offer owner")
        void shouldThrowException_WhenUserIsNotOfferOwner() {
            when(offerRepository.findById(TEST_OFFER_ID)).thenReturn(Optional.of(draftOffer));

            assertThatThrownBy(() -> offerService.updateOffer(OTHER_USER_ID, TEST_OFFER_ID, updateRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("You do not have permission to modify this offer");
        }

        @Test
        @DisplayName("Should update offer with valid status transition (DRAFT → ACTIVE)")
        void shouldUpdateOffer_WithValidStatusTransition() {
            UpdateOfferRequest statusUpdate = UpdateOfferRequest.builder()
                    .status(OfferStatus.ACTIVE)
                    .build();

            when(offerRepository.findById(TEST_OFFER_ID)).thenReturn(Optional.of(draftOffer));
            doNothing().when(mapper).updateEntity(draftOffer, statusUpdate);
            when(offerRepository.save(draftOffer)).thenReturn(draftOffer);
            when(mapper.toOfferResponse(draftOffer)).thenReturn(offerResponse);

            OfferResponse result = offerService.updateOffer(TEST_USER_ID, TEST_OFFER_ID, statusUpdate);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should throw IllegalStateException for invalid status transition (DRAFT → ARCHIVED)")
        void shouldThrowException_ForInvalidStatusTransition() {
            UpdateOfferRequest badStatusUpdate = UpdateOfferRequest.builder()
                    .status(OfferStatus.ARCHIVED)
                    .build();

            when(offerRepository.findById(TEST_OFFER_ID)).thenReturn(Optional.of(draftOffer));

            assertThatThrownBy(() -> offerService.updateOffer(TEST_USER_ID, TEST_OFFER_ID, badStatusUpdate))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Invalid status transition: DRAFT → ARCHIVED");
        }

        @Test
        @DisplayName("Should allow null status in update (no status change)")
        void shouldAllowNullStatus_InUpdate() {
            UpdateOfferRequest noStatusUpdate = UpdateOfferRequest.builder()
                    .title("Updated Title Only")
                    .build();

            when(offerRepository.findById(TEST_OFFER_ID)).thenReturn(Optional.of(draftOffer));
            doNothing().when(mapper).updateEntity(draftOffer, noStatusUpdate);
            when(offerRepository.save(draftOffer)).thenReturn(draftOffer);
            when(mapper.toOfferResponse(draftOffer)).thenReturn(offerResponse);

            OfferResponse result = offerService.updateOffer(TEST_USER_ID, TEST_OFFER_ID, noStatusUpdate);

            assertThat(result).isNotNull();
        }
    }

    // ======================== DELETE ========================

    @Nested
    @DisplayName("Delete Offer")
    class DeleteOffer {

        @Test
        @DisplayName("Should delete offer successfully")
        void shouldDeleteOffer() {
            when(offerRepository.findById(TEST_OFFER_ID)).thenReturn(Optional.of(draftOffer));

            offerService.deleteOffer(TEST_USER_ID, TEST_OFFER_ID);

            verify(offerRepository).delete(draftOffer);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existent offer")
        void shouldThrowException_WhenDeletingNonExistentOffer() {
            when(offerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> offerService.deleteOffer(TEST_USER_ID, 999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when deleting offer owned by another user")
        void shouldThrowException_WhenDeletingOtherUsersOffer() {
            when(offerRepository.findById(TEST_OFFER_ID)).thenReturn(Optional.of(draftOffer));

            assertThatThrownBy(() -> offerService.deleteOffer(OTHER_USER_ID, TEST_OFFER_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("You do not have permission to modify this offer");

            verify(offerRepository, never()).delete(any(Offer.class));
        }
    }

    // ======================== EDGE CASES ========================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle offer with all contract types")
        void shouldHandleAllContractTypes() {
            for (ContractType type : ContractType.values()) {
                CreateOfferRequest req = CreateOfferRequest.builder()
                        .title("Position - " + type)
                        .description("Description for " + type)
                        .contractType(type)
                        .build();

                Offer offer = Offer.builder()
                        .id(100L)
                        .title("Position - " + type)
                        .description("Description for " + type)
                        .contractType(type)
                        .status(OfferStatus.DRAFT)
                        .company(approvedCompany)
                        .build();

                when(companyService.findEntityByUserId(TEST_USER_ID)).thenReturn(approvedCompany);
                when(mapper.toEntity(req)).thenReturn(offer);
                when(offerRepository.save(any(Offer.class))).thenReturn(offer);
                when(mapper.toOfferResponse(offer)).thenReturn(
                        OfferResponse.builder().id(100L).title("Position - " + type)
                                .contractType(type).status(OfferStatus.DRAFT).company(companySummary).build());

                OfferResponse result = offerService.createOffer(TEST_USER_ID, req);

                assertThat(result.getContractType()).isEqualTo(type);
            }
        }

        @Test
        @DisplayName("Should handle complete offer lifecycle: DRAFT → ACTIVE → CLOSED → ARCHIVED")
        void shouldHandleCompleteLifecycle() {
            Offer lifecycle = Offer.builder()
                    .id(TEST_OFFER_ID)
                    .title("Lifecycle Test")
                    .description("Testing lifecycle")
                    .contractType(ContractType.CDI)
                    .status(OfferStatus.DRAFT)
                    .viewsCount(0L)
                    .applicationsCount(0L)
                    .company(approvedCompany)
                    .build();

            // DRAFT → ACTIVE
            when(offerRepository.findById(TEST_OFFER_ID)).thenReturn(Optional.of(lifecycle));
            when(offerRepository.save(lifecycle)).thenReturn(lifecycle);
            when(mapper.toOfferResponse(lifecycle)).thenReturn(
                    OfferResponse.builder().id(TEST_OFFER_ID).status(OfferStatus.ACTIVE).company(companySummary).build());

            OfferResponse published = offerService.publishOffer(TEST_USER_ID, TEST_OFFER_ID);
            assertThat(published.getStatus()).isEqualTo(OfferStatus.ACTIVE);
            assertThat(lifecycle.getStatus()).isEqualTo(OfferStatus.ACTIVE);

            // ACTIVE → CLOSED
            when(mapper.toOfferResponse(lifecycle)).thenReturn(
                    OfferResponse.builder().id(TEST_OFFER_ID).status(OfferStatus.CLOSED).company(companySummary).build());

            OfferResponse closed = offerService.closeOffer(TEST_USER_ID, TEST_OFFER_ID);
            assertThat(closed.getStatus()).isEqualTo(OfferStatus.CLOSED);
            assertThat(lifecycle.getStatus()).isEqualTo(OfferStatus.CLOSED);

            // CLOSED → ARCHIVED
            when(mapper.toOfferResponse(lifecycle)).thenReturn(
                    OfferResponse.builder().id(TEST_OFFER_ID).status(OfferStatus.ARCHIVED).company(companySummary).build());

            OfferResponse archived = offerService.archiveOffer(TEST_USER_ID, TEST_OFFER_ID);
            assertThat(archived.getStatus()).isEqualTo(OfferStatus.ARCHIVED);
            assertThat(lifecycle.getStatus()).isEqualTo(OfferStatus.ARCHIVED);
        }

        @Test
        @DisplayName("Should handle update with all valid status transitions")
        void shouldHandleAllValidStatusTransitions() {
            // DRAFT → ACTIVE is valid
            Offer draft = Offer.builder().id(1L).status(OfferStatus.DRAFT).company(approvedCompany)
                    .title("T").description("D").contractType(ContractType.CDI).build();
            when(offerRepository.findById(1L)).thenReturn(Optional.of(draft));
            doNothing().when(mapper).updateEntity(any(Offer.class), any(UpdateOfferRequest.class));
            when(offerRepository.save(any())).thenReturn(draft);
            when(mapper.toOfferResponse(any())).thenReturn(offerResponse);

            UpdateOfferRequest toActive = UpdateOfferRequest.builder().status(OfferStatus.ACTIVE).build();
            assertThatCode(() -> offerService.updateOffer(TEST_USER_ID, 1L, toActive)).doesNotThrowAnyException();

            // ACTIVE → CLOSED is valid
            draft.setStatus(OfferStatus.ACTIVE);
            UpdateOfferRequest toClosed = UpdateOfferRequest.builder().status(OfferStatus.CLOSED).build();
            assertThatCode(() -> offerService.updateOffer(TEST_USER_ID, 1L, toClosed)).doesNotThrowAnyException();

            // CLOSED → ARCHIVED is valid
            draft.setStatus(OfferStatus.CLOSED);
            UpdateOfferRequest toArchived = UpdateOfferRequest.builder().status(OfferStatus.ARCHIVED).build();
            assertThatCode(() -> offerService.updateOffer(TEST_USER_ID, 1L, toArchived)).doesNotThrowAnyException();

            // CLOSED → ACTIVE (reopen) is valid
            draft.setStatus(OfferStatus.CLOSED);
            UpdateOfferRequest reopen = UpdateOfferRequest.builder().status(OfferStatus.ACTIVE).build();
            assertThatCode(() -> offerService.updateOffer(TEST_USER_ID, 1L, reopen)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should reject invalid status transitions via update")
        void shouldRejectInvalidStatusTransitions() {
            Offer archived = Offer.builder().id(1L).status(OfferStatus.ARCHIVED).company(approvedCompany)
                    .title("T").description("D").contractType(ContractType.CDI).build();
            when(offerRepository.findById(1L)).thenReturn(Optional.of(archived));

            // ARCHIVED → anything is invalid
            UpdateOfferRequest toActive = UpdateOfferRequest.builder().status(OfferStatus.ACTIVE).build();
            assertThatThrownBy(() -> offerService.updateOffer(TEST_USER_ID, 1L, toActive))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Invalid status transition: ARCHIVED → ACTIVE");

            UpdateOfferRequest toDraft = UpdateOfferRequest.builder().status(OfferStatus.DRAFT).build();
            assertThatThrownBy(() -> offerService.updateOffer(TEST_USER_ID, 1L, toDraft))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Invalid status transition: ARCHIVED → DRAFT");
        }
    }
}

