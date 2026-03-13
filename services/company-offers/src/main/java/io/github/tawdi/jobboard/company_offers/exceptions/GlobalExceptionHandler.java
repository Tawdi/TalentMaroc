package io.github.tawdi.jobboard.company_offers.exceptions;

import io.github.tawdi.jobboard.company_offers.dto.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /* --------------------------------------------------------------------- *
     *  1. ResourceNotFoundException (custom 404)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        logger.error("== RESOURCE NOT FOUND ==> {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(
                "Resource not found: " + ex.getMessage());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /* --------------------------------------------------------------------- *
     *  2. CompanyAlreadyExistsException (409)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(CompanyAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleCompanyAlreadyExists(
            CompanyAlreadyExistsException ex,
            HttpServletRequest request) {

        logger.error("Company already exists: {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(ex.getMessage());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /* --------------------------------------------------------------------- *
     *  3. CompanyNotApprovedException (403)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(CompanyNotApprovedException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleCompanyNotApproved(
            CompanyNotApprovedException ex,
            HttpServletRequest request) {

        logger.warn("Company not approved: {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(ex.getMessage());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
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

        logger.warn(" == VALIDATION FAILED ==>  {}", errors);

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

        logger.error(" Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());

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

        logger.warn("HTTP method not supported: {} {}", request.getMethod(), request.getRequestURI());

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
     *  8. Business rule violation
     * --------------------------------------------------------------------- */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleIllegalState(
            IllegalStateException ex, HttpServletRequest request) {
        logger.warn("Business rule violation: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        ApiResponseDTO<String> response = ApiResponseDTO.error(ex.getMessage());
        response.setPath(request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /* --------------------------------------------------------------------- *
     *  9. No static resource / wrong URL
     * --------------------------------------------------------------------- */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        logger.warn("No resource found for request: {} {}", request.getMethod(), request.getRequestURI());

        String message = String.format(
                "The requested endpoint '%s' does not exist. Check the URL and HTTP method.",
                request.getRequestURI());

        ApiResponseDTO<String> response = ApiResponseDTO.error(message);
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /* --------------------------------------------------------------------- *
     *  Catch-all (500)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<String>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        logger.error("Unexpected error", ex);

        ApiResponseDTO<String> response = ApiResponseDTO.error(
                "An unexpected error occurred. Please contact support.");
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
