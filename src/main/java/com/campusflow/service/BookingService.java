package com.campusflow.service;

import com.campusflow.exception.ResourceConflictException;
import com.campusflow.exception.UnauthorizedException;
import com.campusflow.exception.ValidationException;
import com.campusflow.model.*;
import com.campusflow.repository.DataStore;
import com.campusflow.strategy.PriorityAllocationStrategy;
import com.campusflow.strategy.StandardAllocationStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Core engine handling facility reservations, conflict detection, priority scoring, and approvals.
 */
public class BookingService {
    private final DataStore dataStore;
    private PriorityAllocationStrategy allocationStrategy;

    public BookingService(DataStore dataStore) {
        this.dataStore = dataStore;
        this.allocationStrategy = new StandardAllocationStrategy();
    }

    public void setAllocationStrategy(PriorityAllocationStrategy allocationStrategy) {
        this.allocationStrategy = allocationStrategy;
    }

    /**
     * Attempts to create a booking reservation for a user.
     * Enforces slot validation, maintenance checks, and conflict avoidance.
     */
    public synchronized Booking requestBooking(User requester, String facilityId, TimeSlot timeSlot,
                                               String purpose, boolean isAcademic)
            throws ValidationException, ResourceConflictException {

        if (requester == null) {
            throw new ValidationException("requester", "Requester user cannot be null.");
        }
        if (facilityId == null || facilityId.trim().isEmpty()) {
            throw new ValidationException("facilityId", "Target facility must be specified.");
        }
        if (timeSlot == null) {
            throw new ValidationException("timeSlot", "Time slot must be provided.");
        }
        if (purpose == null || purpose.trim().isEmpty()) {
            throw new ValidationException("purpose", "Reservation purpose must be provided.");
        }

        Facility facility = dataStore.findFacilityById(facilityId)
                .orElseThrow(() -> new ValidationException("facilityId", "Facility ID not recognized: " + facilityId));

        if (facility.isUnderMaintenance()) {
            throw new ResourceConflictException("Facility is currently marked under maintenance.", facilityId, "Under Maintenance");
        }

        // Conflict Detection: check against all active CONFIRMED or PENDING bookings for this facility
        for (Booking existing : dataStore.getAllBookings()) {
            if (existing.getFacilityId().equals(facilityId)) {
                if (existing.getStatus() == BookingStatus.CONFIRMED || existing.getStatus() == BookingStatus.PENDING) {
                    if (existing.getTimeSlot().overlapsWith(timeSlot)) {
                        throw new ResourceConflictException(
                                String.format("Time-slot conflict detected with Booking [%s] by User [%s]",
                                        existing.getId(), existing.getRequestedByUserId()),
                                facilityId,
                                existing.getTimeSlot().toString()
                        );
                    }
                }
            }
        }

        // Calculate priority weight using Strategy pattern
        int priority = allocationStrategy.calculatePriorityScore(requester, timeSlot.getDurationMinutes(), isAcademic);

        String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Booking booking = new Booking(bookingId, facilityId, requester.getId(), timeSlot, purpose.trim(), priority);

        // Faculty and Admin requests get auto-confirmed or high-priority confirmation
        if (requester.getRole() == Role.ADMIN || requester.getRole() == Role.FACULTY) {
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setApprovedByUserId(requester.getId());
            booking.setRemarks("Auto-approved based on faculty/admin role privilege.");
        } else {
            booking.setStatus(BookingStatus.PENDING);
            booking.setRemarks("Pending admin review and clearance.");
        }

        dataStore.saveBooking(booking);
        dataStore.addAuditLog(String.format("Booking %s requested by %s for %s (%s). Status: %s",
                booking.getId(), requester.getId(), facility.getName(), timeSlot, booking.getStatus()));

        return booking;
    }

    public synchronized void approveBooking(String bookingId, User adminUser)
            throws ValidationException, UnauthorizedException {

        if (adminUser == null || adminUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException(adminUser != null ? adminUser.getId() : "null", "ADMIN", "Approve Booking");
        }

        Booking booking = dataStore.findBookingById(bookingId)
                .orElseThrow(() -> new ValidationException("bookingId", "Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ValidationException("status", "Only PENDING bookings can be approved. Current status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setApprovedByUserId(adminUser.getId());
        booking.setRemarks("Approved by Admin " + adminUser.getFullName());
        dataStore.addAuditLog(String.format("Booking %s officially APPROVED by Admin %s.", bookingId, adminUser.getId()));
    }

    public synchronized void rejectBooking(String bookingId, User adminUser, String reason)
            throws ValidationException, UnauthorizedException {

        if (adminUser == null || adminUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException(adminUser != null ? adminUser.getId() : "null", "ADMIN", "Reject Booking");
        }

        Booking booking = dataStore.findBookingById(bookingId)
                .orElseThrow(() -> new ValidationException("bookingId", "Booking not found: " + bookingId));

        booking.setStatus(BookingStatus.REJECTED);
        booking.setApprovedByUserId(adminUser.getId());
        booking.setRemarks(reason != null ? reason : "Rejected by administration.");
        dataStore.addAuditLog(String.format("Booking %s REJECTED by Admin %s. Reason: %s", bookingId, adminUser.getId(), reason));
    }

    public synchronized void cancelBooking(String bookingId, User user)
            throws ValidationException, UnauthorizedException {

        Booking booking = dataStore.findBookingById(bookingId)
                .orElseThrow(() -> new ValidationException("bookingId", "Booking not found: " + bookingId));

        boolean isOwner = booking.getRequestedByUserId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException(user.getId(), "ADMIN or Owner", "Cancel Booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setRemarks("Cancelled by " + user.getFullName());
        dataStore.addAuditLog(String.format("Booking %s CANCELLED by %s.", bookingId, user.getId()));
    }

    public List<Booking> getBookingsByUser(String userId) {
        return dataStore.getAllBookings().stream()
                .filter(b -> b.getRequestedByUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByFacility(String facilityId) {
        return dataStore.getAllBookings().stream()
                .filter(b -> b.getFacilityId().equals(facilityId))
                .collect(Collectors.toList());
    }

    public Collection<Booking> getAllBookings() {
        return dataStore.getAllBookings();
    }
}
