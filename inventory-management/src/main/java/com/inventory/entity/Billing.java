package com.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Billing — JPA entity for the {@code billing_master} table (Invoice Header).
 *
 * <p>Represents one sales bill/invoice issued to a customer.
 * Has a one-to-many relationship with {@link BillingDetail} for line items.</p>
 *
 * <p>{@code CascadeType.ALL} ensures that when a bill is saved/deleted,
 * all its line items are also saved/deleted automatically.</p>
 *
 * <p>{@code orphanRemoval = true} removes line items from DB if they are
 * removed from the Java list (prevents orphaned records).</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "billing_master",
       uniqueConstraints = @UniqueConstraint(name = "uk_bill_no", columnNames = "bill_no"))
public class Billing {

    /** Primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billing_id")
    private Long billingId;

    /** Unique invoice/bill number (e.g., BILL-2026-001) */
    @Column(name = "bill_no", nullable = false, length = 20, unique = true)
    private String billNo;

    /** Date of the invoice */
    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    /**
     * The customer for this bill.
     * {@code @ManyToOne} — many bills can belong to one customer.
     * {@code LAZY} fetch means customer data is only loaded when accessed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /** Sum of all line item totals (before discount and tax) */
    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /** Discount applied on the total (optional) */
    @Column(name = "discount_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /** Tax/GST amount */
    @Column(name = "tax_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    /** Final payable amount: totalAmount - discountAmount + taxAmount */
    @Column(name = "net_amount", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal netAmount = BigDecimal.ZERO;

    /** Payment status: PENDING / PARTIAL / PAID */
    @Column(name = "payment_status", length = 20)
    @Builder.Default
    private String paymentStatus = "PENDING";

    /** Optional remarks or notes */
    @Column(name = "remarks", length = 255)
    private String remarks;

    /**
     * The line items of this bill.
     * CascadeType.ALL: saves/deletes details with the parent.
     * orphanRemoval: removes line items deleted from this list.
     */
    @OneToMany(mappedBy = "billing", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BillingDetail> billingDetails = new ArrayList<>();

    /** Automatically set on creation */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Automatically updated on modification */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
