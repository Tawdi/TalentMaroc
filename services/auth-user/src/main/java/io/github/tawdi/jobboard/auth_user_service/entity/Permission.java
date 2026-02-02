package io.github.tawdi.jobboard.auth_user_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // READ_PROFILE, CREATE_OFFER, etc.
    
    @Column(length = 255)
    private String description;
}