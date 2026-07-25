package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.CustomerRequestDto;
import com.inventory.dto.CustomerResponseDto;
import com.inventory.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * CustomerController — REST Controller for Customer Master CRUD operations.
 *
 * <p>Base URL: {@code /api/customers}</p>
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>POST   /api/customers          — Create a new customer</li>
 *   <li>PUT    /api/customers/{id}     — Update an existing customer</li>
 *   <li>DELETE /api/customers/{id}     — Delete a customer</li>
 *   <li>GET    /api/customers/{id}     — Get customer by ID</li>
 *   <li>GET    /api/customers          — Get all customers</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /** POST /api/customers — Create a new customer */
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDto>> createCustomer(
            @Valid @RequestBody CustomerRequestDto requestDto) {
        CustomerResponseDto saved = customerService.createCustomer(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created successfully", saved));
    }

    /** PUT /api/customers/{id} — Update customer */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto requestDto) {
        return ResponseEntity.ok(
                ApiResponse.success("Customer updated successfully",
                        customerService.updateCustomer(id, requestDto)));
    }

    /** DELETE /api/customers/{id} — Delete customer */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully"));
    }

    /** GET /api/customers/{id} — Get customer by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Customer retrieved successfully",
                        customerService.getCustomerById(id)));
    }

    /** GET /api/customers — Get all customers */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponseDto>>> getAllCustomers() {
        return ResponseEntity.ok(
                ApiResponse.success("Customers retrieved successfully",
                        customerService.getAllCustomers()));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<CustomerResponseDto>>> getPaginatedEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success("Data retrieved successfully", 
                customerService.getPaginatedEntities(page, size, search)));
    }
}
