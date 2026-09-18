package com.campusflow.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a reservation for a campus facility.
 */
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String facilityId;
    private final String requestedByUserId;
    private final TimeSlot timeSlot;
    private final String purpose;
    private BookingStatus status;
    private int priorityWeight;
    private final LocalDateTime createdAt;
    private String approvedByUserId;
    private String remarks;

    public Booking(String id, String facilityId, String requestedByUserId, TimeSlot timeSlot, String purpose, int priorityWeight) {
        this.id = Objects.requireNonNull(id, "Booking ID is required");
        this.facilityId = Objects.requireNonNull(facilityId, "Facility ID is required");
        this.requestedByUserId = Objects.requireNonNull(requestedByUserId, "Requesting user is required");
        this.timeSlot = Objects.requireNonNull(timeSlot, "TimeSlot is required");
        this.purpose = Objects.requireNonNull(purpose, "Purpose is required");
        this.status = BookingStatus.PENDING;
        this.priorityWeight = priorityWeight;
        this.createdAt = LocalDateTime.now();
        this.remarks = "";
    }

    public String getId() {
        return id;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getRequestedByUserId() {
        return requestedByUserId;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public String getPurpose() {
        return purpose;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public int getPriorityWeight() {
        return priorityWeight;
    }

    public void setPriorityWeight(int priorityWeight) {
        this.priorityWeight = priorityWeight;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getApprovedByUserId() {
        return approvedByUserId;
    }

    public void setApprovedByUserId(String approvedByUserId) {
        this.approvedByUserId = approvedByUserId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Booking #%s | Facility: %s | User: %s | Slot: %s | Status: %s | Priority: %d",
                id, facilityId, requestedByUserId, timeSlot, status, priorityWeight);
    }
}
