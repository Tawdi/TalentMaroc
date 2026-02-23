package io.github.tawdi.jobboard.auth_user_service.controller;

import io.github.tawdi.jobboard.auth_user_service.dto.PermissionRequest;
import io.github.tawdi.jobboard.auth_user_service.dto.PermissionResponse;
import io.github.tawdi.jobboard.auth_user_service.service.PermissionService;
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
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Slf4j
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<PermissionResponse>> createPermission(
            @Valid @RequestBody PermissionRequest request) {

        PermissionResponse response = permissionService.createPermission(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Permission created successfully", response));
    }

    @PutMapping("/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<PermissionResponse>> updatePermission(
            @PathVariable Long permissionId,
            @Valid @RequestBody PermissionRequest request) {

        PermissionResponse response = permissionService.updatePermission(permissionId, request);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Permission updated successfully", response));
    }

    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deletePermission(@PathVariable Long permissionId) {
        permissionService.deletePermission(permissionId);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Permission deleted successfully", null));
    }

    @GetMapping("/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<PermissionResponse>> getPermissionById(
            @PathVariable Long permissionId) {

        PermissionResponse response = permissionService.getPermissionById(permissionId);
        return ResponseEntity.ok(ApiResponseDTO.success("Permission retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<PermissionResponse>>> getAllPermissions() {
        List<PermissionResponse> responses = permissionService.getAllPermissions();
        return ResponseEntity.ok(ApiResponseDTO.success("Permissions retrieved successfully", responses));
    }
}
