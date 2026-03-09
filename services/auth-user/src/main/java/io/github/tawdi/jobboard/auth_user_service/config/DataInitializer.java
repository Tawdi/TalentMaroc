package io.github.tawdi.jobboard.auth_user_service.config;

import io.github.tawdi.jobboard.auth_user_service.entity.Permission;
import io.github.tawdi.jobboard.auth_user_service.entity.Role;
import io.github.tawdi.jobboard.auth_user_service.repository.PermissionRepository;
import io.github.tawdi.jobboard.auth_user_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (permissionRepository.count() == 0) {
                initializePermissions();
            }
            if (roleRepository.count() == 0) {
                initializeRoles();
            }
        };
    }

    private void initializePermissions() {
        log.info("Initializing permissions...");

        List<Permission> permissions = Arrays.asList(
            // User permissions
            Permission.builder().name("READ_PROFILE").description("View user profiles").build(),
            Permission.builder().name("UPDATE_PROFILE").description("Update own profile").build(),
            Permission.builder().name("DELETE_PROFILE").description("Delete own profile").build(),

            // Job permissions
            Permission.builder().name("VIEW_JOBS").description("View job listings").build(),
            Permission.builder().name("CREATE_JOB").description("Create job postings").build(),
            Permission.builder().name("UPDATE_JOB").description("Update job postings").build(),
            Permission.builder().name("DELETE_JOB").description("Delete job postings").build(),

            // Application permissions
            Permission.builder().name("APPLY_JOB").description("Apply to jobs").build(),
            Permission.builder().name("VIEW_APPLICATIONS").description("View job applications").build(),
            Permission.builder().name("UPDATE_APPLICATION").description("Update application status").build(),

            // Admin permissions
            Permission.builder().name("MANAGE_USERS").description("Manage all users").build(),
            Permission.builder().name("MANAGE_ROLES").description("Manage roles and permissions").build(),
            Permission.builder().name("VIEW_ANALYTICS").description("View system analytics").build()
        );

        permissionRepository.saveAll(permissions);
        log.info("Initialized {} permissions", permissions.size());
    }

    private void initializeRoles() {
        log.info("Initializing roles...");

        // Get permissions
        Permission readProfile = permissionRepository.findByName("READ_PROFILE").orElseThrow();
        Permission updateProfile = permissionRepository.findByName("UPDATE_PROFILE").orElseThrow();
        Permission deleteProfile = permissionRepository.findByName("DELETE_PROFILE").orElseThrow();
        Permission viewJobs = permissionRepository.findByName("VIEW_JOBS").orElseThrow();
        Permission createJob = permissionRepository.findByName("CREATE_JOB").orElseThrow();
        Permission updateJob = permissionRepository.findByName("UPDATE_JOB").orElseThrow();
        Permission deleteJob = permissionRepository.findByName("DELETE_JOB").orElseThrow();
        Permission applyJob = permissionRepository.findByName("APPLY_JOB").orElseThrow();
        Permission viewApplications = permissionRepository.findByName("VIEW_APPLICATIONS").orElseThrow();
        Permission updateApplication = permissionRepository.findByName("UPDATE_APPLICATION").orElseThrow();
        Permission manageUsers = permissionRepository.findByName("MANAGE_USERS").orElseThrow();
        Permission manageRoles = permissionRepository.findByName("MANAGE_ROLES").orElseThrow();
        Permission viewAnalytics = permissionRepository.findByName("VIEW_ANALYTICS").orElseThrow();

        // ADMIN Role - Full access
        Role adminRole = Role.builder()
            .name("ADMIN")
            .description("Administrator with full system access")
            .permissions(new HashSet<>(Arrays.asList(
                readProfile, updateProfile, deleteProfile,
                viewJobs, createJob, updateJob, deleteJob,
                applyJob, viewApplications, updateApplication,
                manageUsers, manageRoles, viewAnalytics
            )))
            .build();

        // COMPANY Role - Can post and manage jobs
        Role companyRole = Role.builder()
            .name("COMPANY")
            .description("Company/Employer account")
            .permissions(new HashSet<>(Arrays.asList(
                readProfile, updateProfile,
                viewJobs, createJob, updateJob, deleteJob,
                viewApplications, updateApplication
            )))
            .build();

        // CANDIDATE Role - Can apply to jobs
        Role studentRole = Role.builder()
            .name("CANDIDATE")
            .description("Student/ CANDIDATE /Job seeker account")
            .permissions(new HashSet<>(Arrays.asList(
                readProfile, updateProfile, deleteProfile,
                viewJobs, applyJob, viewApplications
            )))
            .build();

        // USER Role - Basic access
        Role userRole = Role.builder()
            .name("USER")
            .description("Basic user account")
            .permissions(new HashSet<>(Arrays.asList(
                readProfile, updateProfile,
                viewJobs
            )))
            .build();

        roleRepository.saveAll(Arrays.asList(adminRole, companyRole, studentRole, userRole));
        log.info("Initialized 4 roles: ADMIN, COMPANY, CANDIDATE, USER");
    }
}
