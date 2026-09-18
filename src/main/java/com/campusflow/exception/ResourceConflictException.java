package com.campusflow.exception;

/**
 * Thrown when a booking request conflicts with an existing confirmed reservation or maintenance window.
 */
public class ResourceConflictException extends CampusFlowException {
    private final String resourceId;
    private final String conflictingSlotInfo;

    public ResourceConflictException(String message, String resourceId, String conflictingSlotInfo) {
        super(message);
        this.resourceId = resourceId;
        this.conflictingSlotInfo = conflictingSlotInfo;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getConflictingSlotInfo() {
        return conflictingSlotInfo;
    }
}
