package io.github.tawdi.jobboard.auth_user_service.controller;

import io.github.tawdi.jobboard.auth_user_service.dto.RoleRequest;
import io.github.tawdi.jobboard.auth_user_service.dto.RoleResponse;
import io.github.tawdi.jobboard.auth_user_service.service.RoleService;
import io.github.tawdi.jobboard.auth_user_service.dto.ApiResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<RoleResponse>> createRole(
            @Valid @RequestBody RoleRequest request) {

        RoleResponse response = roleService.createRole(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Role created successfully", response));
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<RoleResponse>> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody RoleRequest request) {

        RoleResponse response = roleService.updateRole(roleId, request);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Role updated successfully", response));
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Role deleted successfully", null));
    }

    @GetMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<RoleResponse>> getRoleById(@PathVariable Long roleId) {
        RoleResponse response = roleService.getRoleById(roleId);
        return ResponseEntity.ok(ApiResponseDTO.success("Role retrieved successfully", response));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<RoleResponse>> getRoleByName(@PathVariable String name) {
        RoleResponse response = roleService.getRoleByName(name);
        return ResponseEntity.ok(ApiResponseDTO.success("Role retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> responses = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponseDTO.success("Roles retrieved successfully", responses));
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<RoleResponse>> addPermissionToRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {

        RoleResponse response = roleService.addPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Permission added to role successfully", response));
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<RoleResponse>> removePermissionFromRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {

        RoleResponse response = roleService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Permission removed from role successfully", response));
    }
}
