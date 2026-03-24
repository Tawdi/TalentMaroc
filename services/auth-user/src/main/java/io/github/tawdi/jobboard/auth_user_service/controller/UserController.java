package io.github.tawdi.jobboard.auth_user_service.controller;

import io.github.tawdi.jobboard.auth_user_service.dto.UserResponse;
import io.github.tawdi.jobboard.auth_user_service.service.UserService;
import io.github.tawdi.jobboard.auth_user_service.dto.ApiResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<UserResponse>>> getAllUsers(@PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success("Users retrieved successfully", users));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("User deleted successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<UserResponse>> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponseDTO.success("User retrieved successfully",user));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDTO<UserResponse>> getUserById(@PathVariable String userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("User retrieved successfully",user));
    }
}
