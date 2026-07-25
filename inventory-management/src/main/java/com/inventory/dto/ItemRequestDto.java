package com.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ItemRequestDto — DTO for incoming Item create/update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    @NotBlank(message = "Item code is required")
    @Size(max = 20, message = "Item code must not exceed 20 characters")
    private String itemCode;

    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name must not exceed 100 characters")
    private String itemName;

    private String description;
    private String category;

    /** Unit of Measure */
    private String unitOfMeasure;
    private String unitOfRate;
    private String grade;
    private String purchaseGl;
    private String salesGl;
    private String entryId;
    private String enteredBy;
    private String modifiedId;
    private String modifiedBy;
    private String manufactureDate; // ISO format string
    private String expiryDate; // ISO format string
    private String entryDate; // ISO format string

    @DecimalMin(value = "0.0", message = "Purchase rate cannot be negative")
    private BigDecimal purchaseRate;

    @DecimalMin(value = "0.0", message = "Selling rate cannot be negative")
    private BigDecimal sellingRate;

    /** GST % must be between 0 and 100 */
    @DecimalMin(value = "0.0", message = "GST percent cannot be negative")
    @DecimalMax(value = "100.0", message = "GST percent cannot exceed 100")
    private BigDecimal gstPercent;

    @DecimalMin(value = "0.0", message = "Opening stock cannot be negative")
    private BigDecimal openingStock;

    @DecimalMin(value = "0.0", message = "Reorder level cannot be negative")
    private BigDecimal reorderLevel;

    @Builder.Default
    private String status = "ACTIVE";
}
