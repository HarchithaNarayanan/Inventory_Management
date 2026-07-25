package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.CompanyRequestDto;
import com.inventory.dto.CompanyResponseDto;
import com.inventory.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponseDto>> createCompany(@Valid @RequestBody CompanyRequestDto request) {
        CompanyResponseDto created = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyResponseDto>>> getAllCompanies() {
        return ResponseEntity.ok(ApiResponse.success("Companies fetched successfully", companyService.getAllCompanies()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Company fetched successfully", companyService.getCompanyById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> updateCompany(
            @PathVariable Long id, @Valid @RequestBody CompanyRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Company updated successfully", companyService.updateCompany(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(ApiResponse.success("Company deleted successfully", null));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<CompanyResponseDto>>> getPaginatedEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success("Data retrieved successfully", 
                companyService.getPaginatedEntities(page, size, search)));
    }
}
