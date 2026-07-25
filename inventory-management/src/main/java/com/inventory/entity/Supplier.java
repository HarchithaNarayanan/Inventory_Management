package com.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Supplier — JPA entity mapped to the {@code supplier_master} table.
 */
@Data                   
@Builder                
@NoArgsConstructor      
@AllArgsConstructor     
@Entity
@Table(name = "supplier_master",
       uniqueConstraints = @UniqueConstraint(
               name = "uk_supplier_code",
               columnNames = "supplier_code"))
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "supplier_code", nullable = false, length = 20, unique = true)
    private String supplierCode;

    @Column(name = "supplier_name", nullable = false, length = 100)
    private String supplierName;

    @Column(name = "address_line1", length = 200)
    private String addressLine1;

    @Column(name = "address_line2", length = 200)
    private String addressLine2;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "pincode", length = 10)
    private String pincode;

    @Column(name = "type", length = 30)
    private String type;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "phone_no", length = 15)
    private String phoneNo;

    @Column(name = "email_id", length = 100)
    private String emailId;

    @Column(name = "gst_no", length = 20)
    private String gstNo;

    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private String status = "ACTIVE";

    // SOFT DELETE FLAG
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}