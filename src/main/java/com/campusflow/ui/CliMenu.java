package com.campusflow.ui;

import com.campusflow.exception.CampusFlowException;
import com.campusflow.model.*;
import com.campusflow.repository.DataStore;
import com.campusflow.repository.PersistenceManager;
import com.campusflow.service.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import static com.campusflow.ui.ConsoleColors.*;

/**
 * Terminal UI handler providing interactive console menus and navigation.
 */
public class CliMenu {
    private final AuthService authService;
    private final FacilityService facilityService;
    private final BookingService bookingService;
    private final EventService eventService;
    private final AnalyticsService analyticsService;
    private final PersistenceManager persistenceManager;
    private final DataStore dataStore;
    private final Scanner scanner;

    public CliMenu(AuthService authService, FacilityService facilityService,
                   BookingService bookingService, EventService eventService,
                   AnalyticsService analyticsService, PersistenceManager persistenceManager,
                   DataStore dataStore) {
        this.authService = authService;
        this.facilityService = facilityService;
        this.bookingService = bookingService;
        this.eventService = eventService;
        this.analyticsService = analyticsService;
        this.persistenceManager = persistenceManager;
        this.dataStore = dataStore;
        this.scanner = new Scanner(System.in);
    }

    public void startInteractiveLoop() {
        printBanner();
        boolean running = true;

        while (running) {
            if (!authService.isAuthenticated()) {
                showAuthMenu();
                String choice = prompt("Select Option");
                switch (choice) {
                    case "1":
                        handleLogin();
                        break;
                    case "2":
                        quickDemoLogin();
                        break;
                    case "3":
                        running = false;
                        System.out.println(cyan("\nExiting CampusFlow. Goodbye!"));
                        break;
                    default:
                        System.out.println(red("Invalid option. Please choose 1, 2, or 3."));
                }
            } else {
                showMainMenu();
                String choice = prompt("Select Action");
                switch (choice) {
                    case "1":
                        displayFacilities();
                        break;
                    case "2":
                        handleBookFacility();
                        break;
                    case "3":
                        displayMyBookings();
                        break;
                    case "4":
                        handleAdminApprovalMenu();
                        break;
                    case "5":
                        handleEventMenu();
                        break;
                    case "6":
                        displayAnalytics();
                        break;
                    case "7":
                        handleExportAudit();
                        break;
                    case "8":
                        authService.logout();
                        System.out.println(yellow("Logged out successfully."));
                        break;
                    case "9":
                        running = false;
                        System.out.println(cyan("\nThank you for using CampusFlow. Goodbye!"));
                        break;
                    default:
                        System.out.println(red("Invalid selection. Try again."));
                }
            }
        }
    }

    private void printBanner() {
        System.out.println(cyan("╔════════════════════════════════════════════════════════════╗"));
        System.out.println(cyan("║        CAMPUSFLOW: SMART RESOURCE & EVENT MANAGEMENT       ║"));
        System.out.println(cyan("║          VITyarthi Academic Project - Java Standard        ║"));
        System.out.println(cyan("╚════════════════════════════════════════════════════════════╝"));
    }

    private void showAuthMenu() {
        System.out.println("\n" + bold("--- AUTHENTICATION MENU ---"));
        System.out.println("1. Login with Credentials");
        System.out.println("2. Quick Role Select (Admin / Faculty / Student)");
        System.out.println("3. Exit System");
    }

    private void quickDemoLogin() {
        System.out.println("\n" + bold("Select Role to simulate session:"));
        System.out.println("1. Admin   (Dr. Rajesh Sharma - admin@campusflow.edu)");
        System.out.println("2. Faculty (Prof. Ananya Rao  - ananya.rao@campusflow.edu)");
        System.out.println("3. Student (Aarav Patel       - aarav.p@campusflow.edu)");

        String choice = prompt("Role Number");
        try {
            switch (choice) {
                case "1":
                    authService.login("admin@campusflow.edu", "admin123");
                    break;
                case "2":
                    authService.login("ananya.rao@campusflow.edu", "fac123");
                    break;
                case "3":
                    authService.login("aarav.p@campusflow.edu", "stu123");
                    break;
                default:
                    System.out.println(red("Invalid choice."));
                    return;
            }
            User u = authService.getCurrentUser();
            System.out.println(green("Active Session: " + u.getFullName() + " [" + u.getRole().getDisplayName() + "]"));
        } catch (CampusFlowException e) {
            System.out.println(red("Login Failed: " + e.getMessage()));
        }
    }

    private void handleLogin() {
        String email = prompt("Email");
        String password = prompt("Password");
        try {
            User u = authService.login(email, password);
            System.out.println(green("Welcome back, " + u.getFullName() + " (" + u.getRole().getDisplayName() + ")!"));
        } catch (CampusFlowException e) {
            System.out.println(red("Authentication Error: " + e.getMessage()));
        }
    }

