package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.ItemRequestDto;
import com.inventory.dto.ItemResponseDto;
import com.inventory.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * ItemController — REST Controller for Item Master CRUD operations.
 *
 * <p>Base URL: {@code /api/items}</p>
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>POST   /api/items          — Create a new item</li>
 *   <li>PUT    /api/items/{id}     — Update an item</li>
 *   <li>DELETE /api/items/{id}     — Delete an item</li>
 *   <li>GET    /api/items/{id}     — Get item by ID</li>
 *   <li>GET    /api/items          — Get all items</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /** POST /api/items — Create a new item */
    @PostMapping
    public ResponseEntity<ApiResponse<ItemResponseDto>> createItem(
            @Valid @RequestBody ItemRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item created successfully",
                        itemService.createItem(requestDto)));
    }

    /** PUT /api/items/{id} — Update an item */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequestDto requestDto) {
        return ResponseEntity.ok(
                ApiResponse.success("Item updated successfully",
                        itemService.updateItem(id, requestDto)));
    }

    /** DELETE /api/items/{id} — Delete an item */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.success("Item deleted successfully"));
    }

    /** GET /api/items/{id} — Get item by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Item retrieved successfully",
                        itemService.getItemById(id)));
    }

    /** GET /api/items — Get all items */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> getAllItems() {
        return ResponseEntity.ok(
                ApiResponse.success("Items retrieved successfully",
                        itemService.getAllItems()));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<ItemResponseDto>>> getPaginatedEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success("Data retrieved successfully", 
                itemService.getPaginatedEntities(page, size, search)));
    }
}
