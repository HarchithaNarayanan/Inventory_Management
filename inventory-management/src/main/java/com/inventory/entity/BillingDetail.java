package com.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * BillingDetail — JPA entity for the {@code billing_detail} table (Invoice Line Item).
 *
 * <p>Represents one product line in a sales bill. Each line tracks:
 * item, quantity, unit price, discount, GST, and calculated line total.</p>
 *
 * <p>Many-to-one with {@link Billing} (parent) and {@link Item}.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "billing_detail")
public class BillingDetail {

    /** Primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long detailId;

    /**
     * Parent bill reference.
     * {@code insertable = false, updatable = false} prevents JPA conflict
     * since the foreign key is managed by the billing_id column.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", nullable = false)
    private Billing billing;

    /** The item sold in this line */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /** Quantity sold */
    @Column(name = "quantity", precision = 12, scale = 3, nullable = false)
    private BigDecimal quantity;

    /** Price per unit at time of sale */
    @Column(name = "unit_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    /** Discount percentage for this line (0–100) */
    @Column(name = "discount_percent", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercent = BigDecimal.ZERO;

    /** GST percentage for this item */
    @Column(name = "gst_percent", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal gstPercent = BigDecimal.ZERO;

    /**
     * Calculated line total:
     * (quantity × unitPrice) × (1 - discountPercent/100) × (1 + gstPercent/100)
     */
    @Column(name = "line_total", precision = 15, scale = 2, nullable = false)
    private BigDecimal lineTotal;
}
