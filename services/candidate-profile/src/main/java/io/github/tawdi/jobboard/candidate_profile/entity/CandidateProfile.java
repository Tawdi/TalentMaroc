package io.github.tawdi.jobboard.candidate_profile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidate_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Links to the auth-user-service User.id (UUID string) */
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String headline;       // e.g. "Full-Stack Developer | Spring Boot & Angular"

    @Column(columnDefinition = "TEXT")
    private String about;

    private String phone;

    private LocalDate dateOfBirth;

    private String photoUrl;

    @Column(name = "cv_file_path")
    private String cvFilePath;

    @Column(name = "cv_original_name")
    private String cvOriginalName;

    // --- Embedded address ---
    @Embedded
    private Address address;

    // --- One-to-many relationships ---
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startDate DESC")
    @Builder.Default
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startDate DESC")
    @Builder.Default
    private List<Formation> formations = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SpokenLanguage> spokenLanguages = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // --- Helper methods ---
    public void addExperience(Experience exp) {
        experiences.add(exp);
        exp.setProfile(this);
    }

    public void addFormation(Formation formation) {
        formations.add(formation);
        formation.setProfile(this);
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
        skill.setProfile(this);
    }

    public void addProject(Project project) {
        projects.add(project);
        project.setProfile(this);
    }

    public void addSpokenLanguage(SpokenLanguage lang) {
        spokenLanguages.add(lang);
        lang.setProfile(this);
    }
}

