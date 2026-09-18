package com.campusflow.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a physical or shared resource available on campus.
 */
public class Facility implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;
    private ResourceType type;
    private int capacity;
    private String buildingBlock;
    private boolean underMaintenance;
    private double hourlyMaintenanceCost;

    public Facility(String id, String name, ResourceType type, int capacity, String buildingBlock, double hourlyMaintenanceCost) {
        this.id = Objects.requireNonNull(id, "Facility ID is required");
        this.name = Objects.requireNonNull(name, "Facility name is required");
        this.type = Objects.requireNonNull(type, "Resource type is required");
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        this.capacity = capacity;
        this.buildingBlock = buildingBlock != null ? buildingBlock : "Main Block";
        this.underMaintenance = false;
        this.hourlyMaintenanceCost = Math.max(0.0, hourlyMaintenanceCost);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getBuildingBlock() {
        return buildingBlock;
    }

    public void setBuildingBlock(String buildingBlock) {
        this.buildingBlock = buildingBlock;
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    public double getHourlyMaintenanceCost() {
        return hourlyMaintenanceCost;
    }

    public void setHourlyMaintenanceCost(double hourlyMaintenanceCost) {
        this.hourlyMaintenanceCost = hourlyMaintenanceCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Facility facility = (Facility) o;
        return Objects.equals(id, facility.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, Cap: %d, Loc: %s%s)",
                id, name, type.getLabel(), capacity, buildingBlock,
                underMaintenance ? " - MAINTENANCE" : "");
    }
}
