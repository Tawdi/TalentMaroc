package io.github.tawdi.jobboard.auth_user_service.repository;

import io.github.tawdi.jobboard.auth_user_service.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByTokenAndTokenType(String token, VerificationToken.TokenType tokenType);

    void deleteByExpiryDateBefore(LocalDateTime date);

    void deleteByUserId(String userId);
}
