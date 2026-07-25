package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.BillingRequestDto;
import com.inventory.dto.BillingResponseDto;
import com.inventory.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BillingController — REST Controller for Billing (Sales Invoice) operations.
 *
 * <p>Base URL: {@code /api/billing}</p>
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>POST   /api/billing          — Create a new bill</li>
 *   <li>PUT    /api/billing/{id}     — Update a bill</li>
 *   <li>DELETE /api/billing/{id}     — Delete a bill</li>
 *   <li>GET    /api/billing/{id}     — Get bill by ID</li>
 *   <li>GET    /api/billing          — Get all bills</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/billings")
public class BillingController {

    @Autowired
    private BillingService billingService;

    /** POST /api/billing — Create a new bill */
    @PostMapping
    public ResponseEntity<ApiResponse<BillingResponseDto>> createBilling(
            @Valid @RequestBody BillingRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bill created successfully",
                        billingService.createBilling(requestDto)));
    }

    /** PUT /api/billing/{id} — Update a bill */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BillingResponseDto>> updateBilling(
            @PathVariable Long id,
            @Valid @RequestBody BillingRequestDto requestDto) {
        return ResponseEntity.ok(
                ApiResponse.success("Bill updated successfully",
                        billingService.updateBilling(id, requestDto)));
    }

    /** DELETE /api/billing/{id} — Delete a bill */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBilling(@PathVariable Long id) {
        billingService.deleteBilling(id);
        return ResponseEntity.ok(ApiResponse.success("Bill deleted successfully"));
    }

    /** GET /api/billing/{id} — Get bill by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BillingResponseDto>> getBillingById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Bill retrieved successfully",
                        billingService.getBillingById(id)));
    }

    /** GET /api/billing — Get all bills */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BillingResponseDto>>> getAllBillings() {
        return ResponseEntity.ok(
                ApiResponse.success("Bills retrieved successfully",
                        billingService.getAllBillings()));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<BillingResponseDto>>> getPaginatedEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success("Data retrieved successfully", 
                billingService.getPaginatedEntities(page, size, search)));
    }
}
