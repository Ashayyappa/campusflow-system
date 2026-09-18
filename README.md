# CampusFlow: Smart Campus Resource Allocation & Event Management Engine

[![Java Standard](https://img.shields.io/badge/Java-8%20%7C%2011%20%7C%2017%20%7C%2021-blue.svg)](https://www.oracle.com/java/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Testing](https://img.shields.io/badge/Unit%20Tests-100%25%20Passing-success.svg)]()
[![License](https://img.shields.io/badge/Course%20Project-VITyarthi-orange.svg)]()

> A robust, command-line-driven Object-Oriented Java application designed to eliminate scheduling conflicts, enforce priority-based facility allocation, and coordinate campus events and attendee ticketing across educational institutions.

---

## 1. Project Title & Overview

**Project Title:** CampusFlow - Smart Campus Resource Allocation & Event Management Engine  
**Student Name:** M T Akash Ayyappa  
**Registration No:** 24BCY10020  
**Course Domain:** Java Programming / Object-Oriented Programming (OOP) in Java  
**Architecture:** Layered Architecture (Model-Service-Repository-Strategy-CLI)  

### The Problem
Universities manage hundreds of shared facilities (auditoriums, computer labs, smart seminar halls, sports complexes). Traditional ad-hoc booking systems suffer from double-booking collisions, absence of role-based priority hierarchies, lack of capacity bounds on public events, and no unified audit trail.

### The Solution
**CampusFlow** delivers an end-to-end command-line solution engineered in core Java. It integrates:
- Mathematical interval-based **conflict detection** to guarantee zero double-booking.
- Design-pattern-driven **priority scheduling** (Strategy Pattern) favoring academic and faculty requirements.
- **Role-Based Access Control (RBAC)** providing distinct privilege scopes for Students, Faculty, and Administrators.
- **Event Lifecycle & Attendee Ticketing** that enforces facility physical seating boundaries.
- **Real-Time Operational Analytics & Flat-File Audit Persistence**.

---

## 2. Key Features

- **Facility Inventory & Discovery:** Real-time cataloging of campus spaces by `ResourceType`, seating capacity, physical building block, and maintenance state.
- **Zero-Overlap Conflict Detection:** Mathematical verification of time-slot intersections (`O(1)` per slot) across calendar dates.
- **Priority-Driven Allocation (Strategy Pattern):** Extensible strategy algorithm dynamically computing scheduling weights based on applicant role, duration, and academic purpose.
- **Approval Workflow State Machine:** Strict transitions (`PENDING` $\rightarrow$ `CONFIRMED` / `REJECTED` $\rightarrow$ `CANCELLED`). Faculty bookings receive automatic approval privilege, while student club requests enter the administrative approval queue.
- **Event Scheduling & Capacity Cap RSVP:** Links events directly to confirmed facility reservations, ensuring public RSVPs never exceed physical venue fire-code capacity.
- **Executive Analytics Engine:** Real-time metrics calculating facility utilization, approval ratios, and peak demand hotspots.
- **Thread-Safe In-Memory Data Store & Audit Export:** Concurrent collection data architecture with exportable timestamped audit snapshots.
- **Dual-Mode CLI Execution:**
  - *Interactive Mode:* Menu-driven console UI for human evaluation.
  - *Automated Demo Mode (`--demo`):* Headless verification pipeline suitable for automated evaluation scripts.

---

## 3. Technologies & Tools Used

| Category | Technology / Tool | Purpose |
| :--- | :--- | :--- |
| **Language** | Java Standard Edition (SE 8 / 11 / 17 / 21) | Core application implementation |
| **Paradigm** | Object-Oriented Programming (OOP) | Encapsulation, Polymorphism, Inheritance, Abstraction |
| **Design Patterns** | Strategy Pattern, Repository Pattern, Singleton Store | Modular, extensible codebase structure |
| **Testing** | Standalone Test Runner & JUnit 5 | Zero-dependency verification + standard Maven surefire |
| **Build Tools** | Maven (optional) & Standard `javac` / Shell scripts | Dual build support for any evaluation environment |
| **Version Control** | Git & GitHub | Source tracking and version control |

---

## 4. System Architecture & Package Structure

```
campusflow-system/
├── pom.xml                                   <- Maven project descriptor (Java 8/11/17/21 compatible)
├── build.sh                                  <- Direct build script (supports javac or Maven)
├── run.sh                                    <- Application launcher script
├── test.sh                                   <- Test suite execution script
├── statement.md                              <- Problem statement, scope, target users & features
├── README.md                                 <- Complete project guide & documentation
├── PROJECT_REPORT.html                       <- Printable detailed 15-section project report
├── PROJECT_REPORT.md                         <- Structured project report (PDF source)
├── data/                                     <- Persistence data and exported audit logs
└── src/
    ├── main/java/com/campusflow/
    │   ├── CampusFlowApp.java                <- Main application entry point (Interactive & Demo)
    │   ├── model/
    │   │   ├── Role.java                     <- User authorization levels (ADMIN, FACULTY, STUDENT)
    │   │   ├── ResourceType.java             <- AUDITORIUM, SEMINAR_HALL, COMPUTER_LAB, etc.
    │   │   ├── TimeSlot.java                 <- Interval scheduling & overlap collision detector
    │   │   ├── User.java                     <- User profile entity
    │   │   ├── Facility.java                 <- Physical venue entity with maintenance flags
    │   │   ├── BookingStatus.java            <- Booking lifecycle states
    │   │   ├── Booking.java                  <- Reservation entity with priority scoring
    │   │   └── CampusEvent.java              <- Event entity with thread-safe RSVP roster
    │   ├── exception/
    │   │   ├── CampusFlowException.java       <- Base application checked exception
    │   │   ├── ResourceConflictException.java <- Thrown on time-slot overlap / maintenance
    │   │   ├── ValidationException.java       <- Thrown on illegal business arguments
    │   │   └── UnauthorizedException.java     <- Thrown on insufficient privilege
    │   ├── strategy/
    │   │   ├── PriorityAllocationStrategy.java<- Strategy interface for booking priority
    │   │   ├── StandardAllocationStrategy.java<- FCFS & student club allocation logic
    │   │   └── FacultyPriorityAllocationStrategy.java <- Faculty & academic conference priority
    │   ├── repository/
    │   │   ├── DataStore.java                <- Thread-safe concurrent memory repository
    │   │   └── PersistenceManager.java       <- Bootstrap seeder & audit report exporter
    │   ├── service/
    │   │   ├── AuthService.java              <- Authentication & RBAC enforcement
    │   │   ├── FacilityService.java          <- Facility catalog & maintenance manager
    │   │   ├── BookingService.java           <- Conflict detection engine & booking lifecycle
    │   │   ├── EventService.java             <- Event scheduling & RSVP capacity manager
    │   │   └── AnalyticsService.java         <- Real-time utilization and demand analytics
    │   └── ui/
    │       ├── ConsoleColors.java            <- ANSI terminal formatting helpers
    │       └── CliMenu.java                  <- Interactive console menu navigator
    └── test/java/com/campusflow/
        ├── BookingServiceTest.java           <- JUnit 5 booking & conflict tests
        └── StandaloneTestRunner.java         <- Zero-dependency test harness (pure Java)
```

---

## 5. Step-by-Step Installation & Execution Guide

### Prerequisites
- Any Java Development Kit (JDK 8, 11, 17, or 21+). Check via `java -version` and `javac -version`.
- A bash-compatible shell (Linux / macOS / Git Bash on Windows).
- *(Optional)* Apache Maven 3.6+.

### Method A: Quick Run with Helper Scripts (Recommended)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/<your-username>/campusflow-system.git
   cd campusflow-system
   ```

2. **Make scripts executable:**
   ```bash
   chmod +x build.sh run.sh test.sh
   ```

3. **Build the project:**
   ```bash
   ./build.sh
   ```

4. **Launch the Automated Demonstration Mode (Evaluator Quick Test):**
   ```bash
   ./run.sh --demo
   ```
   *This executes an automated walkthrough of all modules without waiting for user input, validating authentication, booking, conflict detection, admin approvals, event RSVP, and analytics generation.*

5. **Launch the Interactive Terminal Menu:**
   ```bash
   ./run.sh
   ```

---

### Method B: Pure Java Terminal Execution (Zero External Tools)

If Maven is not installed on the evaluation machine, compile directly using `javac`:

```bash
# 1. Compile all source classes into bin directory
mkdir -p bin
javac -d bin -sourcepath src/main/java $(find src/main/java -name "*.java")

# 2. Run the automated demo mode
java -cp bin com.campusflow.CampusFlowApp --demo

# 3. Run the interactive console UI
java -cp bin com.campusflow.CampusFlowApp
```

---

### Method C: Standard Maven Execution

```bash
# Clean, compile, and run all unit tests
mvn clean test

# Package into executable JAR
mvn package

# Run using the generated standalone JAR
java -jar target/campusflow-system-1.0.0.jar --demo
java -jar target/campusflow-system-1.0.0.jar
```

---

## 6. Pre-Configured Demo Credentials

The system initializes with pre-seeded test accounts across all roles:

| Role | Name | Email | Password | Pre-configured Permissions |
| :--- | :--- | :--- | :--- | :--- |
| **Admin** | Dr. Rajesh Sharma | `admin@campusflow.edu` | `admin123` | Full approval authority, maintenance controls, audit export |
| **Faculty** | Prof. Ananya Rao | `ananya.rao@campusflow.edu` | `fac123` | Auto-approved reservations, event creation |
| **Faculty** | Prof. Vikram Sen | `vikram.sen@campusflow.edu` | `fac456` | Auto-approved reservations, priority scheduling |
| **Student** | Aarav Patel | `aarav.p@campusflow.edu` | `stu123` | Reservation request submission, event RSVP |
| **Student** | Diya Mehta | `diya.m@campusflow.edu` | `stu456` | Reservation request submission, event RSVP |

*(Note: The CLI also provides a "Quick Role Select" shortcut so evaluators do not need to type credentials manually.)*

---

## 7. Instructions for Testing

### Running the Standalone Unit Test Suite
To verify all unit tests without requiring Maven or third-party test libraries:

```bash
./test.sh
```

Or manually:
```bash
javac -d bin -sourcepath src/main/java src/test/java/com/campusflow/StandaloneTestRunner.java $(find src/main/java -name "*.java")
java -cp bin com.campusflow.StandaloneTestRunner
```

### Running with Maven Surefire
```bash
mvn test
```

### Test Coverage Summary
- **TimeSlot Overlap Logic:** Tests intersection conditions (boundary, complete containment, partial overlap, different dates).
- **Authentication & RBAC:** Tests valid login, incorrect password rejection, and role-based privilege checks.
- **Facility Registration & Unique Guard:** Tests catalog creation and duplicate facility ID rejection.
- **Interval Conflict Detection Engine:** Deliberately injects an overlapping reservation request and verifies that `ResourceConflictException` is thrown.
- **Admin Approval State Transitions:** Tests `PENDING` $\rightarrow$ `CONFIRMED` lifecycle and privilege validation.
- **Event Capacity Enforcement:** Tests that events exceeding facility capacity are blocked via `ValidationException`.

---

## 8. Command-Line Execution Output Demo (Screenshots / Terminal Output)

### Automated Test & Demo Run (`./run.sh --demo`):

```
╔════════════════════════════════════════════════════════════╗
║        CAMPUSFLOW AUTOMATED SYSTEM DEMONSTRATION           ║
║             Executing Verification Pipeline                ║
╚════════════════════════════════════════════════════════════╝

[STEP 1] Testing Authentication & RBAC
 -> Admin authenticated: Dr. Rajesh Sharma (ADM01) - System Administrator [Estate Office]
 -> Student authenticated: Aarav Patel (STU01) - Student [Computer Science]
 -> Authentication & Session Management: PASSED

[STEP 2] Querying Facility Inventory
 -> Total facilities registered: 5
    [FAC-AUD-01] Sarojini Naidu Central Auditorium (Auditorium, Cap: 600, Loc: Main Campus Block A)
    [FAC-SEM-02] Aryabhata Seminar Hall (Seminar Hall, Cap: 120, Loc: Science Block B)
    [FAC-LAB-03] Alan Turing Computing Lab (Computer Lab, Cap: 75, Loc: Tech Tower C)
    [FAC-CLS-04] Smart Classroom 301 (Smart Classroom, Cap: 90, Loc: Academic Wing D)
    [FAC-SPT-05] Indoor Badminton Arena (Sports Complex, Cap: 150, Loc: Student Activity Center)
 -> Facility Inventory Discovery: PASSED

[STEP 3] Booking Creation & Priority Calculation
 -> Student created reservation: Booking #BKG-9A2E1F | Facility: FAC-SEM-02 | User: STU01 | Slot: 2026-09-21 [14:00 - 17:00] | Status: PENDING | Priority: 10
 -> Status: PENDING (Priority Score: 10)
 -> Booking Request Workflow: PASSED

[STEP 4] Verifying Conflict Detection Engine
 -> Attempting overlapping reservation on FAC-SEM-02 at 2026-09-21 [15:00 - 16:00]...
 -> SUCCESS: Conflict successfully detected and rejected!
    Caught Exception: Time-slot conflict detected with Booking [BKG-9A2E1F] by User [STU01]
 -> Conflict Detection Engine: PASSED

[STEP 5] Admin Review & Approval Queue
 -> Admin approved booking BKG-9A2E1F
 -> Updated Status: CONFIRMED (Approved by Admin Dr. Rajesh Sharma)
 -> Approval Workflow: PASSED

[STEP 6] Event Lifecycle & RSVP Ticketing
 -> Event Created: [EVT-C814D0] VIT Robotics Hackathon 2026 | Org: STU01 | Seats: 0/100 | Published: true
 -> Attendee Diya Mehta RSVP status: SUCCESS
 -> Available Seats: 99 / 100
 -> Event Ticketing & Capacity: PASSED

[STEP 7] Generating Real-Time System Analytics
=========================================================
         CAMPUSFLOW SYSTEM ANALYTICS & INSIGHTS          
=========================================================
  Total Facilities Managed       : 5
  Operational Facilities         : 5
  Facilities Under Maintenance   : 0
---------------------------------------------------------
  Total Reservations Logged      : 2
  Confirmed Active Bookings      : 2
  Pending Review Bookings        : 0
  Rejected / Cancelled Bookings  : 0 / 0
  System Booking Approval Rate   : 100.0%
  High Demand Facility Hotspot   : Sarojini Naidu Central Auditorium (FAC-AUD-01)
=========================================================
 -> Analytics Calculation: PASSED

[STEP 8] Verifying Persistence & Audit Snapshot
 -> Audit report written to: ./data/campusflow_audit_report.txt (Size: 2180 bytes)
 -> State Persistence & Audit Logging: PASSED

════════════════════════════════════════════════════════════
   ALL SYSTEM VERIFICATION TESTS EXECUTED SUCCESSFULLY!     
════════════════════════════════════════════════════════════
```

---

## 9. Project Report
A complete, academic-standard 15-section project report is prepared and included:
- **`PROJECT_REPORT.html`**: Formatted with publication CSS styling. Open in any web browser and select `Print > Save as PDF` to generate the exact PDF for upload to the VITyarthi submission portal.
- **`PROJECT_REPORT.md`**: Markdown representation containing complete UML diagrams, ER diagrams, test matrices, and architectural justifications.
