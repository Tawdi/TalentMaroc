package io.github.tawdi.jobboard.applications.service;

import io.github.tawdi.jobboard.applications.client.CandidateServiceClient;
import io.github.tawdi.jobboard.applications.client.OfferServiceClient;
import io.github.tawdi.jobboard.applications.dto.*;
import io.github.tawdi.jobboard.applications.entity.Application;
import io.github.tawdi.jobboard.applications.mapper.ApplicationMapper;
import io.github.tawdi.jobboard.applications.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    private static final String CANDIDATE_ID = "candidate-123";
    private static final Long OFFER_ID = 42L;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationMapper mapper;

    @Mock
    private OfferServiceClient offerServiceClient;

    @Mock
    private CandidateServiceClient candidateServiceClient;

    @InjectMocks
    private ApplicationService applicationService;

    private CreateApplicationRequest createRequest;
    private OfferSummaryDTO offerSummary;
    private CandidateSummaryDTO candidateSummary;
    private Application newApplication;
    private Application savedApplication;

    @BeforeEach
    void setUp() {
        createRequest = CreateApplicationRequest.builder()
                .offerId(OFFER_ID)
                .cvUrl("https://cdn.example/cv.pdf")
                .coverLetter("Motivated developer")
                .build();

        offerSummary = OfferSummaryDTO.builder()
                .id(OFFER_ID)
                .title("Senior Engineer")
                .contractType("CDI")
                .location("Remote")
                .companyName("Tech Corp")
                .build();

        candidateSummary = CandidateSummaryDTO.builder()
                .id(7L)
                .userId(CANDIDATE_ID)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .city("Casablanca")
                .build();

        newApplication = Application.builder()
                .candidateUserId(CANDIDATE_ID)
                .offerId(OFFER_ID)
                .cvUrl(createRequest.getCvUrl())
                .coverLetter(createRequest.getCoverLetter())
                .build();

        savedApplication = Application.builder()
                .id(100L)
                .candidateUserId(CANDIDATE_ID)
                .offerId(OFFER_ID)
                .cvUrl(createRequest.getCvUrl())
                .coverLetter(createRequest.getCoverLetter())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void apply_shouldIncrementOfferApplicationsCount() {
        when(applicationRepository.existsByCandidateUserIdAndOfferId(CANDIDATE_ID, OFFER_ID)).thenReturn(false);
        when(offerServiceClient.getOfferById(OFFER_ID))
                .thenReturn(new ApiResponseWrapper<>("SUCCESS", "", offerSummary));
        when(candidateServiceClient.getCandidateByUserId(CANDIDATE_ID))
                .thenReturn(new ApiResponseWrapper<>("SUCCESS", "", candidateSummary));
        when(mapper.toEntity(createRequest, CANDIDATE_ID)).thenReturn(newApplication);
        when(applicationRepository.save(newApplication)).thenReturn(savedApplication);
        ApplicationResponse expectedResponse = ApplicationResponse.builder()
                .id(savedApplication.getId())
                .offer(offerSummary)
                .candidate(candidateSummary)
                .build();
        when(mapper.toResponse(savedApplication, offerSummary, candidateSummary)).thenReturn(expectedResponse);

        ApplicationResponse response = applicationService.apply(CANDIDATE_ID, createRequest);

        assertThat(response).isEqualTo(expectedResponse);
        verify(offerServiceClient).incrementApplicationCount(OFFER_ID);
    }

    @Test
    void apply_shouldContinueWhenIncrementFails() {
        when(applicationRepository.existsByCandidateUserIdAndOfferId(CANDIDATE_ID, OFFER_ID)).thenReturn(false);
        when(offerServiceClient.getOfferById(OFFER_ID))
                .thenReturn(new ApiResponseWrapper<>("SUCCESS", "", offerSummary));
        when(candidateServiceClient.getCandidateByUserId(CANDIDATE_ID))
                .thenReturn(new ApiResponseWrapper<>("SUCCESS", "", candidateSummary));
        when(mapper.toEntity(createRequest, CANDIDATE_ID)).thenReturn(newApplication);
        when(applicationRepository.save(newApplication)).thenReturn(savedApplication);
        when(mapper.toResponse(savedApplication, offerSummary, candidateSummary))
                .thenReturn(ApplicationResponse.builder().id(1L).build());
        doThrow(new RuntimeException("downstream unavailable"))
                .when(offerServiceClient).incrementApplicationCount(OFFER_ID);

        assertThatCode(() -> applicationService.apply(CANDIDATE_ID, createRequest))
                .doesNotThrowAnyException();
        verify(offerServiceClient).incrementApplicationCount(OFFER_ID);
    }
}

