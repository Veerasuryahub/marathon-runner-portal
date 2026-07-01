# Marathon Runner Portal

**Sprint 1 — Core Java Console Application**  
**Technology:** Core Java 17 · No Database · No Spring Boot · No External Libraries

---

## Project Overview

The **Marathon Runner Portal** is a Java Console Application that allows runners to register for marathon events, maintain training logs, view race history, and interact with a community forum. Administrators can manage races, verify accounts, approve forum posts, and generate CSV reports.

All data is stored **in-memory** using Java Collections (`ArrayList`, `HashMap`, `HashSet`). No database, no file persistence beyond CSV exports.

---

## How to Run

### Prerequisites
- Java Development Kit (JDK) 11 or higher installed
- Verify installation: `java -version` and `javac -version`

### Step 1: Get the code
Copy `src/MarathonPortal.java` to any folder on your computer.

### Step 2: Compile

**Windows (PowerShell / Command Prompt):**
```powershell
cd "Marathon Runner Portal/src"
javac MarathonPortal.java
java MarathonPortal
```

**macOS / Linux:**
```bash
cd "Marathon Runner Portal/src"
javac MarathonPortal.java
java MarathonPortal
```

**Eclipse IDE:**
1. File → New → Java Project. Name: `Marathon Runner Portal`.
2. Right-click `src` → New → Class. Name: `MarathonPortal`. Leave package blank.
3. Delete auto-generated code. Paste the entire contents of `MarathonPortal.java`.
4. Click the green **Run** button (or `Ctrl + F11`).
5. Interact via the Eclipse **Console** panel at the bottom.

### Default Login Credentials

| Role | Email | Password |
| :--- | :--- | :--- |
| Admin | admin@gmail.com | Admin@Pass123 |
| Runner | john@gmail.com | Runner@Pass123 |

---

## Project Architecture

```
MarathonPortal.java (1505 lines, 7 sections)
│
├── Section 1: Data Entities
│   └── User, Race, TrainingLog, ForumPost, Registration
│
├── Section 2: In-Memory Database
│   └── ArrayList, HashMap, HashSet collections
│
├── Section 3: Business Logic Managers
│   └── UserMgr, RaceMgr, RegMgr, TrainingMgr, ForumMgr, AnalyticsMgr
│
├── Section 4: Main UI Menus
│   └── showMainMenu, handleRegister, handleLogin
│
├── Section 5: Runner Features (10 menu items)
│   └── viewProfile, viewRaces, registerRace, addTraining,
│       viewTrainingDashboard, editTraining, viewForum,
│       postForum, exportTraining, viewRegistrations
│
├── Section 6: Admin Features (7 menu items)
│   └── adminUsers, adminRaces, adminForum, adminAnalytics,
│       adminExports, adminSecurityLog, adminMarkCompleted
│
└── Section 7: Helper IO Functions
    └── readStr, readInt, readDouble, ok, err, warn, pause
```

---

## Program Flow

```
main()
  └── loadSeedData() → printBanner() → while(true) → showMainMenu()
          │
          ├── [1] Register  → UserMgr.register() → PENDING status
          ├── [2] Login     → UserMgr.login()
          │         ├── ADMIN  → showAdminMenu() [7 options]
          │         └── RUNNER → showRunnerMenu() [10 options]
          └── [0] Exit
```

---

## Folder Structure

```
marathon-runner-portal/
├── src/
│   ├── MarathonPortal.java        ← Main application (single file)
│   └── TestPortal.java            ← Automated QA test suite (14 tests)
├── exports/                       ← CSV exports saved here
├── logs/                          ← security.log written here
├── README.md                      ← This file
├── PROGRAM_STRUCTURE.md           ← Detailed code structure guide
├── ARCHITECTURE.md                ← Visual architecture diagrams
├── USER_STORY_MAPPING.md          ← Maps US01–US20 to code methods
├── CODE_EXPLANATION.md            ← Method-by-method code walkthrough
├── TEST_REPORT.md                 ← Full QA test results
├── DEFECT_ANALYSIS.md             ← Defect review and analysis
├── REVIEW_PREPARATION.md          ← Sprint 1 interview Q&A guide
├── PROJECT_FLOW.md                ← Step-by-step program flows
└── .gitignore
```

---

## User Stories Coverage

| # | User Story | Status | Key Method |
| :---: | :--- | :---: | :--- |
| US01 | User Registration | ✅ Done | `UserMgr.register()` |
| US02 | Login & Role-Based Dashboard | ✅ Done | `UserMgr.login()` |
| US03 | Race Listing & Filtering | ✅ Done | `viewRaces()` |
| US04 | Race Registration | ✅ Done | `RegMgr.register()` |
| US05 | Training Log | ✅ Done | `TrainingMgr.addLog()` |
| US06 | Training Progress Dashboard | ✅ Done | `viewTrainingDashboard()` |
| US07 | Forum | ✅ Done | `ForumMgr.submit()` |
| US08 | User Profile | ✅ Done | `viewProfile()` |
| US09 | Admin Race Management | ✅ Done | `adminRaces()`, `RaceMgr.*` |
| US10 | Admin Analytics | ✅ Done | `adminAnalytics()` |
| US11 | User Auth Audit Log | ✅ Done | `logFailedAttempt()` |
| US12 | Race CRUD Validation | ✅ Done | `RaceMgr.create/edit()` |
| US13 | Race Registration Validation | ✅ Done | `RegMgr.register()` |
| US14 | Training Log Validation | ✅ Done | `TrainingMgr.addLog()` |
| US15 | Forum Moderation | ✅ Done | `adminForum()`, `ForumMgr.cleanup()` |
| US16 | Email Verification | ⚠️ Adapted | Admin manual verify — see note |
| US17 | Analytics | ✅ Done | `adminAnalytics()` |
| US18 | Registration Scheduler | ⚠️ Adapted | Auto-close 48h — see note |
| US19 | Role-Based Authorization | ✅ Done | Role check in `handleLogin()` |
| US20 | Data Export | ✅ Done | `AnalyticsMgr.*` |