    private void showMainMenu() {
        User u = authService.getCurrentUser();
        System.out.println("\n" + bold("════════════════ MAIN DASHBOARD ════════════════"));
        System.out.println("Logged In As: " + cyan(u.getFullName()) + " | Role: " + yellow(u.getRole().getDisplayName()) + " | Dept: " + u.getDepartment());
        System.out.println("1. Browse Campus Facilities & Availability");
        System.out.println("2. Request Facility Reservation");
        System.out.println("3. View My Reservations");
        System.out.println("4. Admin Approval Queue " + (u.getRole() == Role.ADMIN ? green("(Admin Enabled)") : "(Restricted)"));
        System.out.println("5. Events & Attendee Ticketing");
        System.out.println("6. System Utilization & Analytics Report");
        System.out.println("7. Export Audit Snapshot to File");
        System.out.println("8. Switch / Logout User");
        System.out.println("9. Exit");
        System.out.println("────────────────────────────────────────────────");
    }

    private void displayFacilities() {
        System.out.println("\n" + bold("=== REGISTERED CAMPUS FACILITIES ==="));
        Collection<Facility> facilities = facilityService.getAllFacilities();
        System.out.printf("%-14s | %-32s | %-16s | %-8s | %-12s%n", "Facility ID", "Name", "Type", "Capacity", "Status");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Facility f : facilities) {
            String status = f.isUnderMaintenance() ? red("MAINTENANCE") : green("OPERATIONAL");
            System.out.printf("%-14s | %-32s | %-16s | %-8d | %-12s%n",
                    f.getId(), f.getName(), f.getType().getLabel(), f.getCapacity(), status);
        }
    }

    private void handleBookFacility() {
        System.out.println("\n" + bold("=== REQUEST FACILITY RESERVATION ==="));
        displayFacilities();

        String facilityId = prompt("Enter Facility ID (e.g. FAC-SEM-02)");
        String dateStr = prompt("Date (YYYY-MM-DD, e.g. " + LocalDate.now().plusDays(2) + ")");
        String startStr = prompt("Start Time (HH:mm, e.g. 14:00)");
        String endStr = prompt("End Time (HH:mm, e.g. 16:30)");
        String purpose = prompt("Purpose of Reservation");
        String academicStr = prompt("Is this for academic/curriculum purpose? (y/n)");
        boolean isAcademic = academicStr.equalsIgnoreCase("y");

        try {
            TimeSlot slot = TimeSlot.of(dateStr, startStr, endStr);
            Booking booking = bookingService.requestBooking(authService.getCurrentUser(), facilityId, slot, purpose, isAcademic);
            System.out.println(green("\n✔ Reservation Created Successfully!"));
            System.out.println("  Booking ID : " + booking.getId());
            System.out.println("  Facility   : " + booking.getFacilityId());
            System.out.println("  Time Slot  : " + booking.getTimeSlot());
            System.out.println("  Status     : " + (booking.getStatus() == BookingStatus.CONFIRMED ? green("CONFIRMED") : yellow("PENDING APPROVAL")));
            System.out.println("  Remarks    : " + booking.getRemarks());
        } catch (Exception e) {
            System.out.println(red("\n✖ Booking Failed: " + e.getMessage()));
        }
    }

    private void displayMyBookings() {
        User u = authService.getCurrentUser();
        System.out.println("\n" + bold("=== RESERVATIONS FOR USER: " + u.getFullName() + " ==="));
        List<Booking> list = (u.getRole() == Role.ADMIN)
                ? (List<Booking>) bookingService.getAllBookings().stream().collect(java.util.stream.Collectors.toList())
                : bookingService.getBookingsByUser(u.getId());

        if (list.isEmpty()) {
            System.out.println("No reservations recorded.");
            return;
        }

        System.out.printf("%-10s | %-12s | %-24s | %-12s | %s%n", "Booking ID", "Facility", "Time Window", "Status", "Purpose");
        System.out.println("----------------------------------------------------------------------------------------");
        for (Booking b : list) {
            String st = b.getStatus().name();
            if (b.getStatus() == BookingStatus.CONFIRMED) st = green(st);
            else if (b.getStatus() == BookingStatus.PENDING) st = yellow(st);
            else st = red(st);

            System.out.printf("%-10s | %-12s | %-24s | %-12s | %s%n",
                    b.getId(), b.getFacilityId(), b.getTimeSlot().toString(), st, b.getPurpose());
        }
    }

    private void handleAdminApprovalMenu() {
        User u = authService.getCurrentUser();
        if (u.getRole() != Role.ADMIN) {
            System.out.println(red("Access Denied: Only Administrators can review booking approval queues."));
            return;
        }

        System.out.println("\n" + bold("=== PENDING BOOKING APPROVAL QUEUE ==="));
        List<Booking> pending = bookingService.getAllBookings().stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING)
                .collect(java.util.stream.Collectors.toList());

        if (pending.isEmpty()) {
            System.out.println(green("No pending reservation requests in queue!"));
            return;
        }

        for (Booking b : pending) {
            System.out.printf("-> ID: %s | User: %s | Facility: %s | Slot: %s | Priority: %d | Purpose: %s%n",
                    b.getId(), b.getRequestedByUserId(), b.getFacilityId(), b.getTimeSlot(), b.getPriorityWeight(), b.getPurpose());
        }

        String targetId = prompt("\nEnter Booking ID to review (or press ENTER to cancel)");
        if (targetId.trim().isEmpty()) return;

        String action = prompt("Action: (1) Approve  (2) Reject");
        try {
            if ("1".equals(action)) {
                bookingService.approveBooking(targetId, u);
                System.out.println(green("Booking " + targetId + " successfully APPROVED."));
            } else if ("2".equals(action)) {
                String reason = prompt("Enter Rejection Reason");
                bookingService.rejectBooking(targetId, u, reason);
                System.out.println(yellow("Booking " + targetId + " REJECTED."));
            }
        } catch (CampusFlowException e) {
            System.out.println(red("Approval Error: " + e.getMessage()));
        }
    }

    private void handleEventMenu() {
        System.out.println("\n" + bold("=== CAMPUS EVENTS & ATTENDEE TICKETING ==="));
        System.out.println("1. View Published Events & Available Seats");
        System.out.println("2. RSVP / Register for an Event");
        System.out.println("3. Create New Event (Linked to Confirmed Booking)");

        String choice = prompt("Select Option");
        switch (choice) {
            case "1":
                displayEvents();
                break;
            case "2":
                handleRsvp();
                break;
            case "3":
                handleCreateEvent();
                break;
        }
    }

    private void displayEvents() {
        List<CampusEvent> events = eventService.getPublishedEvents();
        System.out.println("\n" + bold("--- UPCOMING PUBLISHED CAMPUS EVENTS ---"));
        if (events.isEmpty()) {
            System.out.println("No scheduled events available.");
            return;
        }
        for (CampusEvent ev : events) {
            System.out.printf("[%s] %s%n  Organizer: %s | Booking Ref: %s%n  Registered: %d/%d seats | Description: %s%n%n",
                    ev.getEventId(), ev.getTitle(), ev.getOrganizerUserId(), ev.getBookingId(),
                    ev.getRegisteredAttendeeIds().size(), ev.getMaxAttendees(), ev.getDescription());
        }
    }

    private void handleRsvp() {
        displayEvents();
        String eventId = prompt("Enter Event ID to RSVP for");
        User u = authService.getCurrentUser();
        try {
            boolean success = eventService.registerForEvent(eventId, u.getId());
            if (success) {
                System.out.println(green("✔ Successfully registered " + u.getFullName() + " for event " + eventId + "!"));
            } else {
                System.out.println(red("✖ Registration failed: Event is full or you are already registered."));
            }
        } catch (ValidationException e) {
            System.out.println(red("✖ Error: " + e.getMessage()));
        }
    }

    private void handleCreateEvent() {
        User u = authService.getCurrentUser();
        System.out.println("\n" + bold("Create New Campus Event"));
        String title = prompt("Event Title");
        String desc = prompt("Event Description");
        String bookingId = prompt("Confirmed Booking Reference ID");
        int maxCap = Integer.parseInt(prompt("Maximum Attendee Capacity"));

        try {
            CampusEvent ev = eventService.createEvent(title, desc, u.getId(), bookingId, maxCap);
            System.out.println(green("✔ Event Created and Published: " + ev.getEventId() + " (" + ev.getTitle() + ")"));
        } catch (Exception e) {
            System.out.println(red("✖ Failed to create event: " + e.getMessage()));
        }
    }

    private void displayAnalytics() {
        System.out.println("\n" + analyticsService.formatSummaryReport());
    }

    private void handleExportAudit() {
        String filename = "./data/campusflow_audit_report.txt";
        try {
            persistenceManager.exportAuditSnapshot(dataStore, filename);
            System.out.println(green("✔ System state and audit trail exported to: " + new File(filename).getAbsolutePath()));
        } catch (Exception e) {
            System.out.println(red("✖ Failed to export snapshot: " + e.getMessage()));
        }
    }

    private String prompt(String message) {
        System.out.print(cyan(message + " > "));
        return scanner.nextLine().trim();
    }
}
