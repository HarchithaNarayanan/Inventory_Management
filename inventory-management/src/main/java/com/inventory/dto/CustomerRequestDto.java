package com.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * CustomerRequestDto — DTO for incoming Customer create/update requests.
 * Bean Validation ensures data integrity before the service layer processes it.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDto {

    @NotBlank(message = "Customer code is required")
    @Size(max = 20, message = "Customer code must not exceed 20 characters")
    private String customerCode;

    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;

    private String addressLine1;
    private String addressLine2;
    private String city;

    @Pattern(regexp = "^$|^[0-9]{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    private String contactPerson;
    private String phoneNo;

    @Email(message = "Please provide a valid email address")
    private String emailId;

    private String gstNo;

    private String type;

    private Integer creditDays;

    private BigDecimal creditAmount;



    /** Opening balance at the time of onboarding */
    private BigDecimal openingBalance;

    @Builder.Default
    private String status = "ACTIVE";
}
