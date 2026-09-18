package com.campusflow.repository;

import com.campusflow.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Manages file persistence and bootstrap data seeding for the CampusFlow system.
 */
public class PersistenceManager {
    private final Path storagePath;

    public PersistenceManager(String filePath) {
        this.storagePath = Paths.get(filePath);
    }

    /**
     * Seeds initial mock data for students, faculty, facilities, and bookings.
     */
    public void seedInitialData(DataStore store) {
        // Create Admin
        User admin = new User("ADM01", "Dr. Rajesh Sharma", "admin@campusflow.edu", "admin123", Role.ADMIN, "Estate Office");
        // Create Faculty
        User faculty1 = new User("FAC01", "Prof. Ananya Rao", "ananya.rao@campusflow.edu", "fac123", Role.FACULTY, "Computer Science");
        User faculty2 = new User("FAC02", "Prof. Vikram Sen", "vikram.sen@campusflow.edu", "fac456", Role.FACULTY, "Electronics & Comm");
        // Create Students
        User student1 = new User("STU01", "Aarav Patel", "aarav.p@campusflow.edu", "stu123", Role.STUDENT, "Computer Science");
        User student2 = new User("STU02", "Diya Mehta", "diya.m@campusflow.edu", "stu456", Role.STUDENT, "Mechanical Eng");

        store.saveUser(admin);
        store.saveUser(faculty1);
        store.saveUser(faculty2);
        store.saveUser(student1);
        store.saveUser(student2);

        // Create Facilities
        Facility aud = new Facility("FAC-AUD-01", "Sarojini Naidu Central Auditorium", ResourceType.AUDITORIUM, 600, "Main Campus Block A", 2500.0);
        Facility sem = new Facility("FAC-SEM-02", "Aryabhata Seminar Hall", ResourceType.SEMINAR_HALL, 120, "Science Block B", 800.0);
        Facility lab = new Facility("FAC-LAB-03", "Alan Turing Computing Lab", ResourceType.COMPUTER_LAB, 75, "Tech Tower C", 500.0);
        Facility cls = new Facility("FAC-CLS-04", "Smart Classroom 301", ResourceType.SMART_CLASSROOM, 90, "Academic Wing D", 300.0);
        Facility spr = new Facility("FAC-SPT-05", "Indoor Badminton Arena", ResourceType.SPORTS_COMPLEX, 150, "Student Activity Center", 400.0);

        store.saveFacility(aud);
        store.saveFacility(sem);
        store.saveFacility(lab);
        store.saveFacility(cls);
        store.saveFacility(spr);

        // Pre-create a confirmed booking
        TimeSlot slot1 = new TimeSlot(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(13, 0));
        Booking b1 = new Booking("BKG-101", aud.getId(), faculty1.getId(), slot1, "Annual International AI Colloquium", 85);
        b1.setStatus(BookingStatus.CONFIRMED);
        b1.setApprovedByUserId(admin.getId());
        store.saveBooking(b1);

        // Pre-create an event linked to this booking
        CampusEvent ev1 = new CampusEvent("EVT-501", "International AI Colloquium 2026",
                "Keynote address on Agentic AI and Large Language Models in Engineering",
                faculty1.getId(), b1.getId(), 500);
        ev1.setPublished(true);
        ev1.registerAttendee(student1.getId());
        ev1.registerAttendee(student2.getId());
        store.saveEvent(ev1);

        store.addAuditLog("System bootstrapped with initial academic entities, facilities, and seed reservations.");
    }

    /**
     * Exports snapshot of bookings and facilities to a flat text report.
     */
    public void exportAuditSnapshot(DataStore store, String exportFilePath) throws IOException {
        Path path = Paths.get(exportFilePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
            writer.println("==================================================================");
            writer.println("               CAMPUSFLOW AUDIT & STATE SNAPSHOT                  ");
            writer.println("==================================================================");
            writer.println("Exported Timestamp: " + java.time.LocalDateTime.now());
            writer.println();

            writer.println("--- 1. REGISTERED FACILITIES ---");
            for (Facility f : store.getAllFacilities()) {
                writer.println(f);
            }
            writer.println();

            writer.println("--- 2. ACTIVE RESERVATIONS ---");
            for (Booking b : store.getAllBookings()) {
                writer.println(b);
            }
            writer.println();

            writer.println("--- 3. SCHEDULED EVENTS ---");
            for (CampusEvent e : store.getAllEvents()) {
                writer.println(e);
            }
            writer.println();

            writer.println("--- 4. AUDIT TRAIL LOGS ---");
            for (String log : store.getAuditLogs()) {
                writer.println(log);
            }
            writer.println("==================================================================");
        }
    }
}
