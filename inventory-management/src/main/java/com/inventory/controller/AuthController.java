package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.AuthResponseDto;
import com.inventory.dto.LoginRequestDto;
import com.inventory.dto.RegisterRequestDto;
import com.inventory.entity.User;
import com.inventory.repository.UserRepository;
import com.inventory.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AuthController — REST Controller for authentication endpoints.
 *
 * <p>Base URL: {@code /api/auth}</p>
 *
 * <p>These endpoints are declared PUBLIC in {@link com.inventory.config.SecurityConfig}
 * ({@code .requestMatchers("/api/auth/**").permitAll()}) — no JWT token required,
 * EXCEPT for {@code GET /api/auth/users} which requires ROLE_ADMIN.</p>
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>POST /api/auth/register — Create a new user account</li>
 *   <li>POST /api/auth/login    — Authenticate and receive JWT token</li>
 *   <li>GET  /api/auth/users   — List all users (Admin only)</li>
 * </ul>
 *
 * <p>{@code @RestController} = {@code @Controller} + {@code @ResponseBody}.
 * All methods automatically serialize return values to JSON.</p>
 *
 * <p>{@code @RequestMapping("/api/auth")} sets the base URL path.
 * All method-level mapping paths are relative to this.</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    // ================================================================
    // REGISTER
    // POST /api/auth/register
    // ================================================================

    /**
     * Registers a new user and returns a JWT token (auto-login after register).
     *
     * <p>{@code @Valid} triggers Bean Validation on the request body:
     * if any {@code @NotBlank}, {@code @Pattern}, or {@code @Size} constraint fails,
     * Spring returns 400 Bad Request with validation error details
     * (handled by {@link com.inventory.config.GlobalExceptionHandler}).</p>
     *
     * <p>{@code @RequestBody} deserializes the JSON request body into
     * a {@link RegisterRequestDto} object.</p>
     *
     * <p>Returns HTTP 201 Created on success — appropriate for resource creation.</p>
     *
     * @param requestDto the registration payload (validated by {@code @Valid})
     * @return 201 Created with JWT token and user info
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDto>> register(
            @Valid @RequestBody RegisterRequestDto requestDto) {

        AuthResponseDto authResponse = authService.register(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", authResponse));
    }

    // ================================================================
    // LOGIN
    // POST /api/auth/login
    // ================================================================

    /**
     * Authenticates a user and returns a JWT token.
     *
     * <p>On success: returns 200 OK with the JWT in the response body.
     * The client must store this token and include it in all subsequent
     * requests as: {@code Authorization: Bearer <token>}</p>
     *
     * <p>On failure:</p>
     * <ul>
     *   <li>Wrong password / unknown user → Spring throws BadCredentialsException
     *       → {@link com.inventory.config.GlobalExceptionHandler} maps to 401 Unauthorized</li>
     *   <li>Empty fields → validation fails → 400 Bad Request</li>
     * </ul>
     *
     * @param requestDto the login credentials (username + password)
     * @return 200 OK with JWT token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(
            @Valid @RequestBody LoginRequestDto requestDto) {

        AuthResponseDto authResponse = authService.login(requestDto);
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", authResponse));
    }

    // ================================================================
    // GET ALL USERS (Admin only)
    // GET /api/auth/users
    // ================================================================

    /**
     * Returns a list of all registered users.
     *
     * <p>Access restricted to ROLE_ADMIN via SecurityConfig.</p>
     * <p>Password is never included in the response — only safe fields are exposed.</p>
     *
     * @return 200 OK with list of {@link UserSummaryDto}
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserSummaryDto>>> getAllUsers() {
        List<UserSummaryDto> users = userRepository.findAll().stream()
                .map(UserSummaryDto::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    // ================================================================
    // INNER DTO — safe user summary (no password)
    // ================================================================

    /**
     * Lightweight read-only DTO returned by {@code GET /api/auth/users}.
     * Deliberately omits the password hash.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDto {
        private Long id;
        private String username;       // emailId used as username
        private String employeeName;
        private String role;
        private boolean isActive;

        public static UserSummaryDto fromUser(User user) {
            return UserSummaryDto.builder()
                    .id(user.getUserId())
                    .username(user.getEmailId())
                    .employeeName(user.getEmployeeName())
                    .role(user.getRole() != null ? user.getRole().name() : null)
                    .isActive(user.isActive())
                    .build();
        }
    }
}


