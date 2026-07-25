package com.inventory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SupplierRequestDto — Data Transfer Object for incoming Supplier create/update requests.
 *
 * <p>This DTO decouples the HTTP request body from the {@link com.inventory.entity.Supplier}
 * JPA entity. Bean Validation annotations ensure data quality before it reaches the service layer.</p>
 *
 * <p>Used in: POST /api/suppliers and PUT /api/suppliers/{id}</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequestDto {

    /**
     * Unique supplier code (mandatory, max 20 chars).
     * Example: "SUP001"
     */
    @NotBlank(message = "Supplier code is required")
    @Size(max = 20, message = "Supplier code must not exceed 20 characters")
    private String supplierCode;

    /**
     * Full name of the supplier (mandatory, max 100 chars).
     */
    @NotBlank(message = "Supplier name is required")
    @Size(max = 100, message = "Supplier name must not exceed 100 characters")
    private String supplierName;

    /** First address line (optional) */
    private String addressLine1;

    /** Second address line (optional) */
    private String addressLine2;

    /** City name (optional) */
    private String city;

    /** PIN code — must be exactly 6 digits if provided */
    @Pattern(regexp = "^$|^[0-9]{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    /** Type: LOCAL / INTERSTATE / IMPORT */
    private String type;

    /** Contact person name (optional) */
    private String contactPerson;

    /** Phone number (optional) */
    private String phoneNo;

    /** Email address — validated format if provided */
    @Email(message = "Please provide a valid email address")
    private String emailId;

    /** GST registration number (optional) */
    private String gstNo;

    /**
     * Status of the supplier: ACTIVE or INACTIVE.
     * Defaults to ACTIVE if not provided.
     */
    @Builder.Default
    private String status = "ACTIVE";
}
