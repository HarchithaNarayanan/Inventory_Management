package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginRequestDto — DTO for the POST /api/auth/login request body.
 *
 * <p>Deliberately minimal — only username and password are needed for login.
 * Keeping it simple reduces the attack surface and makes the API intuitive.</p>
 *
 * <p>Both fields use {@code @NotBlank} so Spring's {@code @Valid} annotation
 * in the controller immediately rejects empty submissions with a 400 Bad Request
 * before the service layer is ever called.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    /**
     * The username to authenticate.
     * {@code @NotBlank} rejects null/empty/whitespace — validated before controller logic runs.
     */
    @NotBlank(message = "Email Id is required")
    private String emailId;

    /**
     * The plain-text password to verify against the stored BCrypt hash.
     * This value is never logged or stored — it is only compared and discarded.
     */
    @NotBlank(message = "Password is required")
    private String password;
}
