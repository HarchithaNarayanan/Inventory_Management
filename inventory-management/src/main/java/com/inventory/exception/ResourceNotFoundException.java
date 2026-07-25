package com.inventory.exception;

/**
 * ResourceNotFoundException — thrown when a requested entity is not found in the database.
 *
 * <p>This exception is caught by {@link com.inventory.config.GlobalExceptionHandler}
 * and automatically returns HTTP 404 Not Found to the client.</p>
 *
 * <p>Usage example:
 * <pre>
 *     Supplier supplier = supplierRepository.findById(id)
 *         .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
 * </pre>
 * </p>
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Name of the entity/resource that was not found (e.g., "Supplier", "Customer").
     */
    private final String resourceName;

    /**
     * The field name used for the lookup (e.g., "id", "supplierCode").
     */
    private final String fieldName;

    /**
     * The value that was searched (e.g., 42L, "SUP001").
     */
    private final Object fieldValue;

    /**
     * Constructs a ResourceNotFoundException with a descriptive message.
     *
     * @param resourceName name of the entity (e.g., "Supplier")
     * @param fieldName    the field used for lookup (e.g., "id")
     * @param fieldValue   the value that was searched (e.g., 1L)
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName    = fieldName;
        this.fieldValue   = fieldValue;
    }

    public String getResourceName() { return resourceName; }
    public String getFieldName()    { return fieldName; }
    public Object getFieldValue()   { return fieldValue; }
}
