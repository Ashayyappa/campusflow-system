package com.campusflow.repository;

import com.campusflow.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe centralized in-memory datastore holding all entities and audit logs.
 */
public class DataStore {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Facility> facilities = new ConcurrentHashMap<>();
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
    private final Map<String, CampusEvent> events = new ConcurrentHashMap<>();
    private final List<String> auditLogs = new CopyOnWriteArrayList<>();

    public DataStore() {
    }

    // --- Users ---
    public void saveUser(User user) {
        users.put(user.getId(), user);
    }

    public Optional<User> findUserById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findUserByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Collection<User> getAllUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    // --- Facilities ---
    public void saveFacility(Facility facility) {
        facilities.put(facility.getId(), facility);
    }

    public Optional<Facility> findFacilityById(String id) {
        return Optional.ofNullable(facilities.get(id));
    }

    public Collection<Facility> getAllFacilities() {
        return Collections.unmodifiableCollection(facilities.values());
    }

    // --- Bookings ---
    public void saveBooking(Booking booking) {
        bookings.put(booking.getId(), booking);
    }

    public Optional<Booking> findBookingById(String id) {
        return Optional.ofNullable(bookings.get(id));
    }

    public Collection<Booking> getAllBookings() {
        return Collections.unmodifiableCollection(bookings.values());
    }

    // --- Events ---
    public void saveEvent(CampusEvent event) {
        events.put(event.getEventId(), event);
    }

    public Optional<CampusEvent> findEventById(String id) {
        return Optional.ofNullable(events.get(id));
    }

    public Collection<CampusEvent> getAllEvents() {
        return Collections.unmodifiableCollection(events.values());
    }

    // --- Audit Logs ---
    public void addAuditLog(String action) {
        String entry = String.format("[%s] %s", java.time.LocalDateTime.now().toString(), action);
        auditLogs.add(entry);
    }

    public List<String> getAuditLogs() {
        return Collections.unmodifiableList(auditLogs);
    }

    public void clearAll() {
        users.clear();
        facilities.clear();
        bookings.clear();
        events.clear();
        auditLogs.clear();
    }
}
