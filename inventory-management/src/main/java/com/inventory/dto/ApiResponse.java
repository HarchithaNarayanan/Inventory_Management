package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ApiResponse — Generic envelope wrapper for all REST API responses.
 *
 * <p>Every controller returns this wrapper so that clients receive a consistent
 * JSON structure regardless of the operation:</p>
 * <pre>
 * {
 *   "success": true,
 *   "message": "Supplier created successfully",
 *   "data": { ... },
 *   "timestamp": "2026-06-19T09:00:00"
 * }
 * </pre>
 *
 * @param <T> the type of the response payload (e.g., SupplierResponseDto, List, etc.)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** Indicates whether the operation succeeded */
    private boolean success;

    /** Human-readable message describing the result */
    private String message;

    /** The actual response payload */
    private T data;

    /** Server-side timestamp of the response */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // ----------------------------------------------------------------
    // Static factory methods for convenience
    // ----------------------------------------------------------------

    /**
     * Creates a successful response with data and a custom message.
     *
     * @param message descriptive success message
     * @param data    the response payload
     * @param <T>     type of the payload
     * @return ApiResponse with success=true
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response with only a message (no data payload).
     *
     * @param message descriptive success message
     * @param <T>     type of the payload
     * @return ApiResponse with success=true and null data
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a failure response with an error message.
     *
     * @param message error message
     * @param <T>     type of the payload
     * @return ApiResponse with success=false
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
