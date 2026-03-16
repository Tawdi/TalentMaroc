package io.github.tawdi.jobboard.applications.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "applications",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_candidate_offer",
                columnNames = {"candidate_user_id", "offer_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Links to the auth-user-service User.id (UUID string) */
    @Column(name = "candidate_user_id", nullable = false)
    private String candidateUserId;

    /** Links to the company-offers-service Offer.id */
    @Column(name = "offer_id", nullable = false)
    private Long offerId;

    /** URL/path to the candidate's CV file */
    @Column(name = "cv_url", nullable = false)
    private String cvUrl;

    /** Optional cover letter */
    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.RECEIVED;

    /** Internal note for the company/recruiter (not visible to candidate) */
    @Column(name = "company_note", columnDefinition = "TEXT")
    private String companyNote;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // --- Helper methods ---

    public boolean isActive() {
        return this.status != ApplicationStatus.WITHDRAWN
                && this.status != ApplicationStatus.REJECTED
                && this.status != ApplicationStatus.ACCEPTED;
    }

    public boolean canBeWithdrawn() {
        return this.status == ApplicationStatus.RECEIVED
                || this.status == ApplicationStatus.REVIEWING;
    }
}
