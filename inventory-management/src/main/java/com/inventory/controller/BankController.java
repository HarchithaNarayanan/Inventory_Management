package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.BankRequestDto;
import com.inventory.dto.BankResponseDto;
import com.inventory.service.BankService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BankController — REST Controller for Bank Master CRUD operations.
 *
 * <p>Base URL: {@code /api/banks}</p>
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>POST   /api/banks          — Create a new bank account</li>
 *   <li>PUT    /api/banks/{id}     — Update a bank account</li>
 *   <li>DELETE /api/banks/{id}     — Delete a bank account</li>
 *   <li>GET    /api/banks/{id}     — Get bank by ID</li>
 *   <li>GET    /api/banks          — Get all banks</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/banks")
public class BankController {

    @Autowired
    private BankService bankService;

    /** POST /api/banks — Create a bank */
    @PostMapping
    public ResponseEntity<ApiResponse<BankResponseDto>> createBank(
            @Valid @RequestBody BankRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bank created successfully",
                        bankService.createBank(requestDto)));
    }

    /** PUT /api/banks/{id} — Update a bank */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BankResponseDto>> updateBank(
            @PathVariable Long id,
            @Valid @RequestBody BankRequestDto requestDto) {
        return ResponseEntity.ok(
                ApiResponse.success("Bank updated successfully",
                        bankService.updateBank(id, requestDto)));
    }

    /** DELETE /api/banks/{id} — Delete a bank */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBank(@PathVariable Long id) {
        bankService.deleteBank(id);
        return ResponseEntity.ok(ApiResponse.success("Bank deleted successfully"));
    }

    /** GET /api/banks/{id} — Get bank by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BankResponseDto>> getBankById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Bank retrieved successfully",
                        bankService.getBankById(id)));
    }

    /** GET /api/banks — Get all banks */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BankResponseDto>>> getAllBanks() {
        return ResponseEntity.ok(
                ApiResponse.success("Banks retrieved successfully",
                        bankService.getAllBanks()));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<BankResponseDto>>> getPaginatedEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success("Data retrieved successfully", 
                bankService.getPaginatedEntities(page, size, search)));
    }
}
