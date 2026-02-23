package io.github.tawdi.jobboard.auth_user_service.jwt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtConfig {

    @Value("${job-board.jwt.secret-key}")
    private String secret;

    @Value("${job-board.jwt.expiration-ms}")
    private long expirationMs;
}
