package com.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Item — JPA entity mapped to the {@code item_master} table.
 *
 * <p>Represents a product or inventory item that the company buys and sells.
 * Tracks pricing, GST, stock levels, and reorder points for inventory management.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item_master",
       uniqueConstraints = @UniqueConstraint(
               name = "uk_item_code",
               columnNames = "item_code"))
public class Item {

    /** Primary key — auto-incremented by MySQL */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    /** Unique item identification code, e.g., ITEM001 */
    @Column(name = "item_code", nullable = false, length = 20, unique = true)
    private String itemCode;

    /** Full descriptive name of the item */
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    /** Detailed description of the item (optional) */
    @Column(name = "description", length = 255)
    private String description;

    /** Item category (e.g., Electronics, Stationery, FMCG) */
    @Column(name = "category", length = 50)
    private String category;

    /** Unit of Measure */
    @Column(name = "unit_of_measure", length = 20)
    private String unitOfMeasure;

    /** Unit of Rate */
    @Column(name = "unit_of_rate", length = 20)
    private String unitOfRate;

    /** Grade */
    @Column(name = "grade", length = 50)
    private String grade;

    /** Purchase GL */
    @Column(name = "purchase_gl", length = 50)
    private String purchaseGl;

    /** Sales GL */
    @Column(name = "sales_gl", length = 50)
    private String salesGl;

    /** Auditing / Tracking */
    @Column(name = "entry_id", length = 50)
    private String entryId;

    @Column(name = "entered_by", length = 100)
    private String enteredBy;

    @Column(name = "modified_id", length = 50)
    private String modifiedId;

    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    /** Dates */
    @Column(name = "manufacture_date")
    private LocalDateTime manufactureDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "entry_date")
    private LocalDateTime entryDate;

    /** Standard purchase rate (cost price) */
    @Column(name = "purchase_rate", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal purchaseRate = BigDecimal.ZERO;

    /** Standard selling rate (MRP / sale price) */
    @Column(name = "selling_rate", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal sellingRate = BigDecimal.ZERO;

    /** GST percentage applicable (e.g., 5.00, 12.00, 18.00, 28.00) */
    @Column(name = "gst_percent", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal gstPercent = BigDecimal.ZERO;

    /**
     * Opening stock quantity at the time of onboarding.
     * Scale of 3 supports fractional quantities (e.g., 10.500 KG).
     */
    @Column(name = "opening_stock", precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal openingStock = BigDecimal.ZERO;

    /**
     * Minimum stock level below which a reorder should be triggered.
     */
    @Column(name = "reorder_level", precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal reorderLevel = BigDecimal.ZERO;

    /** Status: ACTIVE / INACTIVE */
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private String status = "ACTIVE";

    // SOFT DELETE FLAG
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    /** Automatically set on record creation */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Automatically updated on every modification */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
