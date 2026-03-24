package io.github.tawdi.jobboard.auth_user_service.service;

import io.github.tawdi.jobboard.auth_user_service.dto.*;
import io.github.tawdi.jobboard.auth_user_service.entity.RefreshToken;
import io.github.tawdi.jobboard.auth_user_service.entity.Role;
import io.github.tawdi.jobboard.auth_user_service.entity.User;
import io.github.tawdi.jobboard.auth_user_service.entity.VerificationToken;
import io.github.tawdi.jobboard.auth_user_service.exceptions.*;
import io.github.tawdi.jobboard.auth_user_service.service.NotificationProducerService;
import io.github.tawdi.jobboard.auth_user_service.jwt.JwtService;
import io.github.tawdi.jobboard.auth_user_service.repository.RoleRepository;
import io.github.tawdi.jobboard.auth_user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;
    private final NotificationProducerService notificationProducerService;

    @Value("${job-board.jwt.expiration-ms:3600000}")
    private Long jwtExpirationMs;


    public LoginResponse login(LoginRequest request) {
        String loginField = request.getEmail() != null ? request.getEmail() : request.getUsername();

        if (loginField == null || loginField.trim().isEmpty()) {
            throw new InvalidCredentialsException("Email or username is required");
        }

        User user = userRepository.findByEmail(loginField)
                .or(() -> userRepository.findByUsername(loginField))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Check if account is enabled
        if (!user.isEnabled()) {
            throw new AccountNotVerifiedException("Account is not verified. Please check your email.");
        }

        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        log.info("User logged in successfully: {}", user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtExpirationMs / 1000) // Convert to seconds
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().getName())
                .build();
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new EmailAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        String roleName = request.getRoleName() != null ? request.getRoleName() : "USER";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(role)
                .provider("LOCAL")
                .enabled(false)
                .build();

        User savedUser = userRepository.save(user);

        VerificationToken verificationToken = verificationTokenService.createEmailVerificationToken(savedUser);
        
        // Publish async  event instead of blocking to send an email
        notificationProducerService.sendWelcomeEmail(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().getName());

        notificationProducerService.sendVerificationEmail(
                savedUser.getId(),
                savedUser.getEmail(),
                verificationToken.getToken()
        );
        log.info("User registered successfully: {}", savedUser.getEmail());

        return UserResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .name(savedUser.getName())
                .role(savedUser.getRole().getName())
                .build();
    }

    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenService.validateToken(
                token, VerificationToken.TokenType.EMAIL_VERIFICATION
        );

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenService.markTokenAsUsed(verificationToken);

        log.info("Email verified for user: {}", user.getEmail());
    }

    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (user.isEnabled()) {
            throw new IllegalStateException("Account is already verified");
        }

        VerificationToken verificationToken = verificationTokenService.createEmailVerificationToken(user);
        notificationProducerService.sendVerificationEmail(user.getId(), user.getEmail(), verificationToken.getToken());

        log.info("Verification email resent to: {}", email);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            log.warn("Password reset requested for non-existent email: {}", email);
            return;
        }

        VerificationToken resetToken = verificationTokenService.createPasswordResetToken(user);
        notificationProducerService.sendPasswordResetEmail(user.getId(), user.getEmail(), resetToken.getToken());

        log.info("Password reset email sent to: {}", email);
    }

    public void resetPassword(String token, String newPassword) {
        VerificationToken verificationToken = verificationTokenService.validateToken(
                token, VerificationToken.TokenType.PASSWORD_RESET
        );

        User user = verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        verificationTokenService.markTokenAsUsed(verificationToken);


        refreshTokenService.revokeAllUserTokens(user.getId());

        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    public TokenResponse refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenStr);
        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateToken(user);

        log.info("Access token refreshed for user: {}", user.getEmail());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenStr)
                .expiresIn(jwtExpirationMs / 1000)
                .build();
    }

    public void logout(String refreshToken) {
        try {
            refreshTokenService.revokeRefreshToken(refreshToken);
            log.info("User logged out successfully");
        } catch (Exception e) {
            log.warn("Logout attempted with invalid refresh token");
        }
    }
}
