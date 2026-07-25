package com.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * ReceiptRequestDto — DTO for incoming Receipt create/update requests.
 *
 * <p>Captures payment details and bill allocations.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptRequestDto {

    @NotBlank(message = "Receipt number is required")
    @Size(max = 20, message = "Receipt number must not exceed 20 characters")
    private String receiptNo;

    @NotNull(message = "Receipt date is required")
    private LocalDate receiptDate;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    /** Bank ID — required for CHEQUE / NEFT / UPI payments; null for CASH */
    private Long bankId;

    /** Payment mode: CASH / CHEQUE / NEFT / UPI */
    @NotBlank(message = "Payment mode is required")
    private String paymentMode;

    /** Required only when paymentMode = CHEQUE */
    private String chequeNo;

    /** Required only when paymentMode = CHEQUE */
    private LocalDate chequeDate;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    private String remarks;

    /**
     * List of bill allocations for this receipt.
     * The sum of allocatedAmounts should equal totalAmount.
     */
    @Valid
    private List<ReceiptDetailRequestDto> receiptDetails;

    // ----------------------------------------------------------------
    // Inner DTO for bill allocations
    // ----------------------------------------------------------------

    /**
     * ReceiptDetailRequestDto — one bill allocation entry.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiptDetailRequestDto {

        @NotNull(message = "Billing ID is required")
        private Long billingId;

        @NotNull(message = "Allocated amount is required")
        @DecimalMin(value = "0.01", message = "Allocated amount must be greater than 0")
        private BigDecimal allocatedAmount;
    }
}
