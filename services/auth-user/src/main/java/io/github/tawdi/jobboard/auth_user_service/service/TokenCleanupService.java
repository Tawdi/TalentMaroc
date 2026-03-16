package io.github.tawdi.jobboard.auth_user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;

    /**
     * Clean up expired tokens every day at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTokens() {
        log.info("Starting scheduled token cleanup...");

        try {
            refreshTokenService.cleanupExpiredTokens();
            verificationTokenService.cleanupExpiredTokens();

            log.info("Token cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during token cleanup", e);
        }
    }
}
