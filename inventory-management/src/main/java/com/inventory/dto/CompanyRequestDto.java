package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRequestDto {

    @NotBlank(message = "Company code is required")
    private String companyCode;

    @NotBlank(message = "Company name is required")
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
