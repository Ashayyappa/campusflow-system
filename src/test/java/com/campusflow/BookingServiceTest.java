package com.campusflow;

import com.campusflow.exception.ResourceConflictException;
import com.campusflow.exception.ValidationException;
import com.campusflow.model.*;
import com.campusflow.repository.DataStore;
import com.campusflow.repository.PersistenceManager;
import com.campusflow.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    private DataStore dataStore;
    private BookingService bookingService;
    private User student;

    @BeforeEach
    void setUp() {
        dataStore = new DataStore();
        PersistenceManager pm = new PersistenceManager("");
        pm.seedInitialData(dataStore);
        bookingService = new BookingService(dataStore);
        student = dataStore.findUserById("STU01").orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @Test
    @DisplayName("Should successfully create a pending booking for student")
    void testCreateBookingSuccess() throws Exception {
        TimeSlot slot = new TimeSlot(LocalDate.now().plusDays(2), LocalTime.of(14, 0), LocalTime.of(16, 0));
        Booking booking = bookingService.requestBooking(student, "FAC-LAB-03", slot, "Hackathon Prep", false);

        assertNotNull(booking);
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        assertEquals("FAC-LAB-03", booking.getFacilityId());
    }

    @Test
    @DisplayName("Should reject overlapping reservations on the same facility")
    void testDetectConflictingBooking() throws Exception {
        TimeSlot slot1 = new TimeSlot(LocalDate.now().plusDays(3), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Booking b1 = bookingService.requestBooking(student, "FAC-SEM-02", slot1, "Session A", false);
        assertNotNull(b1);

        TimeSlot slotOverlap = new TimeSlot(LocalDate.now().plusDays(3), LocalTime.of(11, 0), LocalTime.of(13, 0));
        assertThrows(ResourceConflictException.class, () -> {
            bookingService.requestBooking(student, "FAC-SEM-02", slotOverlap, "Session B", false);
        });
    }

    @Test
    @DisplayName("Should reject booking if facility does not exist")
    void testInvalidFacilityThrowsValidationException() {
        TimeSlot slot = new TimeSlot(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(10, 0));
        assertThrows(ValidationException.class, () -> {
            bookingService.requestBooking(student, "INVALID-ID", slot, "Test", false);
        });
    }
}
