package io.github.tawdi.jobboard.auth_user_service.service;

import io.github.tawdi.jobboard.auth_user_service.dto.*;
import io.github.tawdi.jobboard.auth_user_service.entity.RefreshToken;
import io.github.tawdi.jobboard.auth_user_service.entity.Role;
import io.github.tawdi.jobboard.auth_user_service.entity.User;
import io.github.tawdi.jobboard.auth_user_service.entity.VerificationToken;
import io.github.tawdi.jobboard.auth_user_service.exceptions.AccountNotVerifiedException;
import io.github.tawdi.jobboard.auth_user_service.exceptions.EmailAlreadyExistsException;
import io.github.tawdi.jobboard.auth_user_service.exceptions.InvalidCredentialsException;
import io.github.tawdi.jobboard.auth_user_service.exceptions.UserNotFoundException;
import io.github.tawdi.jobboard.auth_user_service.jwt.JwtService;
import io.github.tawdi.jobboard.auth_user_service.repository.RoleRepository;
import io.github.tawdi.jobboard.auth_user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private VerificationTokenService verificationTokenService;


    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role testRole;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Set JWT expiration via reflection
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", 3600000L);

        // Setup test role
        testRole = Role.builder()
                .id(1L)
                .name("USER")
                .description("Default user role")
                .permissions(new HashSet<>())
                .build();

        // Setup test user
        testUser = User.builder()
                .id(UUID.randomUUID().toString())
                .email("test@example.com")
                .username("testuser")
                .password("encodedPassword")
                .name("Test User")
                .role(testRole)
                .enabled(true)
                .build();

        // Setup register request
        registerRequest = RegisterRequest.builder()
                .email("newuser@example.com")
                .username("newuser")
                .password("password123")
                .name("New User")
                .roleName("USER")
                .build();

        // Setup login request
        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();
    }

    // ========================================================================
    // REGISTRATION TESTS
    // ========================================================================

    @Test
    @DisplayName("Should register new user successfully")
    void register_ShouldCreateNewUser_WhenValidRequest() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        // Create a user that matches the registerRequest data
        User newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password("encodedPassword")
                .name(registerRequest.getName())
                .role(testRole)
                .enabled(false) // Email not verified yet
                .build();
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        VerificationToken token = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(newUser)
                .tokenType(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
        when(verificationTokenService.createEmailVerificationToken(any(User.class))).thenReturn(token);

        // Act
        UserResponse response = authService.register(registerRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(response.getUsername()).isEqualTo(registerRequest.getUsername());
        assertThat(response.getName()).isEqualTo(registerRequest.getName());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository).save(any(User.class));
        // verify(emailService).sendVerificationEmail(any(User.class), anyString());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void register_ShouldThrowException_WhenUsernameExists() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username already exists");

        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when role not found")
    void register_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role not found");

        verify(userRepository, never()).save(any(User.class));
    }

    // ========================================================================
    // LOGIN TESTS
    // ========================================================================

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void login_ShouldReturnTokens_WhenValidCredentials() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("access-token");

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(testUser)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        when(refreshTokenService.createRefreshToken(testUser)).thenReturn(refreshToken);

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken.getToken());
        assertThat(response.getUserId()).isEqualTo(testUser.getId());
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(response.getExpiresIn()).isEqualTo(3600L);

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
        verify(jwtService).generateToken(testUser);
        verify(refreshTokenService).createRefreshToken(testUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void login_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(loginRequest.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid credentials");

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void login_ShouldThrowException_WhenPasswordIncorrect() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid credentials");

        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when account not verified")
    void login_ShouldThrowException_WhenAccountNotVerified() {
        // Arrange
        testUser.setEnabled(false);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AccountNotVerifiedException.class)
                .hasMessageContaining("Account is not verified");

        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should login with username instead of email")
    void login_ShouldWork_WhenUsingUsername() {
        // Arrange
        LoginRequest usernameRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        // Mock findByEmail to return empty (since email is null in the request)
        when(userRepository.findByEmail("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(usernameRequest.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("access-token");

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(testUser)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        when(refreshTokenService.createRefreshToken(testUser)).thenReturn(refreshToken);

        // Act
        LoginResponse response = authService.login(usernameRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");

        verify(userRepository).findByUsername("testuser");
    }

    // ========================================================================
    // EMAIL VERIFICATION TESTS
    // ========================================================================

    @Test
    @DisplayName("Should verify email successfully")
    void verifyEmail_ShouldEnableUser_WhenValidToken() {
        // Arrange
        String token = UUID.randomUUID().toString();
        testUser.setEnabled(false);

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .tokenType(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .used(false)
                .build();

        when(verificationTokenService.validateToken(token, VerificationToken.TokenType.EMAIL_VERIFICATION))
                .thenReturn(verificationToken);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        authService.verifyEmail(token);

        // Assert
        assertThat(testUser.isEnabled()).isTrue();
        verify(verificationTokenService).validateToken(token, VerificationToken.TokenType.EMAIL_VERIFICATION);
        verify(verificationTokenService).markTokenAsUsed(verificationToken);
        verify(userRepository).save(testUser);
    }

    // ========================================================================
    // PASSWORD RESET TESTS
    // ========================================================================

    @Test
    @DisplayName("Should send password reset email for existing user")
    void forgotPassword_ShouldSendEmail_WhenUserExists() {
        // Arrange
        String email = "test@example.com";
        VerificationToken resetToken = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(testUser)
                .tokenType(VerificationToken.TokenType.PASSWORD_RESET)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(verificationTokenService.createPasswordResetToken(testUser)).thenReturn(resetToken);

        // Act
        authService.forgotPassword(email);

        // Assert
        verify(userRepository).findByEmail(email);
        verify(verificationTokenService).createPasswordResetToken(testUser);
        // verify(emailService).sendPasswordResetEmail(testUser, resetToken.getToken());
    }

    @Test
    @DisplayName("Should not reveal user existence for non-existing email")
    void forgotPassword_ShouldNotRevealUser_WhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        authService.forgotPassword(email);

        // Assert
        verify(userRepository).findByEmail(email);
        verify(verificationTokenService, never()).createPasswordResetToken(any());
        // verify(emailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    @DisplayName("Should reset password successfully")
    void resetPassword_ShouldUpdatePassword_WhenValidToken() {
        // Arrange
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";

        VerificationToken resetToken = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .tokenType(VerificationToken.TokenType.PASSWORD_RESET)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        when(verificationTokenService.validateToken(token, VerificationToken.TokenType.PASSWORD_RESET))
                .thenReturn(resetToken);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        authService.resetPassword(token, newPassword);

        // Assert
        verify(verificationTokenService).validateToken(token, VerificationToken.TokenType.PASSWORD_RESET);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);
        verify(verificationTokenService).markTokenAsUsed(resetToken);
        verify(refreshTokenService).revokeAllUserTokens(testUser.getId());
    }

    // ========================================================================
    // TOKEN REFRESH TESTS
    // ========================================================================

    @Test
    @DisplayName("Should refresh access token successfully")
    void refreshAccessToken_ShouldReturnNewToken_WhenValidRefreshToken() {
        // Arrange
        String refreshTokenString = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .user(testUser)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        when(refreshTokenService.verifyRefreshToken(refreshTokenString)).thenReturn(refreshToken);
        when(jwtService.generateToken(testUser)).thenReturn("new-access-token");

        // Act
        TokenResponse response = authService.refreshAccessToken(refreshTokenString);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo(refreshTokenString);
        assertThat(response.getExpiresIn()).isEqualTo(3600L);

        verify(refreshTokenService).verifyRefreshToken(refreshTokenString);
        verify(jwtService).generateToken(testUser);
    }

    // ========================================================================
    // LOGOUT TESTS
    // ========================================================================

    @Test
    @DisplayName("Should logout and revoke refresh token")
    void logout_ShouldRevokeToken_WhenValidRefreshToken() {
        // Arrange
        String refreshToken = UUID.randomUUID().toString();
        doNothing().when(refreshTokenService).revokeRefreshToken(refreshToken);

        // Act
        authService.logout(refreshToken);

        // Assert
        verify(refreshTokenService).revokeRefreshToken(refreshToken);
    }

    // ========================================================================
    // RESEND VERIFICATION TESTS
    // ========================================================================

    @Test
    @DisplayName("Should resend verification email")
    void resendVerificationEmail_ShouldSendEmail_WhenUserExists() {
        // Arrange
        String email = "test@example.com";
        testUser.setEnabled(false);

        VerificationToken token = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(testUser)
                .tokenType(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(verificationTokenService.createEmailVerificationToken(testUser)).thenReturn(token);

        // Act
        authService.resendVerificationEmail(email);

        // Assert
        verify(userRepository).findByEmail(email);
        verify(verificationTokenService).createEmailVerificationToken(testUser);
        // verify(emailService).sendVerificationEmail(testUser, token.getToken());
    }

    @Test
    @DisplayName("Should throw exception when user not found for resend")
    void resendVerificationEmail_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.resendVerificationEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");

        // verify(emailService, never()).sendVerificationEmail(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when account already verified")
    void resendVerificationEmail_ShouldThrowException_WhenAlreadyVerified() {
        // Arrange
        String email = "test@example.com";
        testUser.setEnabled(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThatThrownBy(() -> authService.resendVerificationEmail(email))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Account is already verified");

        // verify(emailService, never()).sendVerificationEmail(any(), any());
    }
}
