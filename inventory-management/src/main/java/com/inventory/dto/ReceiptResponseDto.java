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
 * ReceiptResponseDto — DTO for outgoing Receipt API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptResponseDto {

    private Long receiptId;
    private String receiptNo;
    private LocalDate receiptDate;

    // Flattened customer info
    private Long customerId;
    private String customerName;

    // Flattened bank info
    private Long bankId;
    private String bankName;

    private String paymentMode;
    private String chequeNo;
    private LocalDate chequeDate;
    private BigDecimal totalAmount;
    private String remarks;

    private List<ReceiptDetailResponseDto> receiptDetails;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ----------------------------------------------------------------
    // Inner DTO for bill allocations
    // ----------------------------------------------------------------

    /**
     * ReceiptDetailResponseDto — one bill allocation in the response.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiptDetailResponseDto {
        private Long recDetailId;
        private Long billingId;
        private String billNo;
        private BigDecimal allocatedAmount;
    }
}
