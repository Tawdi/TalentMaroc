package io.github.tawdi.jobboard.auth_user_service.repository;

import io.github.tawdi.jobboard.auth_user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query("""
                SELECT u FROM User u
                WHERE u.email = :value OR u.username = :value
            """)
    Optional<User> findByEmailOrUsername(@Param("value") String value);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
