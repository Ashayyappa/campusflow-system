package com.campusflow.model;

/**
 * Enumeration representing campus facility categories.
 */
public enum ResourceType {
    AUDITORIUM("Auditorium", 500),
    SEMINAR_HALL("Seminar Hall", 150),
    COMPUTER_LAB("Computer Lab", 60),
    SMART_CLASSROOM("Smart Classroom", 80),
    SPORTS_COMPLEX("Sports Complex", 200);

    private final String label;
    private final int defaultCapacity;

    ResourceType(String label, int defaultCapacity) {
        this.label = label;
        this.defaultCapacity = defaultCapacity;
    }

    public String getLabel() {
        return label;
    }

    public int getDefaultCapacity() {
        return defaultCapacity;
    }
}
