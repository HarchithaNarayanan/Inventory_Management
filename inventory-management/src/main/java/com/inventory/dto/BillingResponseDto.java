package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * BillingResponseDto — DTO for outgoing Billing API responses.
 * Includes header data plus all line items as nested DTOs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingResponseDto {

    private Long billingId;
    private String billNo;
    private LocalDate billDate;

    // Customer info (flattened — no need to expose full Customer object)
    private Long customerId;
    private String customerName;

    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
    private String paymentStatus;
    private String remarks;

    private List<BillingDetailResponseDto> billingDetails;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ----------------------------------------------------------------
    // Inner DTO for line items
    // ----------------------------------------------------------------

    /**
     * BillingDetailResponseDto — one line item in the billing response.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingDetailResponseDto {
        private Long detailId;
        private Long itemId;
        private String itemName;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal discountPercent;
        private BigDecimal gstPercent;
        private BigDecimal lineTotal;
    }
}