---

## Features Not Implemented (Web-Only Scope)

These features require web infrastructure and are outside the scope of a Core Java Console Application:

| Feature | Reason | Console Alternative |
| :--- | :--- | :--- |
| Database (MySQL/MongoDB) | Needs JDBC, external server | In-memory ArrayList + HashMap |
| REST APIs | Needs Servlet/Spring | Direct method calls |
| JWT Authentication | Needs HTTP headers/sessions | `currentUser` variable as session |
| HTTP Status Codes | Needs HTTP protocol | Exception messages shown on console |
| Email Service (SMTP) | Needs external mail server | Admin manually verifies accounts |
| Browser Session Timers | Needs cookies | Login/logout menu options |
| Scheduled Background Jobs | Needs threads/cron | Admin triggers cleanup manually |
| HTML/CSS User Interface | Needs browser | ASCII console menus |
| PDF Export | Needs Apache POI / iText | CSV files (Excel-compatible) |
| API Authorization (OAuth) | Needs HTTP layer | Role-based menu routing |

---

## OOP Concepts Used

| Concept | Example |
| :--- | :--- |
| **Encapsulation** | `User` class groups all fields; `UserMgr` encapsulates all login/register logic |
| **Abstraction** | Menu calls `UserMgr.register()` without knowing its validation internals |
| **Polymorphism** | Lambda comparators in training sort — same method, different behavior |
| **Static Members** | All collections are `static` — shared across all methods |
| **Inner Classes** | 5 entities + 6 managers as inner static classes |

---

## Collections Used

| Collection | Type | Purpose | Time Complexity |
| :--- | :--- | :--- | :--- |
| `users` | `ArrayList<User>` | All registered users | Add: O(1), Search: O(n) |
| `races` | `ArrayList<Race>` | All race events | Add: O(1), Search: O(n) |
| `trainingLogs` | `ArrayList<TrainingLog>` | All workout records | Add: O(1), Filter: O(n) |
| `forumPosts` | `ArrayList<ForumPost>` | All forum posts | Add: O(1), Filter: O(n) |
| `registrations` | `ArrayList<Registration>` | All race signups | Add: O(1), Filter: O(n) |
| `userByEmail` | `HashMap<String, User>` | Fast email-to-user lookup | O(1) average |
| `usedEmails` | `HashSet<String>` | Duplicate email prevention | O(1) average |

---

## Validation Rules Summary

| Rule | Value |
| :--- | :--- |
| Password minimum length | 12 characters |
| Password must contain | Uppercase + Digit + Special character |
| Allowed email domains | @gmail.com, @yahoo.com |
| Runner age range | 18 to 40 years |
| Phone format | Exactly 10 digits |
| Training distance | 1.0 to 100.0 km |
| Training pace | 3.0 to 10.0 min/km |
| Training edit lock | Logs older than 6 months |
| Race future requirement | At least 7 days from today |
| Race edit block | Within 24 hours of start |
| Race registration cutoff | 48 hours before race |
| Late registration penalty | −50 marathon points if within 7 days |
| Double booking | Same calendar date blocked |
| Login lockout threshold | 5 failed attempts |
| Lockout duration | 30 minutes |
| Forum post ban threshold | 3 flagged posts |
| Forum post minimum | Title ≥5 chars, Content ≥10 chars |
| Race eligibility | ≥5 training logs in last 30 days |

---

## Testing Summary

| Test Suite | Total Tests | Passed | Failed |
| :--- | :---: | :---: | :---: |
| Automated (TestPortal.java) | 14 | 14 | 0 |

Run the tests:
```bash
cd "Marathon Runner Portal/src"
javac TestPortal.java
java TestPortal
```

---

## Sample Console Output

```
  ╔══════════════════════════════════════════════════════════╗
  ║        MARATHON RUNNER PORTAL  —  v1.0.0                ║
  ║        Java Console Application                         ║
  ╠══════════════════════════════════════════════════════════╣
  ║  Default Accounts (ready to use):                       ║
  ║  Admin  → admin@gmail.com    password: Admin@Pass123     ║
  ║  Runner → john@gmail.com     password: Runner@Pass123    ║
  ╚══════════════════════════════════════════════════════════╝

  ────────────────────────────────────────────────────────────
  MAIN MENU
  ────────────────────────────────────────────────────────────
  [1] Register as New Runner
  [2] Login
  [0] Exit
  ────────────────────────────────────────────────────────────
  > Enter choice [0-2]:
```

---

## Future Enhancements (Sprint 2)

- Top 3 Runners leaderboard in analytics dashboard
- Forum Posts CSV export
- Force cancel race with points refund to registered runners
- Admin activity audit trail (log all admin actions)
- Import race schedule from a text file
- Runner-to-runner messaging via forum replies
