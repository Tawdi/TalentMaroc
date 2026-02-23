package io.github.tawdi.jobboard.auth_user_service.oauth2;

import io.github.tawdi.jobboard.auth_user_service.entity.Role;
import io.github.tawdi.jobboard.auth_user_service.entity.User;
import io.github.tawdi.jobboard.auth_user_service.repository.RoleRepository;
import io.github.tawdi.jobboard.auth_user_service.repository.UserRepository;
import io.github.tawdi.jobboard.auth_user_service.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

/**
 * Custom OAuth2 User Service that loads and creates users from OAuth2 providers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update existing user
            user = updateExistingUser(user, oAuth2UserInfo, registrationId);
        } else {
            // Register new user
            user = registerNewUser(oAuth2UserInfo, registrationId);
        }

        return new CustomOAuth2User(oAuth2User.getAttributes(), user);
    }

    private User registerNewUser(OAuth2UserInfo oAuth2UserInfo, String provider) {
        // Get default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default USER role not found"));

        User user = User.builder()
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .username(generateUsername(oAuth2UserInfo.getEmail()))
                .password("oauth")
                .provider(provider.toUpperCase())
                .providerId(oAuth2UserInfo.getId())
                .imageUrl(oAuth2UserInfo.getImageUrl())
                .role(userRole)
                .enabled(true) // OAuth users are pre-verified by provider
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered via OAuth2: {} (provider: {})", savedUser.getEmail(), provider);

        return savedUser;
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo, String provider) {
        // Update name and image if changed
        if (StringUtils.hasText(oAuth2UserInfo.getName())) {
            existingUser.setName(oAuth2UserInfo.getName());
        }
        if (StringUtils.hasText(oAuth2UserInfo.getImageUrl())) {
            existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        }

        // Update provider info if not set
        if (!StringUtils.hasText(existingUser.getProvider())) {
            existingUser.setProvider(provider.toUpperCase());
            existingUser.setProviderId(oAuth2UserInfo.getId());
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("User updated via OAuth2: {} (provider: {})", updatedUser.getEmail(), provider);

        return updatedUser;
    }

    private String generateUsername(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int counter = 1;

        // Ensure username is unique
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter++;
        }

        return username;
    }
}
