package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CustomerResponseDto — DTO for outgoing Customer API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {

    private Long customerId;
    private String customerCode;
    private String customerName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String pincode;
    private String contactPerson;
    private String phoneNo;
    private String emailId;
    private String gstNo;

    private String type;
    private Integer creditDays;
    private BigDecimal creditAmount;
    private BigDecimal openingBalance;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
