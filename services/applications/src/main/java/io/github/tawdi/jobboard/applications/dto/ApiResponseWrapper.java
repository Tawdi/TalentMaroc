package io.github.tawdi.jobboard.applications.dto;

import lombok.*;

/**
 * Generic API response wrapper matching the standard response format
 * used across all microservices: { status, message, data }.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseWrapper<T> {
    private String status;
    private String message;
    private T data;
}
