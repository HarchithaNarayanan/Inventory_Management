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
 * BillingRequestDto — DTO for incoming Billing (Invoice) create/update requests.
 *
 * <p>Contains header-level fields and a list of line item DTOs.
 * The {@code @Valid} on billingDetails triggers nested validation on each line.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingRequestDto {

    @NotBlank(message = "Bill number is required")
    @Size(max = 20, message = "Bill number must not exceed 20 characters")
    private String billNo;

    @NotNull(message = "Bill date is required")
    private LocalDate billDate;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private BigDecimal discountAmount;
    private String paymentStatus;
    private String remarks;

    /**
     * List of billing line items.
     * Must contain at least one item.
     */
    @NotNull(message = "Billing details are required")
    @Size(min = 1, message = "At least one billing detail item is required")
    @Valid
    private List<BillingDetailRequestDto> billingDetails;

    // ----------------------------------------------------------------
    // Inner DTO for line items
    // ----------------------------------------------------------------

    /**
     * BillingDetailRequestDto — represents one line item in the invoice.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingDetailRequestDto {

        @NotNull(message = "Item ID is required")
        private Long itemId;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.001", message = "Quantity must be greater than 0")
        private BigDecimal quantity;

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
        private BigDecimal unitPrice;

        @DecimalMin(value = "0.0", message = "Discount cannot be negative")
        @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%")
        private BigDecimal discountPercent;

        @DecimalMin(value = "0.0", message = "GST cannot be negative")
        @DecimalMax(value = "100.0", message = "GST cannot exceed 100%")
        private BigDecimal gstPercent;
    }
}
