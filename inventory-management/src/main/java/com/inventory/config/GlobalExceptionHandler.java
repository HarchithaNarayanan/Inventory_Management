package com.inventory.config;

import com.inventory.dto.ApiResponse;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler — Centralized exception handling for all REST controllers.
 *
 * <p>Instead of each controller catching exceptions individually, this class
 * intercepts exceptions globally using {@code @RestControllerAdvice} and returns
 * consistent {@link ApiResponse} error envelopes with appropriate HTTP status codes.</p>
 *
 * <p>Handled exceptions:</p>
 * <ul>
 *   <li>{@link ResourceNotFoundException}       → 404 Not Found</li>
 *   <li>{@link DuplicateResourceException}      → 409 Conflict</li>
 *   <li>{@link MethodArgumentNotValidException} → 400 Bad Request (validation errors)</li>
 *   <li>{@link BadCredentialsException}         → 401 Unauthorized (wrong credentials)</li>
 *   <li>{@link AccessDeniedException}           → 403 Forbidden (insufficient role)</li>
 *   <li>{@link DisabledException}               → 401 Unauthorized (account disabled)</li>
 *   <li>{@link Exception}                       → 500 Internal Server Error (fallback)</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException — returns 404 with the exception message.
     *
     * @param ex the caught exception
     * @return 404 response with error message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DuplicateResourceException — returns 409 Conflict.
     *
     * @param ex the caught exception
     * @return 409 response with error message
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(
            DuplicateResourceException ex) {

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles Bean Validation failures (@Valid annotation errors).
     *
     * <p>Collects all field-level validation errors and returns them as a map
     * where the key is the field name and the value is the error message.</p>
     *
     * @param ex the caught validation exception
     * @return 400 response with a map of field → error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // Collect each field error from the binding result
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName    = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ================================================================
    // SPRING SECURITY EXCEPTION HANDLERS
    // ================================================================

    /**
     * Handles BadCredentialsException — wrong username or password during login.
     * Returns 401 Unauthorized (not 500) so the client knows it's a credential issue.
     *
     * @param ex the caught exception
     * @return 401 response with a generic message (don't reveal which field is wrong)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        ApiResponse<Object> response = ApiResponse.error(
                "Invalid username or password. Please try again.");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles general AuthenticationException (covers DisabledException, LockedException, etc.)
     * Returns 401 Unauthorized with the exception message.
     *
     * @param ex the caught exception
     * @return 401 response
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationException ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles AccessDeniedException — authenticated user lacks required role.
     * Example: ROLE_USER trying to DELETE a record (ROLE_ADMIN only).
     * Returns 403 Forbidden (user is known but not permitted).
     *
     * @param ex the caught exception
     * @return 403 response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex) {
        ApiResponse<Object> response = ApiResponse.error(
                "Access denied. You do not have permission to perform this action.");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Fallback handler for any unhandled exception — returns 500 Internal Server Error.
     *
     * @param ex the caught exception
     * @return 500 response with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex) {
        ApiResponse<Object> response = ApiResponse.error(
                "An unexpected error occurred: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
