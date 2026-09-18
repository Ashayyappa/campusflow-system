# Problem Statement & Project Scope

## Project Title
**CampusFlow: Smart Resource Allocation & Event Management Engine**  
**Submitted By:** M T Akash Ayyappa (Reg No: 24BCY10020)  
**Academic Course:** Java Programming / Object-Oriented Software Engineering (VITyarthi)  

---

## 1. Problem Statement
Educational and university campuses host hundreds of academic, co-curricular, and extracurricular activities weekly across diverse infrastructure including auditoriums, smart classrooms, computer laboratories, and sports complexes. Currently, facility reservation workflows suffer from major operational bottlenecks:
1. **Manual Scheduling & Clashing Overlaps:** Paper-based or unstructured communication (emails/forms) leads to frequent double-bookings and time-slot scheduling conflicts.
2. **Lack of Transparent Priority Allocation:** Administrative events, departmental academic colloquiums, and student club workshops compete for the same physical facilities without clear, objective scheduling rules.
3. **Inefficient Capacity Utilization:** Large auditoriums (500+ capacity) are often underutilized for small team meetings, while large seminars are constrained into smaller classrooms due to lack of real-time capacity and inventory visibility.
4. **Disconnected Event & Attendee Lifecycle:** Booking a room is disconnected from event ticketing and attendee management, leading to overcrowding and fire-safety compliance hazards.
5. **Absence of Auditable Records & Analytics:** Estate offices lack automated metrics on facility utilization rates, peak demand windows, and maintenance downtime.

**CampusFlow** resolves these challenges by providing an automated, high-performance, command-line executable Java platform that unifies facility inventory management, interval conflict detection, priority-driven booking workflows, and attendee RSVP management.

---

## 2. Scope of the Project

### In-Scope:
- **Centralized Facility Catalog:** Detailed registration of campus facilities categorized by `ResourceType`, seating capacity, physical location block, and operational/maintenance status.
- **Dynamic Interval Conflict-Detection Engine:** Instant mathematical validation ensuring no two overlapping reservations can be confirmed for the same facility on the same calendar day.
- **Priority-Driven Scheduling (Strategy Pattern):** Dynamic score weighting based on user role (Admin, Faculty, Student), duration, and academic vs. recreational purpose.
- **Role-Based Access Control (RBAC):** Distinct permission boundaries separating Administrative approvers, Faculty requesters, and Student organizers.
- **Campus Event Lifecycle & RSVP Ticketing:** Seamless transition of confirmed facility reservations into published campus events with strictly enforced seating caps.
- **Operational Analytics & Audit Trail:** Instant generation of executive reports detailing facility utilization, approval ratios, high-demand hotspots, and append-only audit event logs.
- **Zero-Dependency CLI Execution:** Fully operable via an intuitive interactive console menu or non-interactive automated test mode (`--demo`).

### Out-of-Scope (Future Enhancements):
- Direct biometric or RFID card-swipe access hardware integration at auditorium gates.
- Online credit card / payment gateway processing for commercial third-party facility rentals.
- Distributed multi-campus cloud synchronizers across different geographic continents.

---

## 3. Target Users

| User Persona | Role Description | Key Permissions & Use Cases |
| :--- | :--- | :--- |
| **Estate & Academic Administrator** | System Administrator (`ADMIN`) | Reviews pending reservation requests, approves/rejects bookings, flags facilities for maintenance, monitors campus-wide utilization analytics, and exports audit trails. |
| **Faculty & Department Chairs** | Academic Staff (`FACULTY`) | Schedules academic conferences, symposiums, guest lectures, and lab sessions with auto-approved priority scheduling privileges. |
| **Student Body & Club Leads** | Student Organizers (`STUDENT`) | Browses available facility slots, submits booking requests for club events/workshops, schedules events, and RSVPs for published campus activities. |
| **Course Evaluator / Automated Grader** | Academic Evaluator | Reviews system architecture, executes automated verification harness via `--demo` mode, and runs unit test suites. |

---

## 4. High-Level Features

1. **Identity & Access Management (IAM):**
   - Secure credential verification with simulated cryptographic hashing.
   - Granular RBAC hierarchy enforcing access limits at each action layer.

2. **Resource Catalog & Inventory Engine:**
   - Real-time indexing of campus infrastructure.
   - Instant filtering by facility type and minimum seating capacity.
   - Dynamic maintenance-mode toggling with reason logging.

3. **Intelligent Reservation & Conflict-Detection Engine:**
   - Date and time interval mathematical intersection algorithms (`O(1)` per slot comparison).
   - Strategy-based priority scoring calculation (`StandardAllocationStrategy`, `FacultyPriorityAllocationStrategy`).
   - Multi-state booking lifecycle: `PENDING`, `CONFIRMED`, `REJECTED`, `CANCELLED`.

4. **Event Management & RSVP Ticketing Engine:**
   - Links published events directly to verified facility reservations.
   - Thread-safe seat reservations preventing overbooking beyond facility limits.
   - Instant seat vacancy calculations and attendee roster management.

5. **Executive Analytics & Reporting:**
   - Utilization metrics calculation, booking approval rates, and facility demand hotspots.
   - Flat-file snapshot export for audit logs and historical tracking.
