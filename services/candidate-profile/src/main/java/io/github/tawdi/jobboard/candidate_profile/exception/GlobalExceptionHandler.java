package io.github.tawdi.jobboard.candidate_profile.exception;

import io.github.tawdi.jobboard.candidate_profile.dto.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        log.error("Resource not found: {}", ex.getMessage());
        ApiResponseDTO<String> response = ApiResponseDTO.error("Resource not found: " + ex.getMessage());
        response.setPath(request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProfileAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleProfileAlreadyExists(
            ProfileAlreadyExistsException ex, HttpServletRequest request) {

        log.error("Profile already exists: {}", ex.getMessage());
        ApiResponseDTO<String> response = ApiResponseDTO.error(ex.getMessage());
        response.setPath(request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseDTO<String>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponseDTO<String> response = ApiResponseDTO.error("Validation failed", errors);
        response.setPath(request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleMaxUploadSize(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {

        ApiResponseDTO<String> response = ApiResponseDTO.error("File size exceeds the maximum allowed size (10MB)");
        response.setPath(request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        ApiResponseDTO<String> response = ApiResponseDTO.error(ex.getMessage());
        response.setPath(request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleNoResourceFound(
            NoResourceFoundException ex, HttpServletRequest request) {

        ApiResponseDTO<String> response = ApiResponseDTO.error("Endpoint not found");
        response.setPath(request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<String>> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error", ex);
        ApiResponseDTO<String> response = ApiResponseDTO.error("An unexpected error occurred");
        response.setPath(request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

