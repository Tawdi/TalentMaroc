package io.github.tawdi.jobboard.auth_user_service.exceptions;

import io.github.tawdi.jobboard.auth_user_service.dto.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Auth-User Service specific exception handler.
 * Extends the shared GlobalExceptionHandler for common exceptions.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler  {

    private static final Logger logger = LoggerFactory.getLogger(io.github.tawdi.jobboard.auth_user_service.exceptions.GlobalExceptionHandler.class);

    /* --------------------------------------------------------------------- *
     *  1. ResourceNotFoundException (your custom 404)
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
     *  2. Validation errors (@Valid, @Validated)
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
     *  3. Path-variable
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
     *  4. HTTP 405 – Method Not Allowed (PATCH, DELETE, etc. not supported)
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
     *  5. JSON Errors
     * --------------------------------------------------------------------- */

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleJsonError(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponseDTO.error("Invalid JSON "));
    }

    /* --------------------------------------------------------------------- *
     *  6. Status-transition violation (your business rule)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        logger.warn("Business rule violation: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }

    /* --------------------------------------------------------------------- *
     *  7. Spring MVC: No static resource / wrong URL (e.g. /historyn)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        logger.warn("No resource found for request: {} {}", request.getMethod(), request.getRequestURI());

        String message = String.format(
                "The requested endpoint '%s' does not exist. Check the URL and HTTP method.",
                request.getRequestURI()
        );

        ApiResponseDTO<String> response = ApiResponseDTO.error(message);
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /* --------------------------------------------------------------------- *
     *  8. Spring Security: Access Denied (403 Forbidden)
     * --------------------------------------------------------------------- */
    @ExceptionHandler({
            AccessDeniedException.class,
            AuthorizationDeniedException.class
    })
    public ResponseEntity<ApiResponseDTO<String>> handleAccessDenied(
            Exception ex,
            HttpServletRequest request) {

        logger.warn("Access denied for user to endpoint: {} {}",
                request.getMethod(), request.getRequestURI());
        logger.debug("Access denied details: {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(
                "You don't have permission to access this resource");
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }


    /* --------------------------------------------------------------------- *
     *  Auth-Specific: Email Already Exists (409 Conflict)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex,
            HttpServletRequest request) {

        log.warn("Email already exists: {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(ex.getMessage());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /* --------------------------------------------------------------------- *
     *  Auth-Specific: Invalid Credentials (401 Unauthorized)
     * --------------------------------------------------------------------- */
    @ExceptionHandler({
        InvalidCredentialsException.class,
        BadCredentialsException.class
    })
    public ResponseEntity<ApiResponseDTO<String>> handleInvalidCredentials(
            RuntimeException ex,
            HttpServletRequest request) {

        log.warn("Invalid credentials attempt from: {}", request.getRemoteAddr());

        ApiResponseDTO<String> response = ApiResponseDTO.error(
            "Invalid email or password"
        );
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /* --------------------------------------------------------------------- *
     *  Auth-Specific: Authentication Failed (401 Unauthorized)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        log.error("Authentication failed: {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(
            "Authentication failed. Please check your credentials."
        );
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /* --------------------------------------------------------------------- *
     *  Auth-Specific: Invalid/Expired Token (401 Unauthorized)
     * --------------------------------------------------------------------- */
    @ExceptionHandler({
        InvalidTokenException.class,
        TokenExpiredException.class
    })
    public ResponseEntity<ApiResponseDTO<String>> handleTokenException(
            RuntimeException ex,
            HttpServletRequest request) {

        log.warn("Token validation failed: {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(ex.getMessage());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /* --------------------------------------------------------------------- *
     *  Auth-Specific: Account Not Verified (403 Forbidden)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleAccountNotVerified(
            AccountNotVerifiedException ex,
            HttpServletRequest request) {

        log.warn("Unverified account login attempt: {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(
            "Account not verified. Please check your email for verification link."
        );
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /* --------------------------------------------------------------------- *
     *  Auth-Specific: User Not Found (404 Not Found)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request) {

        log.error("User not found: {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(ex.getMessage());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    /* --------------------------------------------------------------------- *
     *  ?. Catch-all for any other exception (500)
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
