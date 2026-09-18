package com.campusflow.exception;

/**
 * Thrown when input data fails domain or structural validation checks.
 */
public class ValidationException extends CampusFlowException {
    private final String fieldName;

    public ValidationException(String message) {
        super(message);
        this.fieldName = null;
    }

    public ValidationException(String fieldName, String message) {
        super(String.format("Validation failed on field '%s': %s", fieldName, message));
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
