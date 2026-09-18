package com.campusflow.service;

import com.campusflow.exception.CampusFlowException;
import com.campusflow.exception.ValidationException;
import com.campusflow.model.Booking;
import com.campusflow.model.BookingStatus;
import com.campusflow.model.CampusEvent;
import com.campusflow.model.Facility;
import com.campusflow.repository.DataStore;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service orchestrating campus events, publishing, and attendee ticketing.
 */
public class EventService {
    private final DataStore dataStore;

    public EventService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public CampusEvent createEvent(String title, String description, String organizerId, String bookingId, int maxAttendees)
            throws CampusFlowException {

        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("title", "Event title cannot be empty.");
        }

        Booking booking = dataStore.findBookingById(bookingId)
                .orElseThrow(() -> new ValidationException("bookingId", "Booking reference not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ValidationException("bookingId", "Events can only be scheduled on CONFIRMED bookings. Current: " + booking.getStatus());
        }

        Facility facility = dataStore.findFacilityById(booking.getFacilityId())
                .orElseThrow(() -> new ValidationException("facilityId", "Underlying facility not found"));

        if (maxAttendees > facility.getCapacity()) {
            throw new ValidationException("maxAttendees",
                    String.format("Requested event capacity (%d) exceeds facility capacity (%d).",
                            maxAttendees, facility.getCapacity()));
        }

        String eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        CampusEvent event = new CampusEvent(eventId, title.trim(), description, organizerId, bookingId, maxAttendees);
        event.setPublished(true);

        dataStore.saveEvent(event);
        dataStore.addAuditLog(String.format("Event %s ('%s') created by %s linked to Booking %s",
                eventId, title, organizerId, bookingId));

        return event;
    }

    public synchronized boolean registerForEvent(String eventId, String studentId) throws ValidationException {
        CampusEvent event = dataStore.findEventById(eventId)
                .orElseThrow(() -> new ValidationException("eventId", "Event not found: " + eventId));

        if (!event.isPublished()) {
            throw new ValidationException("status", "Event is not currently open for registrations.");
        }

        boolean registered = event.registerAttendee(studentId);
        if (registered) {
            dataStore.addAuditLog(String.format("Student %s successfully registered for Event %s (%s).",
                    studentId, event.getEventId(), event.getTitle()));
        }
        return registered;
    }

    public synchronized boolean cancelRegistration(String eventId, String studentId) throws ValidationException {
        CampusEvent event = dataStore.findEventById(eventId)
                .orElseThrow(() -> new ValidationException("eventId", "Event not found: " + eventId));

        boolean cancelled = event.cancelRegistration(studentId);
        if (cancelled) {
            dataStore.addAuditLog(String.format("Student %s cancelled registration for Event %s.", studentId, eventId));
        }
        return cancelled;
    }

    public List<CampusEvent> getPublishedEvents() {
        return dataStore.getAllEvents().stream()
                .filter(CampusEvent::isPublished)
                .collect(Collectors.toList());
    }

    public Collection<CampusEvent> getAllEvents() {
        return dataStore.getAllEvents();
    }
}
