package io.github.tawdi.jobboard.auth_user_service.service;

import io.github.tawdi.jobboard.auth_user_service.dto.PermissionRequest;
import io.github.tawdi.jobboard.auth_user_service.dto.PermissionResponse;
import io.github.tawdi.jobboard.auth_user_service.entity.Permission;
import io.github.tawdi.jobboard.auth_user_service.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionResponse createPermission(PermissionRequest request) {
        if (permissionRepository.existsByName(request.getName())) {
            throw new RuntimeException("Permission with name '" + request.getName() + "' already exists");
        }

        Permission permission = Permission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created: {}", savedPermission.getName());

        return mapToPermissionResponse(savedPermission);
    }

    public PermissionResponse updatePermission(Long permissionId, PermissionRequest request) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        if (!permission.getName().equals(request.getName()) &&
            permissionRepository.existsByName(request.getName())) {
            throw new RuntimeException("Permission with name '" + request.getName() + "' already exists");
        }

        permission.setName(request.getName());
        permission.setDescription(request.getDescription());

        Permission updatedPermission = permissionRepository.save(permission);
        log.info("Permission updated: {}", updatedPermission.getName());

        return mapToPermissionResponse(updatedPermission);
    }

    public void deletePermission(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        permissionRepository.delete(permission);
        log.info("Permission deleted: {}", permission.getName());
    }

    @Transactional(readOnly = true)
    public PermissionResponse getPermissionById(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        return mapToPermissionResponse(permission);
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toList());
    }

    private PermissionResponse mapToPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}
