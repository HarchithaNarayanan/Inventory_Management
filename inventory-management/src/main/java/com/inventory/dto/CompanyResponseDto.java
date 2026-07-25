package com.inventory.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyResponseDto {
    private Long companyId;
    private String companyCode;
    private String companyName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String pincode;
    private String phoneNo;
    private String emailId;
    private String website;
    private String gstNo;
    private String financialYear;
    private String logoUrl;
    private String status;
}
