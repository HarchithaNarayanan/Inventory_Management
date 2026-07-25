package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponseDto {
    private Long purchaseId;
    private String billNo;
    private LocalDate billDate;
    
    private Long supplierId;
    private String supplierCode;
    private String supplierName;

    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
    private String remarks;

    private List<PurchaseDetailResponseDto> purchaseDetails;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseDetailResponseDto {
        private Long detailId;
        private Long itemId;
        private String itemCode;
        private String itemName;
        
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal gstPercent;
        private BigDecimal lineTotal;
    }
}
