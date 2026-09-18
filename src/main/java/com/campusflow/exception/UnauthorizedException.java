package com.campusflow.exception;

/**
 * Thrown when an operation is attempted by a user lacking necessary role privileges.
 */
public class UnauthorizedException extends CampusFlowException {
    private final String userId;
    private final String requiredRole;

    public UnauthorizedException(String message) {
        super(message);
        this.userId = null;
        this.requiredRole = null;
    }

    public UnauthorizedException(String userId, String requiredRole, String operation) {
        super(String.format("User '%s' is not authorized to perform '%s'. Required minimum role: %s",
                userId, operation, requiredRole));
        this.userId = userId;
        this.requiredRole = requiredRole;
    }

    public String getUserId() {
        return userId;
    }

    public String getRequiredRole() {
        return requiredRole;
    }
}
