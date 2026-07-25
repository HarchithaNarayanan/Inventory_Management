package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.SupplierRequestDto;
import com.inventory.dto.SupplierResponseDto;
import com.inventory.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * SupplierController — REST Controller for Supplier Master CRUD operations.
 *
 * <p>Base URL: {@code /api/suppliers}</p>
 *
 * <p>All responses are wrapped in {@link ApiResponse} for consistent structure.
 * Input validation is triggered by {@code @Valid} on the request body.</p>
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>POST   /api/suppliers          — Create a new supplier</li>
 *   <li>PUT    /api/suppliers/{id}     — Update an existing supplier</li>
 *   <li>DELETE /api/suppliers/{id}     — Delete a supplier</li>
 *   <li>GET    /api/suppliers/{id}     — Get supplier by ID</li>
 *   <li>GET    /api/suppliers          — Get all suppliers</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // ----------------------------------------------------------------
    // POST /api/suppliers — Create a new Supplier
    // ----------------------------------------------------------------

    /**
     * Creates a new supplier.
     *
     * @param requestDto the supplier data (validated by @Valid)
     * @return 201 Created with the saved supplier data
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponseDto>> createSupplier(
            @Valid @RequestBody SupplierRequestDto requestDto) {

        SupplierResponseDto savedSupplier = supplierService.createSupplier(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supplier created successfully", savedSupplier));
    }

    // ----------------------------------------------------------------
    // PUT /api/suppliers/{id} — Update an existing Supplier
    // ----------------------------------------------------------------

    /**
     * Updates an existing supplier identified by the path variable ID.
     *
     * @param id         the supplier ID from the URL path
     * @param requestDto the updated supplier data
     * @return 200 OK with updated supplier data
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponseDto>> updateSupplier(
            @PathVariable("id") Long id,
            @Valid @RequestBody SupplierRequestDto requestDto) {

        SupplierResponseDto updatedSupplier = supplierService.updateSupplier(id, requestDto);

        return ResponseEntity.ok(
                ApiResponse.success("Supplier updated successfully", updatedSupplier));
    }

    // ----------------------------------------------------------------
    // DELETE /api/suppliers/{id} — Delete a Supplier
    // ----------------------------------------------------------------

    /**
     * Deletes a supplier by ID.
     *
     * @param id the supplier ID to delete
     * @return 200 OK with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(
            @PathVariable("id") Long id) {

        supplierService.deleteSupplier(id);

        return ResponseEntity.ok(
                ApiResponse.success("Supplier deleted successfully"));
    }

    // ----------------------------------------------------------------
    // GET /api/suppliers/{id} — Get a Supplier by ID
    // ----------------------------------------------------------------

    /**
     * Retrieves a single supplier by its primary key.
     *
     * @param id the supplier ID
     * @return 200 OK with the supplier data
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponseDto>> getSupplierById(
            @PathVariable("id") Long id) {

        SupplierResponseDto supplier = supplierService.getSupplierById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Supplier retrieved successfully", supplier));
    }

    // ----------------------------------------------------------------
    // GET /api/suppliers — Get All Suppliers
    // ----------------------------------------------------------------

    /**
     * Retrieves all suppliers from the database.
     *
     * @return 200 OK with a list of all suppliers
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierResponseDto>>> getAllSuppliers() {

        List<SupplierResponseDto> suppliers = supplierService.getAllSuppliers();

        return ResponseEntity.ok(
                ApiResponse.success("Suppliers retrieved successfully", suppliers));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<SupplierResponseDto>>> getPaginatedEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success("Data retrieved successfully", 
                supplierService.getPaginatedEntities(page, size, search)));
    }
}