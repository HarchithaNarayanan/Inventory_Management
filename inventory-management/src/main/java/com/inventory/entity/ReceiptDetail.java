package com.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ReceiptDetail — JPA entity for the {@code receipt_detail} table.
 *
 * <p>Represents the allocation of a receipt's payment amount to a specific bill.
 * One receipt can be split across multiple bills (partial payments).</p>
 *
 * <p>Example: Receipt of ₹10,000 allocated:
 * <ul>
 *   <li>Bill #101 → ₹6,000</li>
 *   <li>Bill #102 → ₹4,000</li>
 * </ul>
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "receipt_detail")
public class ReceiptDetail {

    /** Primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rec_detail_id")
    private Long recDetailId;

    /** Parent receipt */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    /** The bill this amount is being allocated against */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", nullable = false)
    private Billing billing;

    /** Amount from the receipt allocated to this specific bill */
    @Column(name = "allocated_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal allocatedAmount;
}
