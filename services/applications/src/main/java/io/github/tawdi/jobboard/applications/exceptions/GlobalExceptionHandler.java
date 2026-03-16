package io.github.tawdi.jobboard.applications.exceptions;

import io.github.tawdi.jobboard.applications.dto.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Applications Service exception handler.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /* --------------------------------------------------------------------- *
     *  1. ResourceNotFoundException (custom 404)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.error("== RESOURCE NOT FOUND ==> {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(
                "Resource not found: " + ex.getMessage());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /* --------------------------------------------------------------------- *
     *  2. DuplicateApplicationException (409 Conflict)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleDuplicateApplication(
            DuplicateApplicationException ex,
            HttpServletRequest request) {

        log.warn("== DUPLICATE APPLICATION ==> {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(ex.getMessage());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /* --------------------------------------------------------------------- *
     *  3. InvalidStatusTransitionException (400 Bad Request)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleInvalidStatusTransition(
            InvalidStatusTransitionException ex,
            HttpServletRequest request) {

        log.warn("== INVALID STATUS TRANSITION ==> {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(ex.getMessage());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /* --------------------------------------------------------------------- *
     *  4. Validation errors (@Valid, @Validated)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseDTO<String>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String msg = error.getDefaultMessage();
            errors.put(field, msg);
        });

        log.warn(" == VALIDATION FAILED ==>  {}", errors);

        ApiResponseDTO<String> response = ApiResponseDTO.error("Validation failed", errors);
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /* --------------------------------------------------------------------- *
     *  5. Path-variable type mismatch
     * --------------------------------------------------------------------- */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.error(" Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());

        String msg = String.format(
                "Parameter '%s' should be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        ApiResponseDTO<String> response = ApiResponseDTO.error(msg);
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /* --------------------------------------------------------------------- *
     *  6. HTTP 405 – Method Not Allowed
     * --------------------------------------------------------------------- */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        log.warn("HTTP method not supported: {} {}", request.getMethod(), request.getRequestURI());

        String supported = ex.getSupportedHttpMethods() != null
                ? ex.getSupportedHttpMethods().toString()
                : "none";

        String message = String.format(
                "HTTP method '%s' is not supported for this endpoint. Supported: %s",
                request.getMethod(), supported);

        ApiResponseDTO<String> response = ApiResponseDTO.error(message);
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /* --------------------------------------------------------------------- *
     *  7. JSON parse errors
     * --------------------------------------------------------------------- */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleJsonError(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponseDTO.error("Invalid JSON"));
    }

    /* --------------------------------------------------------------------- *
     *  8. IllegalStateException (business rule violations)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleIllegalState(
            IllegalStateException ex, HttpServletRequest request) {
        log.warn("Business rule violation: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }

    /* --------------------------------------------------------------------- *
     *  9. No static resource / wrong URL
     * --------------------------------------------------------------------- */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        log.warn("No resource found for request: {} {}", request.getMethod(), request.getRequestURI());

        String message = String.format(
                "The requested endpoint '%s' does not exist. Check the URL and HTTP method.",
                request.getRequestURI()
        );

        ApiResponseDTO<String> response = ApiResponseDTO.error(message);
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /* --------------------------------------------------------------------- *
     *  10. Catch-all (500)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<String>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error", ex);

        ApiResponseDTO<String> response = ApiResponseDTO.error(
                "An unexpected error occurred. Please contact support.");
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
