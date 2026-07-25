package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * BankResponseDto — DTO for outgoing Bank API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankResponseDto {

    private Long bankId;
    private String bankCode;
    private String bankName;
    private String accountNo;
    private String accountType;
    private String ifscCode;
    private String bankBranch;
    private BigDecimal limitAmount;
    private String glCode;
    private BigDecimal openingBalance;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
