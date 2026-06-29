# Marathon Runner Portal — Software Architecture
**Project**: Marathon Runner Portal  
**Technology**: Java 17 Console Application  
**Architecture Style**: Layered Architecture (MVC-Style)  
**Version**: 1.0.0  
**Date**: 2026-06-29  

---

## 1. Architecture Overview

The Marathon Runner Portal follows a **Layered Architecture** pattern with MVC-style separation of concerns. The application is divided into distinct layers, each with a specific responsibility.

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│              (Menu Classes — Console UI)                     │
│    MainMenu | RunnerMenu | AdminMenu | AuthMenu              │
├─────────────────────────────────────────────────────────────┤
│                     SERVICE LAYER                            │
│            (Business Logic & Orchestration)                  │
│  UserService | RaceService | RegistrationService             │
│  TrainingService | ForumService | AnalyticsService           │
│  AuthService | ProfileService | ExportService                │
├─────────────────────────────────────────────────────────────┤
│                   VALIDATION LAYER                           │
│             (Input & Business Rule Validation)               │
│  UserValidator | RaceValidator | TrainingValidator           │
│  ForumValidator | PasswordValidator                          │
├─────────────────────────────────────────────────────────────┤
│                     MODEL LAYER                              │
│               (Domain Objects / Entities)                    │
│  User | Admin | Runner | Race | Registration                 │
│  TrainingLog | ForumPost | Session | SecurityLog             │
├─────────────────────────────────────────────────────────────┤
│                   REPOSITORY LAYER                           │
│           (In-Memory Data Store — Collections)               │
│  DataStore (ArrayList, HashMap, HashSet)                     │
├─────────────────────────────────────────────────────────────┤
│                   UTILITY / SUPPORT LAYER                    │
│  ConsoleHelper | DateUtils | IdGenerator | FileExporter      │
│  SessionManager | PasswordUtils | DisplayFormatter           │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Package Structure

```
MarathonRunnerPortal/
│
├── src/
│   │
│   ├── model/                         # Domain entities
│   │   ├── User.java                  # Abstract base user
│   │   ├── Admin.java                 # Admin extends User
│   │   ├── Runner.java                # Runner extends User
│   │   ├── Race.java                  # Race entity
│   │   ├── Registration.java          # Race registration
│   │   ├── TrainingLog.java           # Training entry
│   │   ├── ForumPost.java             # Forum post
│   │   ├── Session.java               # Login session
│   │   └── SecurityLog.java           # Failed login audit
│   │
│   ├── enums/                         # Enumerations
│   │   ├── UserRole.java              # ADMIN, RUNNER
│   │   ├── AccountStatus.java         # PENDING, VERIFIED, LOCKED
│   │   ├── RaceDistance.java          # FIVE_K, TEN_K, HALF, FULL
│   │   ├── RaceStatus.java            # ACTIVE, INACTIVE
│   │   ├── PostStatus.java            # PENDING, APPROVED, FLAGGED, REJECTED
│   │   └── RegistrationStatus.java    # REGISTERED, CANCELLED, COMPLETED
│   │
│   ├── interfaces/                    # Contracts / Abstractions
│   │   ├── Exportable.java            # export() contract
│   │   ├── Manageable.java            # add/update/delete/findById
│   │   ├── Reportable.java            # generateReport()
│   │   └── Validatable.java           # validate()
│   │
│   ├── exception/                     # Custom exceptions
│   │   ├── InvalidAgeException.java
│   │   ├── DuplicateEmailException.java
│   │   ├── WeakPasswordException.java
│   │   ├── RaceFullException.java
│   │   ├── TrainingException.java
│   │   ├── UnauthorizedAccessException.java
│   │   ├── AccountLockedException.java
│   │   ├── ValidationException.java
│   │   └── DuplicateRegistrationException.java
│   │
│   ├── service/                       # Business logic
│   │   ├── AuthService.java           # Login, logout, lock
│   │   ├── UserService.java           # Register, manage users
│   │   ├── RaceService.java           # CRUD for races
│   │   ├── RegistrationService.java   # Race registration logic
│   │   ├── TrainingService.java       # Training log logic
│   │   ├── ForumService.java          # Forum posts, moderation
│   │   ├── AnalyticsService.java      # Stats computation
│   │   ├── ProfileService.java        # Profile view/edit
│   │   └── ExportService.java         # CSV file export
│   │
│   ├── validation/                    # Validators
│   │   ├── UserValidator.java
│   │   ├── RaceValidator.java
│   │   ├── TrainingValidator.java
│   │   ├── ForumValidator.java
│   │   └── PasswordValidator.java
│   │
│   ├── data/                          # In-memory data store
│   │   └── DataStore.java             # Central collections hub
│   │
│   ├── util/                          # Utilities
│   │   ├── ConsoleHelper.java         # Input reading helpers
│   │   ├── DateUtils.java             # Date calculations
│   │   ├── IdGenerator.java           # UUID/sequential IDs
│   │   ├── PasswordUtils.java         # Hashing simulation
│   │   ├── DisplayFormatter.java      # Table/border printing
│   │   └── SessionManager.java        # Session tracking
│   │
│   ├── menu/                          # Presentation (Console menus)
│   │   ├── MainMenu.java              # Entry point menu
│   │   ├── AuthMenu.java              # Register/Login flow
│   │   ├── RunnerMenu.java            # Runner dashboard menu
│   │   └── AdminMenu.java             # Admin dashboard menu
│   │
│   └── MarathonApp.java               # Main entry point (main())
│
├── exports/                           # CSV output directory
├── logs/                              # Security/audit logs
├── RequirementAnalysis.md
├── Architecture.md
├── TEST_PLAN.md
├── README.md
├── LICENSE
└── .gitignore
```

