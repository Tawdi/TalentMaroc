package io.github.tawdi.jobboard.auth_user_service.service;

import io.github.tawdi.jobboard.auth_user_service.dto.RoleRequest;
import io.github.tawdi.jobboard.auth_user_service.dto.RoleResponse;
import io.github.tawdi.jobboard.auth_user_service.dto.PermissionResponse;
import io.github.tawdi.jobboard.auth_user_service.entity.Permission;
import io.github.tawdi.jobboard.auth_user_service.entity.Role;
import io.github.tawdi.jobboard.auth_user_service.repository.PermissionRepository;
import io.github.tawdi.jobboard.auth_user_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role with name '" + request.getName() + "' already exists");
        }

        Set<Permission> permissions = new HashSet<>();
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            permissions = new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(permissions)
                .build();

        Role savedRole = roleRepository.save(role);
        log.info("Role created: {}", savedRole.getName());

        return mapToRoleResponse(savedRole);
    }

    public RoleResponse updateRole(Long roleId, RoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        if (!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role with name '" + request.getName() + "' already exists");
        }

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        // Update permissions
        if (request.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));
            role.setPermissions(permissions);
        }

        Role updatedRole = roleRepository.save(role);
        log.info("Role updated: {}", updatedRole.getName());

        return mapToRoleResponse(updatedRole);
    }

    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        roleRepository.delete(role);
        log.info("Role deleted: {}", role.getName());
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        return mapToRoleResponse(role);
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleByName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));

        return mapToRoleResponse(role);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToRoleResponse)
                .collect(Collectors.toList());
    }

    public RoleResponse addPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        role.getPermissions().add(permission);
        Role updatedRole = roleRepository.save(role);

        log.info("Permission '{}' added to role '{}'", permission.getName(), role.getName());

        return mapToRoleResponse(updatedRole);
    }

    public RoleResponse removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        role.getPermissions().remove(permission);
        Role updatedRole = roleRepository.save(role);

        log.info("Permission '{}' removed from role '{}'", permission.getName(), role.getName());

        return mapToRoleResponse(updatedRole);
    }

    private RoleResponse mapToRoleResponse(Role role) {
        Set<PermissionResponse> permissions = role.getPermissions().stream()
                .map(p -> PermissionResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .build())
                .collect(Collectors.toSet());

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissions)
                .build();
    }
}
