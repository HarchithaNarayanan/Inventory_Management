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
 * Receipt — JPA entity for the {@code receipt_master} table (Payment Receipt Header).
 *
 * <p>Represents a payment received from a customer.
 * Has a one-to-many relationship with {@link ReceiptDetail} for bill allocation.</p>
 *
 * <p>Payment can be received via CASH, CHEQUE, NEFT, or UPI.
 * When CHEQUE, the cheque number and date are recorded.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "receipt_master",
       uniqueConstraints = @UniqueConstraint(name = "uk_receipt_no", columnNames = "receipt_no"))
public class Receipt {

    /** Primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long receiptId;

    /** Unique receipt number (e.g., REC-2026-001) */
    @Column(name = "receipt_no", nullable = false, length = 20, unique = true)
    private String receiptNo;

    /** Date the payment was received */
    @Column(name = "receipt_date", nullable = false)
    private LocalDate receiptDate;

    /** The customer who made the payment */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /**
     * Bank account where the payment was deposited.
     * Null for CASH payments (no bank involvement).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    /** Payment mode: CASH / CHEQUE / NEFT / UPI */
    @Column(name = "payment_mode", length = 20)
    private String paymentMode;

    /** Cheque number (only for CHEQUE payments) */
    @Column(name = "cheque_no", length = 20)
    private String chequeNo;

    /** Cheque date (only for CHEQUE payments) */
    @Column(name = "cheque_date")
    private LocalDate chequeDate;

    /** Total amount received */
    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /** Optional notes or remarks */
    @Column(name = "remarks", length = 255)
    private String remarks;

    /**
     * Bill allocation details — which bills this receipt amount was applied to.
     */
    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReceiptDetail> receiptDetails = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
