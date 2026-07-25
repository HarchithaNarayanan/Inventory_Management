package com.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * BankRequestDto — DTO for incoming Bank create/update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankRequestDto {

    @NotBlank(message = "Bank code is required")
    @Size(max = 20, message = "Bank code must not exceed 20 characters")
    private String bankCode;

    @NotBlank(message = "Bank name is required")
    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    private String bankName;

    @NotBlank(message = "Account number is required")
    @Size(max = 30, message = "Account number must not exceed 30 characters")
    private String accountNo;

    /** CURRENT or SAVINGS */
    private String accountType;

    /** IFSC code format: 4 letters + 0 + 6 alphanumeric */
    @Pattern(regexp = "^$|^[A-Z]{4}0[A-Z0-9]{6}$",
             message = "IFSC code format is invalid (e.g., HDFC0001234)")
    private String ifscCode;

    private String bankBranch;

    private BigDecimal limitAmount;

    private String glCode;

    @DecimalMin(value = "0.0", message = "Opening balance cannot be negative")
    private BigDecimal openingBalance;

    @Builder.Default
    private String status = "ACTIVE";
}
