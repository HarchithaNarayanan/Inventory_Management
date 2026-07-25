package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.PurchaseRequestDto;
import com.inventory.dto.PurchaseResponseDto;
import com.inventory.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseResponseDto>> createPurchase(@Valid @RequestBody PurchaseRequestDto requestDto) {
        PurchaseResponseDto purchase = purchaseService.createPurchase(requestDto);
        return new ResponseEntity<>(
                ApiResponse.success("Purchase created successfully", purchase),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PurchaseResponseDto>>> getAllPurchases() {
        List<PurchaseResponseDto> purchases = purchaseService.getAllPurchases();
        return ResponseEntity.ok(
                ApiResponse.success("Purchases fetched successfully", purchases)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseResponseDto>> getPurchaseById(@PathVariable Long id) {
        PurchaseResponseDto purchase = purchaseService.getPurchaseById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Purchase fetched successfully", purchase)
        );
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<PurchaseResponseDto>>> getPaginatedEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success("Data retrieved successfully", 
                purchaseService.getPaginatedEntities(page, size, search)));
    }
}
