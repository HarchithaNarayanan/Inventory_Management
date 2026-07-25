package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransactionDto {
    private Long transactionId;

    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;

    @NotBlank(message = "Transaction type is required (CASH, BANK, JOURNAL)")
    private String transactionType;

    private String referenceNo;

    private Long bankId;
    private String bankName;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotBlank(message = "DR or CR is required")
    private String drCr;

    private String remarks;
}
