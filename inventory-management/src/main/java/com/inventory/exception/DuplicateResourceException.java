package com.inventory.exception;

/**
 * DuplicateResourceException — thrown when a unique constraint is violated.
 *
 * <p>For example, trying to create a Supplier with a code that already exists.
 * Caught by {@link com.inventory.config.GlobalExceptionHandler} and returns HTTP 409 Conflict.</p>
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * @param resourceName name of the entity (e.g., "Supplier")
     * @param fieldName    the field with the duplicate value (e.g., "supplierCode")
     * @param fieldValue   the duplicate value (e.g., "SUP001")
     */
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
