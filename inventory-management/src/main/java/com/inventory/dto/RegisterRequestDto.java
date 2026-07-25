package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "Email Id is required")
    @Size(min = 3, max = 50, message = "Email Id must be between 3 and 50 characters")
    private String emailId;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Employee Name is required")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Employee Name must contain only alphabets and spaces")
    private String employeeName;

    private String role;
}