---

## 3. Class Diagram (Text Representation)

```
[User (abstract)]
  ├─ id: String
  ├─ username: String
  ├─ email: String
  ├─ passwordHash: String
  ├─ age: int
  ├─ phone: String
  ├─ role: UserRole
  ├─ status: AccountStatus
  ├─ createdAt: LocalDateTime
  ├─ failedAttempts: int
  ├─ lockedUntil: LocalDateTime
  ├─ getRole(): UserRole  [abstract]
  └─ getDashboardInfo(): String  [abstract]
      │
      ├── [Admin extends User]
      │    ├─ department: String
      │    ├─ getRole(): UserRole {ADMIN}
      │    └─ getDashboardInfo(): String
      │
      └── [Runner extends User]
           ├─ marathonPoints: int
           ├─ isBannedFromForum: boolean
           ├─ flaggedPostCount: int
           ├─ getRole(): UserRole {RUNNER}
           └─ getDashboardInfo(): String

[Race]
  ├─ id: String
  ├─ name: String
  ├─ location: String
  ├─ date: LocalDateTime
  ├─ distance: RaceDistance
  ├─ status: RaceStatus
  ├─ maxCapacity: int
  ├─ currentRegistrations: int
  ├─ createdBy: String (adminId)
  ├─ lastModifiedAt: LocalDateTime
  └─ lastModifiedBy: String

[Registration]
  ├─ id: String
  ├─ runnerId: String
  ├─ raceId: String
  ├─ registeredAt: LocalDateTime
  ├─ status: RegistrationStatus
  └─ completionTime: int (minutes, if completed)

[TrainingLog]
  ├─ id: String
  ├─ runnerId: String
  ├─ date: LocalDate
  ├─ distanceKm: double
  ├─ durationMinutes: int
  ├─ paceMinPerKm: double  (computed)
  ├─ createdAt: LocalDateTime
  ├─ modifiedAt: LocalDateTime
  ├─ isFlagged: boolean
  └─ isEdited: boolean  (computed)

[ForumPost]
  ├─ id: String
  ├─ authorId: String
  ├─ title: String
  ├─ content: String
  ├─ status: PostStatus
  ├─ createdAt: LocalDateTime
  ├─ approvedAt: LocalDateTime
  ├─ containsLink: boolean
  └─ isFlagged: boolean

[Session]
  ├─ userId: String
  ├─ role: UserRole
  ├─ loginTime: LocalDateTime
  └─ isActive: boolean

[SecurityLog]
  ├─ id: String
  ├─ email: String
  ├─ attemptTime: LocalDateTime
  ├─ reason: String
  └─ ipAddress: String  ("console" for console app)
```

---

## 4. Interface Diagram

```
«interface» Exportable
  └─ void export(String filePath)

«interface» Manageable<T>
  ├─ void add(T entity)
  ├─ boolean update(T entity)
  ├─ boolean delete(String id)
  └─ T findById(String id)

«interface» Reportable
  └─ String generateReport()

«interface» Validatable
  └─ boolean validate()

Implementations:
  RaceService     → implements Manageable<Race>, Reportable
  UserService     → implements Manageable<User>
  TrainingService → implements Manageable<TrainingLog>
  ForumService    → implements Manageable<ForumPost>
  ExportService   → implements Exportable
  AnalyticsService → implements Reportable
```

---

## 5. Data Flow Diagram

```
USER INPUT (Console)
        │
        ▼
  [Menu Classes]  ─── reads input ──► [ConsoleHelper]
        │
        ▼
  [Service Layer]  ─── validates via ──► [Validators]
        │                                     │
        │  ─── throws ──────────────► [Custom Exceptions]
        │
        ▼
  [DataStore]  ◄──────── stores/retrieves ────────────┐
  (ArrayList<User>)                                    │
  (ArrayList<Race>)                                    │
  (ArrayList<TrainingLog>)                             │
  (ArrayList<ForumPost>)                               │
  (ArrayList<Registration>)                            │
  (ArrayList<SecurityLog>)                             │
  (HashMap<String, User>)    ← email index             │
  (HashSet<String>)          ← email uniqueness        │
        │                                              │
        ▼                                              │
  [ExportService] ──────── writes CSV ──► [exports/ directory]
        │
        ▼
  CONSOLE OUTPUT (formatted via DisplayFormatter)
```

