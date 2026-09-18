package com.campusflow.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an academic, technical, or cultural event linked to a facility booking.
 */
public class CampusEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String eventId;
    private String title;
    private String description;
    private final String organizerUserId;
    private final String bookingId;
    private int maxAttendees;
    private final Set<String> registeredAttendeeIds;
    private boolean published;

    public CampusEvent(String eventId, String title, String description, String organizerUserId, String bookingId, int maxAttendees) {
        this.eventId = Objects.requireNonNull(eventId, "Event ID cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.description = description != null ? description : "";
        this.organizerUserId = Objects.requireNonNull(organizerUserId, "Organizer ID cannot be null");
        this.bookingId = Objects.requireNonNull(bookingId, "Booking ID cannot be null");
        this.maxAttendees = Math.max(1, maxAttendees);
        this.registeredAttendeeIds = new HashSet<>();
        this.published = false;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizerUserId() {
        return organizerUserId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Set<String> getRegisteredAttendeeIds() {
        return Collections.unmodifiableSet(registeredAttendeeIds);
    }

    public synchronized boolean registerAttendee(String studentId) {
        if (studentId == null || registeredAttendeeIds.size() >= maxAttendees) {
            return false;
        }
        return registeredAttendeeIds.add(studentId);
    }

    public synchronized boolean cancelRegistration(String studentId) {
        return registeredAttendeeIds.remove(studentId);
    }

    public int getAvailableSeats() {
        return Math.max(0, maxAttendees - registeredAttendeeIds.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CampusEvent that = (CampusEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Org: %s | Seats: %d/%d | Published: %b",
                eventId, title, organizerUserId, registeredAttendeeIds.size(), maxAttendees, published);
    }
}
