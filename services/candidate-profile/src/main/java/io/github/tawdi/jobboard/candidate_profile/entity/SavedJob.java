package io.github.tawdi.jobboard.candidate_profile.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "candidate_saved_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "offer_id", nullable = false)
    private Long offerId;

    @Column(name = "saved_at", nullable = false, updatable = false)
    private Instant savedAt;

    @PrePersist
    void onPersist() {
        if (savedAt == null) {
            savedAt = Instant.now();
        }
    }
}