---

## 6. Module Interaction Diagram

```
┌──────────┐    login      ┌─────────────┐    session   ┌──────────────┐
│AuthMenu  │──────────────►│ AuthService │─────────────►│SessionManager│
└──────────┘               └─────────────┘              └──────────────┘
                                  │ failed attempt
                                  ▼
                           ┌─────────────┐
                           │SecurityLog  │──── writes ──► logs/security.log
                           └─────────────┘

┌──────────────┐  register  ┌─────────────┐  validate  ┌───────────────┐
│  AuthMenu    │───────────►│ UserService │───────────►│UserValidator  │
└──────────────┘            └─────────────┘            └───────────────┘
                                  │                          │ throws
                                  ▼                          ▼
                           ┌─────────────┐  ┌──────────────────────────┐
                           │  DataStore  │  │ DuplicateEmailException   │
                           │ (storage)   │  │ WeakPasswordException     │
                           └─────────────┘  │ InvalidAgeException       │
                                            └──────────────────────────┘

┌────────────┐  register   ┌──────────────────────┐  validate ┌──────────────┐
│ RunnerMenu │────────────►│ RegistrationService  │──────────►│RaceValidator │
└────────────┘             └──────────────────────┘           └──────────────┘
                                  │                                  │
                                  ▼                                  │ throws
                           ┌─────────────┐             ┌────────────────────┐
                           │ RaceService │             │ RaceFullException   │
                           └─────────────┘             │ TrainingException   │
                                                        └────────────────────┘
```

---

## 7. OOP Concepts Mapping

| Concept | Where Used |
|---------|-----------|
| **Encapsulation** | All model classes with private fields + getters/setters |
| **Inheritance** | Admin, Runner extend abstract User |
| **Polymorphism** | getDashboardInfo() overridden in Admin/Runner |
| **Abstraction** | User (abstract class), all 4 interfaces |
| **Method Overriding** | getDashboardInfo(), toString() in all models |
| **Method Overloading** | findUser(email), findUser(id), findUser(role) in UserService |
| **Interfaces** | Exportable, Manageable, Reportable, Validatable |
| **Generics** | Manageable<T>, DataStore generic methods |
| **Comparable** | Race implements Comparable<Race> (by date) |
| **Comparator** | TrainingLog sorted by date, distance, pace |
| **Enums** | UserRole, AccountStatus, RaceDistance, RaceStatus, PostStatus, RegistrationStatus |
| **Custom Exceptions** | 9 custom exception classes |
| **Collections** | ArrayList (lists), HashMap (lookup), HashSet (uniqueness) |
| **File Handling** | ExportService writes CSV; SecurityLog writes audit file |
| **Static Members** | IdGenerator.nextId(), DataStore.getInstance(), banned word list |

---

## 8. Data Storage Design

```
DataStore (Singleton)
│
├── ArrayList<User>            users           — all registered users
├── ArrayList<Race>            races           — all races
├── ArrayList<Registration>    registrations   — all race registrations
├── ArrayList<TrainingLog>     trainingLogs    — all training entries
├── ArrayList<ForumPost>       forumPosts      — all forum posts
├── ArrayList<SecurityLog>     securityLogs    — failed login audit trail
│
├── HashMap<String, User>      userByEmail     — fast lookup by email
├── HashMap<String, User>      userById        — fast lookup by ID
├── HashMap<String, Race>      raceById        — fast lookup by ID
│
└── HashSet<String>            registeredEmails — uniqueness check
```

---

## 9. Security Design (Console Simulation)

- **Password**: SHA-256 hashing simulation via `PasswordUtils`
- **Account Lock**: After 5 failed attempts → `lockedUntil = now + 30 min`
- **Roles**: Enforced in every service method via `SessionManager.currentUser.getRole()`
- **Admin Verification**: Admins verify runner accounts (simulating email verification)
- **Audit Log**: All failed logins written to `logs/security.log`

---

## 10. Error Handling Strategy

All exceptions follow this hierarchy:
```
Exception
└── MarathonException (base)
    ├── ValidationException
    │   ├── InvalidAgeException
    │   ├── WeakPasswordException
    │   └── DuplicateEmailException
    ├── RaceFullException
    ├── TrainingException
    ├── UnauthorizedAccessException
    ├── AccountLockedException
    ├── DuplicateRegistrationException
    └── ForumException
```

---
*End of Architecture Document — Phase 2 Complete*
