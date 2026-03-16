package io.github.tawdi.jobboard.auth_user_service.service;

import io.github.tawdi.jobboard.auth_user_service.entity.User;
import io.github.tawdi.jobboard.auth_user_service.entity.VerificationToken;
import io.github.tawdi.jobboard.auth_user_service.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationToken createEmailVerificationToken(User user) {
        // Delete any existing verification tokens for this user
        verificationTokenRepository.deleteByUserId(user.getId());

        VerificationToken token = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .tokenType(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiryDate(LocalDateTime.now().plusHours(24)) // 24 hours
                .used(false)
                .build();

        return verificationTokenRepository.save(token);
    }

    public VerificationToken createPasswordResetToken(User user) {
        // Delete any existing password reset tokens for this user
        verificationTokenRepository.deleteByUserId(user.getId());

        VerificationToken token = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .tokenType(VerificationToken.TokenType.PASSWORD_RESET)
                .expiryDate(LocalDateTime.now().plusHours(1)) // 1 hour
                .used(false)
                .build();

        return verificationTokenRepository.save(token);
    }

    public VerificationToken validateToken(String token, VerificationToken.TokenType tokenType) {
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndTokenType(token, tokenType)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (verificationToken.isUsed()) {
            throw new RuntimeException("Token has already been used");
        }

        if (verificationToken.isExpired()) {
            throw new RuntimeException("Token has expired");
        }

        return verificationToken;
    }

    public void markTokenAsUsed(VerificationToken token) {
        token.setUsed(true);
        verificationTokenRepository.save(token);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        verificationTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        log.info("Expired verification tokens cleaned up");
    }
}
