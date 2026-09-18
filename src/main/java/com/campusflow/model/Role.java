package com.campusflow.model;

/**
 * Enumeration representing user access roles within CampusFlow.
 */
public enum Role {
    STUDENT(1, "Student"),
    FACULTY(2, "Faculty Member"),
    ADMIN(3, "System Administrator");

    private final int accessLevel;
    private final String displayName;

    Role(int accessLevel, String displayName) {
        this.accessLevel = accessLevel;
        this.displayName = displayName;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public String getDisplayName() {
        return displayName;
    }
}
