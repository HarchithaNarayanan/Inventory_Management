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
 * Customer — JPA entity mapped to the {@code customer_master} table.
 *
 * <p>Represents a buyer/client who purchases goods from the company.
 * Includes credit limit and opening balance for financial tracking.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer_master",
       uniqueConstraints = @UniqueConstraint(
               name = "uk_customer_code",
               columnNames = "customer_code"))
public class Customer {

    /** Primary key — auto-incremented by MySQL */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    /** Unique short code for the customer, e.g., CUST001 */
    @Column(name = "customer_code", nullable = false, length = 20, unique = true)
    private String customerCode;

    /** Full name of the customer */
    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    /** First line of billing address */
    @Column(name = "address_line1", length = 200)
    private String addressLine1;

    /** Second line of billing address (optional) */
    @Column(name = "address_line2", length = 200)
    private String addressLine2;

    /** City */
    @Column(name = "city", length = 50)
    private String city;

    /** PIN / ZIP code */
    @Column(name = "pincode", length = 10)
    private String pincode;

    /** Primary contact person at the customer */
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    /** Contact phone number */
    @Column(name = "phone_no", length = 15)
    private String phoneNo;

    /** Contact email address */
    @Column(name = "email_id", length = 100)
    private String emailId;

    /** GST Registration Number */
    @Column(name = "gst_no", length = 20)
    private String gstNo;

    /** Type: LOCAL / INTERSTATE / IMPORT */
    @Column(name = "type", length = 30)
    private String type;

    /** Credit Days */
    @Column(name = "credit_days")
    private Integer creditDays;

    /**
     * Maximum credit limit extended to this customer.
     * Precision: 15 total digits, 2 decimal places.
     */
    @Column(name = "credit_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal creditAmount = BigDecimal.ZERO;

    /**
     * Opening balance (outstanding amount at the time of data entry).
     * Positive = customer owes money; Negative = advance received.
     */
    @Column(name = "opening_balance", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal openingBalance = BigDecimal.ZERO;

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
