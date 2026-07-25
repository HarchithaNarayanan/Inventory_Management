package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDto {

    @NotBlank(message = "Bill number is required")
    private String billNo;

    @NotNull(message = "Bill date is required")
    private LocalDate billDate;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private String remarks;

    @NotNull(message = "Purchase details cannot be null")
    private List<PurchaseDetailDto> purchaseDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseDetailDto {
        @NotNull(message = "Item ID is required")
        private Long itemId;

        @NotNull(message = "Quantity is required")
        private BigDecimal quantity;

        @NotNull(message = "Unit price is required")
        private BigDecimal unitPrice;

        private BigDecimal gstPercent;
    }
}
