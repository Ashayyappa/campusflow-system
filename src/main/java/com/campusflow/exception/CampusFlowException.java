package com.campusflow.exception;

/**
 * Base checked exception for all domain errors within CampusFlow.
 */
public class CampusFlowException extends Exception {
    public CampusFlowException(String message) {
        super(message);
    }

    public CampusFlowException(String message, Throwable cause) {
        super(message, cause);
    }
}
