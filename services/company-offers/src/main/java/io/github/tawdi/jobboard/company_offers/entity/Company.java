package io.github.tawdi.jobboard.company_offers.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Links to the auth-user-service User.id (UUID string) */
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String sector;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String website;

    private String phone;

    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CompanyStatus status = CompanyStatus.PENDING;

    private LocalDateTime validatedAt;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    @Builder.Default
    private List<Offer> offers = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // --- Helper methods ---
    public void addOffer(Offer offer) {
        offers.add(offer);
        offer.setCompany(this);
    }

    public void removeOffer(Offer offer) {
        offers.remove(offer);
        offer.setCompany(null);
    }

    public boolean isApproved() {
        return this.status == CompanyStatus.APPROVED;
    }
}

