package com.campusflow;

import com.campusflow.exception.ResourceConflictException;
import com.campusflow.exception.UnauthorizedException;
import com.campusflow.exception.ValidationException;
import com.campusflow.model.*;
import com.campusflow.repository.DataStore;
import com.campusflow.repository.PersistenceManager;
import com.campusflow.service.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Standalone Zero-Dependency Unit Test Runner.
 * Executes comprehensive unit and integration tests directly without requiring Maven or JUnit jars.
 */
public class StandaloneTestRunner {
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("     CAMPUSFLOW STANDALONE UNIT TEST HARNESS     ");
        System.out.println("=================================================\n");

        testTimeSlotOverlapLogic();
        testUserAuthenticationSuccessAndFailure();
        testFacilityRegistrationAndQuery();
        testBookingConflictDetection();
        testAdminApprovalWorkflow();
        testEventCreationAndCapacityEnforcement();

        System.out.println("\n-------------------------------------------------");
        System.out.printf("Test Execution Summary: %d Total | %d Passed | %d Failed%n",
                testsRun, testsPassed, testsFailed);
        System.out.println("-------------------------------------------------");

        if (testsFailed > 0) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL UNIT TESTS PASSED WITH 100% SUCCESS RATE!");
            System.exit(0);
        }
    }

    private static void assertTrue(String testName, boolean condition, String message) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.printf("  [PASS] %-45s : %s%n", testName, message);
        } else {
            testsFailed++;
            System.err.printf("  [FAIL] %-45s : %s%n", testName, message);
        }
    }

    private static void testTimeSlotOverlapLogic() {
        System.out.println("Running TimeSlot Unit Tests...");
        LocalDate today = LocalDate.now();
        TimeSlot slotA = new TimeSlot(today, LocalTime.of(10, 0), LocalTime.of(12, 0));
        TimeSlot slotB = new TimeSlot(today, LocalTime.of(11, 0), LocalTime.of(13, 0)); // Overlaps
        TimeSlot slotC = new TimeSlot(today, LocalTime.of(12, 0), LocalTime.of(14, 0)); // Contiguous, no overlap
        TimeSlot slotD = new TimeSlot(today.plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0)); // Different date

        assertTrue("TimeSlot Overlap Check", slotA.overlapsWith(slotB), "Slots 10-12 and 11-13 correctly identify overlap");
        assertTrue("TimeSlot Non-Overlap Check", !slotA.overlapsWith(slotC), "Adjacent boundary slots do not conflict");
        assertTrue("Different Date Non-Overlap", !slotA.overlapsWith(slotD), "Different dates do not conflict");
    }

    private static void testUserAuthenticationSuccessAndFailure() {
        System.out.println("\nRunning AuthService Unit Tests...");
        DataStore store = new DataStore();
        new PersistenceManager("").seedInitialData(store);
        AuthService auth = new AuthService(store);

        try {
            User u = auth.login("admin@campusflow.edu", "admin123");
            assertTrue("Auth Valid Credentials", u != null && u.getRole() == Role.ADMIN, "Admin logs in successfully");
        } catch (Exception e) {
            assertTrue("Auth Valid Credentials", false, e.getMessage());
        }

        try {
            auth.login("admin@campusflow.edu", "wrongpass");
            assertTrue("Auth Invalid Credentials", false, "Should have thrown UnauthorizedException");
        } catch (UnauthorizedException e) {
            assertTrue("Auth Invalid Credentials", true, "Unauthorized credentials correctly rejected");
        } catch (Exception e) {
            assertTrue("Auth Invalid Credentials", false, "Unexpected exception: " + e.getMessage());
        }
    }

    private static void testFacilityRegistrationAndQuery() {
        System.out.println("\nRunning FacilityService Unit Tests...");
        DataStore store = new DataStore();
        FacilityService fs = new FacilityService(store);

        try {
            fs.registerFacility("TEST-01", "Robotics Workshop", ResourceType.COMPUTER_LAB, 45, "Block E", 200.0);
            assertTrue("Facility Registration", store.findFacilityById("TEST-01").isPresent(), "Facility successfully saved");
        } catch (Exception e) {
            assertTrue("Facility Registration", false, e.getMessage());
        }

        try {
            fs.registerFacility("TEST-01", "Duplicate", ResourceType.COMPUTER_LAB, 30, "Block E", 100.0);
            assertTrue("Duplicate Facility Guard", false, "Should prevent duplicate facility IDs");
        } catch (ValidationException e) {
            assertTrue("Duplicate Facility Guard", true, "Duplicate facility ID correctly caught");
        } catch (Exception e) {
            assertTrue("Duplicate Facility Guard", false, "Unexpected exception: " + e.getMessage());
        }
    }

    private static void testBookingConflictDetection() {
        System.out.println("\nRunning Conflict Detection Unit Tests...");
        DataStore store = new DataStore();
        new PersistenceManager("").seedInitialData(store);
        BookingService bs = new BookingService(store);

        User student = store.findUserById("STU01").get();
        TimeSlot slot = new TimeSlot(LocalDate.now().plusDays(5), LocalTime.of(14, 0), LocalTime.of(16, 0));

        try {
            Booking b1 = bs.requestBooking(student, "FAC-SEM-02", slot, "Seminar on IoT", false);
            assertTrue("Valid Booking Creation", b1 != null, "Initial booking created in pending state");

            // Deliberate Conflict Attempt
            TimeSlot conflictSlot = new TimeSlot(LocalDate.now().plusDays(5), LocalTime.of(15, 0), LocalTime.of(17, 0));
            bs.requestBooking(student, "FAC-SEM-02", conflictSlot, "Overlapping Meeting", false);
            assertTrue("Conflict Prevention", false, "Should have blocked overlapping booking");
        } catch (ResourceConflictException e) {
            assertTrue("Conflict Prevention", true, "ResourceConflictException correctly thrown for overlapping slot");
        } catch (Exception e) {
            assertTrue("Conflict Prevention", false, "Unexpected error: " + e.getMessage());
        }
    }

    private static void testAdminApprovalWorkflow() {
        System.out.println("\nRunning Admin Approval Workflow Unit Tests...");
        DataStore store = new DataStore();
        new PersistenceManager("").seedInitialData(store);
        BookingService bs = new BookingService(store);

        User student = store.findUserById("STU02").get();
        User admin = store.findUserById("ADM01").get();
        TimeSlot slot = new TimeSlot(LocalDate.now().plusDays(6), LocalTime.of(9, 0), LocalTime.of(11, 0));

        try {
            Booking b = bs.requestBooking(student, "FAC-CLS-04", slot, "Peer Study Session", false);
            assertTrue("Student Booking Initial State", b.getStatus() == BookingStatus.PENDING, "Student request starts as PENDING");

            bs.approveBooking(b.getId(), admin);
            assertTrue("Admin Approval Transition", b.getStatus() == BookingStatus.CONFIRMED, "Status transitioned to CONFIRMED");
        } catch (Exception e) {
            assertTrue("Admin Approval Transition", false, e.getMessage());
        }
    }

    private static void testEventCreationAndCapacityEnforcement() {
        System.out.println("\nRunning Event & Capacity Unit Tests...");
        DataStore store = new DataStore();
        new PersistenceManager("").seedInitialData(store);
        EventService es = new EventService(store);

        Booking confirmedBooking = store.findBookingById("BKG-101").get(); // Capacity is 600

        try {
            // Exceed capacity check
            es.createEvent("Overfilled Event", "Desc", "FAC01", confirmedBooking.getId(), 750);
            assertTrue("Capacity Overflow Check", false, "Should reject events exceeding facility size");
        } catch (ValidationException e) {
            assertTrue("Capacity Overflow Check", true, "Excess capacity correctly caught and rejected");
        } catch (Exception e) {
            assertTrue("Capacity Overflow Check", false, "Unexpected exception: " + e.getMessage());
        }
    }
}
