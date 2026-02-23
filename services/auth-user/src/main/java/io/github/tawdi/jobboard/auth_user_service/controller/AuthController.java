package io.github.tawdi.jobboard.auth_user_service.controller;

import io.github.tawdi.jobboard.auth_user_service.dto.*;
import io.github.tawdi.jobboard.auth_user_service.service.AuthService;
import io.github.tawdi.jobboard.auth_user_service.dto.ApiResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(
                    "Registration successful. Please check your email to verify your account.",
                    response
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(
            ApiResponseDTO.success("Login successful", response)
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponseDTO<String>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request.getToken());
        return ResponseEntity.ok(
            ApiResponseDTO.success("Email verified successfully. You can now login.", null)
        );
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponseDTO<String>> verifyEmailGet(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(
            ApiResponseDTO.success("Email verified successfully. You can now login.", null)
        );
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponseDTO<String>> resendVerification(@RequestParam String email) {
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok(
            ApiResponseDTO.success("Verification email sent. Please check your inbox.", null)
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseDTO<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(
            ApiResponseDTO.success("If the email exists, a password reset link has been sent.", null)
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(
            ApiResponseDTO.success("Password reset successful. You can now login with your new password.", null)
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponseDTO<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(
            ApiResponseDTO.success("Token refreshed successfully", response)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<String>> logout(@RequestParam String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok(
            ApiResponseDTO.success("Logged out successfully", null)
        );
    }
}
