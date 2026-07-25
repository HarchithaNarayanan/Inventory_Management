package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.ReceiptRequestDto;
import com.inventory.dto.ReceiptResponseDto;
import com.inventory.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ReceiptController — REST Controller for Customer Payment Receipt operations.
 *
 * <p>Base URL: {@code /api/receipts}</p>
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>POST   /api/receipts          — Create a new receipt</li>
 *   <li>PUT    /api/receipts/{id}     — Update a receipt</li>
 *   <li>DELETE /api/receipts/{id}     — Delete a receipt</li>
 *   <li>GET    /api/receipts/{id}     — Get receipt by ID</li>
 *   <li>GET    /api/receipts          — Get all receipts</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    /** POST /api/receipts — Create a new receipt */
    @PostMapping
    public ResponseEntity<ApiResponse<ReceiptResponseDto>> createReceipt(
            @Valid @RequestBody ReceiptRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Receipt created successfully",
                        receiptService.createReceipt(requestDto)));
    }

    /** PUT /api/receipts/{id} — Update a receipt */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReceiptResponseDto>> updateReceipt(
            @PathVariable Long id,
            @Valid @RequestBody ReceiptRequestDto requestDto) {
        return ResponseEntity.ok(
                ApiResponse.success("Receipt updated successfully",
                        receiptService.updateReceipt(id, requestDto)));
    }

    /** DELETE /api/receipts/{id} — Delete a receipt */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return ResponseEntity.ok(ApiResponse.success("Receipt deleted successfully"));
    }

    /** GET /api/receipts/{id} — Get receipt by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReceiptResponseDto>> getReceiptById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Receipt retrieved successfully",
                        receiptService.getReceiptById(id)));
    }

    /** GET /api/receipts — Get all receipts */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReceiptResponseDto>>> getAllReceipts() {
        return ResponseEntity.ok(
                ApiResponse.success("Receipts retrieved successfully",
                        receiptService.getAllReceipts()));
    }
}
