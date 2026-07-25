package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ItemResponseDto — DTO for outgoing Item API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {

    private Long itemId;
    private String itemCode;
    private String itemName;
    private String description;
    private String category;
    private String unitOfMeasure;
    private String unitOfRate;
    private String grade;
    private String purchaseGl;
    private String salesGl;
    private String entryId;
    private String enteredBy;
    private String modifiedId;
    private String modifiedBy;
    private LocalDateTime manufactureDate;
    private LocalDateTime expiryDate;
    private LocalDateTime entryDate;
    private BigDecimal purchaseRate;
    private BigDecimal sellingRate;
    private BigDecimal gstPercent;
    private BigDecimal openingStock;
    private BigDecimal reorderLevel;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
