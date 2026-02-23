package io.github.tawdi.jobboard.auth_user_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String username;
    private String name;
    private String role;
}