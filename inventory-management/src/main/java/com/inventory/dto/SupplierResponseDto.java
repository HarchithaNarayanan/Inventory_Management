package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SupplierResponseDto — Data Transfer Object for outgoing Supplier responses.
 *
 * <p>This DTO is what clients see in the API response. By using a separate
 * response DTO, we can control exactly which fields are exposed (e.g., hiding
 * internal fields or adding computed fields) without modifying the entity.</p>
 *
 * <p>Used in: GET /api/suppliers, GET /api/suppliers/{id}, POST, PUT responses</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponseDto {

    /** Database-generated primary key */
    private Long supplierId;

    /** Unique supplier identification code */
    private String supplierCode;

    /** Full name of the supplier */
    private String supplierName;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String pincode;

    /** Supplier classification: LOCAL / INTERSTATE / IMPORT */
    private String type;

    private String contactPerson;
    private String phoneNo;
    private String emailId;
    private String gstNo;

    /** Current status: ACTIVE or INACTIVE */
    private String status;

    /** Timestamp when the record was first created */
    private LocalDateTime createdAt;

    /** Timestamp of the last modification */
    private LocalDateTime updatedAt;
}
