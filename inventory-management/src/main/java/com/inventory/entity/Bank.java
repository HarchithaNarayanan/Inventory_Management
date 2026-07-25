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
 * Bank — JPA entity mapped to the {@code bank_master} table.
 *
 * <p>Represents a company bank account used for payment receipts.
 * Used in receipts to track which bank account received a payment.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bank_master",
       uniqueConstraints = {
               @UniqueConstraint(name = "uk_bank_code",      columnNames = "bank_code"),
               @UniqueConstraint(name = "uk_bank_account",   columnNames = "account_no")
       })
public class Bank {

    /** Primary key — auto-incremented by MySQL */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_id")
    private Long bankId;

    /** Unique short code for the bank account, e.g., HDFC001 */
    @Column(name = "bank_code", nullable = false, length = 20, unique = true)
    private String bankCode;

    /** Name of the bank (e.g., HDFC Bank, SBI) */
    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    /** Bank account number (must be unique) */
    @Column(name = "account_no", nullable = false, length = 30, unique = true)
    private String accountNo;

    /** Account type: CURRENT / SAVINGS */
    @Column(name = "account_type", length = 20)
    private String accountType;

    /** IFSC code for electronic transfers (e.g., HDFC0001234) */
    @Column(name = "ifsc_code", length = 15)
    private String ifscCode;

    /** Branch name */
    @Column(name = "bank_branch", length = 100)
    private String bankBranch;

    /** Limit Amount */
    @Column(name = "limit_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal limitAmount = BigDecimal.ZERO;

    /** GL Code */
    @Column(name = "gl_code", length = 50)
    private String glCode;

    /** Opening balance at the time of data entry */
    @Column(name = "opening_balance", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal openingBalance = BigDecimal.ZERO;

    /** Status: ACTIVE / INACTIVE */
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private String status = "ACTIVE";

    /** Automatically set on record creation */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Automatically updated on every modification */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
