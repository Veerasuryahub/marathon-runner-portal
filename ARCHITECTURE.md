# Marathon Runner Portal — ARCHITECTURE

> Simple visual diagrams of the application structure, suitable for beginner explanation.

---

## 1. Project Folder Structure

```
marathon-runner-portal/
│
├── src/
│   ├── MarathonPortal.java   ← Single source file (1505 lines, entire application)
│   └── TestPortal.java       ← Automated QA test suite (14 test cases)
│
├── exports/                  ← CSV files are saved here when admin exports
├── logs/                     ← security.log is written here on failed logins
│
├── README.md                 ← How to run the project + User Stories overview
├── PROGRAM_STRUCTURE.md      ← Detailed code structure explanation
├── ARCHITECTURE.md           ← This file — visual diagrams
├── USER_STORY_MAPPING.md     ← Maps each US01–US20 to code methods
├── CODE_EXPLANATION.md       ← Line-by-line method explanations
├── TEST_REPORT.md            ← Full QA test results
├── DEFECT_ANALYSIS.md        ← Bug analysis and fix log
├── REVIEW_PREPARATION.md     ← Interview Q&A preparation guide
├── PROJECT_FLOW.md           ← Step-by-step program flow
├── test_report.md            ← Original quick test summary
└── .gitignore                ← Excludes .class, exports, logs from Git
```

---

## 2. Main Program Flow

```
┌─────────────────────────────────────────┐
│            main()                       │
│  Create folders: exports/ logs/         │
│  loadSeedData()                         │
│  printBanner()                          │
│  Loop: showMainMenu()                   │
└─────────────────────┬───────────────────┘
                      │
          ┌───────────▼───────────┐
          │      MAIN MENU        │
          │  [1] Register         │
          │  [2] Login            │
          │  [0] Exit             │
          └───┬───────────┬───────┘
              │           │
     ┌────────▼──┐   ┌────▼──────────┐
     │ Register  │   │    Login      │
     │ Runner    │   │ Authenticate  │
     └────────┬──┘   └────┬──────────┘
              │           │
              │    ┌──────▼─────────────────┐
              │    │   Role Check           │
              │    │  ADMIN  or  RUNNER     │
              │    └──────┬────────┬────────┘
              │           │        │
              │   ┌───────▼──┐ ┌───▼───────┐
              │   │  ADMIN   │ │  RUNNER   │
              │   │  MENU    │ │  MENU     │
              │   └───────┬──┘ └───┬───────┘
              │           │        │
              │       Features   Features
              │       (7 items)  (10 items)
              │
          [Loops until Logout or Exit]
```

---

## 3. Internal Class Relationship

```
MarathonPortal (main class)
│
├── ENTITIES (Section 1)
│   ├── User            ─── one entity per person
│   ├── Race            ─── one entity per event
│   ├── TrainingLog     ─── one entity per workout
│   ├── ForumPost       ─── one entity per community post
│   └── Registration    ─── one entity per race signup
│
├── COLLECTIONS (Section 2)
│   ├── ArrayList<User>          → stores all users
│   ├── ArrayList<Race>          → stores all races
│   ├── ArrayList<TrainingLog>   → stores all workouts
│   ├── ArrayList<ForumPost>     → stores all forum posts
│   ├── ArrayList<Registration>  → stores all signups
│   ├── HashMap<String, User>    → fast email-to-user lookup
│   └── HashSet<String>          → prevents duplicate email registration
│
├── MANAGERS (Section 3) — All business rules live here
│   ├── UserMgr        → register(), login(), checkPassword(), logFailedAttempt()
│   ├── RaceMgr        → create(), edit(), deactivate()
│   ├── RegMgr         → register()
│   ├── TrainingMgr    → addLog(), editLog()
│   ├── ForumMgr       → submit(), cleanup()
│   └── AnalyticsMgr   → exportTraining(), exportUsers(), exportRaces(), exportAllLogs()
│
├── MENUS (Section 4–6)
│   ├── showMainMenu()
│   ├── handleRegister()
│   ├── handleLogin()
│   ├── showRunnerMenu()  ─── dispatches to 10 runner features
│   └── showAdminMenu()   ─── dispatches to 7 admin features
│
└── HELPERS (Section 7)
    ├── readStr(), readStrOptional(), readInt(), readDouble()
    ├── println(), ok(), err(), warn(), pause(), line()
    ├── findRaceById(), findUserById()
    └── printBanner()
```

---

## 4. Authentication and Session Flow

```
handleLogin()
│
├── readStr("Email")
├── readStr("Password")
│
└── UserMgr.login(email, password)
      │
      ├── email not found?       → throw Exception (logged to security.log)
      ├── account isLocked()?    → throw Exception (30-min window check)
      ├── status == PENDING?     → throw Exception (must be verified)
      ├── password wrong?
      │     ├── failedAttempts++ (logged to security.log)
      │     ├── attempts >= 5?   → lock account for 30 minutes
      │     └── attempts < 5?    → show remaining attempts
      │
      └── password correct?
            ├── reset failedAttempts = 0
            └── return User object → currentUser = u
                  │
                  ├── ADMIN → showAdminMenu()
                  └── RUNNER → showRunnerMenu()
```

---

## 5. Runner Menu Dispatch Tree

```
showRunnerMenu()
  │
  ├── [1]  viewProfile()        → shows profile, personal bests, allows age/phone edit
  ├── [2]  viewRaces()          → filters and lists open races (hides registered/closed)
  ├── [3]  registerRace()       → RegMgr.register() with all validations
  ├── [4]  viewRegistrations()  → lists all runner's past and current signups
  ├── [5]  addTraining()        → TrainingMgr.addLog() with all validations
  ├── [6]  viewTrainingDashboard() → stats + sortable log list
  ├── [7]  editTraining()       → TrainingMgr.editLog() with 6-month lock check
  ├── [8]  viewForum()          → shows only APPROVED posts
  ├── [9]  postForum()          → ForumMgr.submit() with spam detection
  ├── [10] exportTraining()     → AnalyticsMgr.exportTraining() → saves CSV
  └── [0]  Logout               → exits runner menu loop
```

---

## 6. Admin Menu Dispatch Tree

```
showAdminMenu()
  │
  ├── [1]  adminUsers()
  │          ├── [1] List all users
  │          └── [2] Verify a pending account → u.status = "VERIFIED"
  │
  ├── [2]  adminRaces()
  │          ├── [1] List all races
  │          ├── [2] Add new race → RaceMgr.create()
  │          ├── [3] Edit race    → RaceMgr.edit()
  │          └── [4] Deactivate   → RaceMgr.deactivate()
  │
  ├── [3]  adminForum()
  │          ├── Shows PENDING + FLAGGED posts
  │          ├── [1] Approve post → status = "APPROVED"
  │          ├── [2] Reject post  → status = "REJECTED" + flag author
  │          └── [3] Cleanup posts older than 1 year → ForumMgr.cleanup()
  │
  ├── [4]  adminAnalytics()
  │          └── Shows user counts, race stats, forum stats (read-only display)
  │
  ├── [5]  adminExports()
  │          ├── [1] Export users to CSV   → AnalyticsMgr.exportUsers()
  │          ├── [2] Export races to CSV   → AnalyticsMgr.exportRaces()
  │          └── [3] Export all logs to CSV → AnalyticsMgr.exportAllLogs()
  │
  ├── [6]  adminSecurityLog()
  │          └── Reads logs/security.log and displays each line
  │
  ├── [7]  adminMarkCompleted()
  │          └── Updates reg.status = "COMPLETED" + records completion time
  │
  └── [0]  Logout → exits admin menu loop
```

---

## 7. Validation Chain for Race Registration

```
RegMgr.register(runner, race)
│
├── race.status != "ACTIVE"?         → throw Exception
├── !race.isRegistrationOpen()?      → throw Exception (< 48 hours to start)
├── race.isFull()?                   → throw Exception (at capacity)
├── double-booking on same date?     → throw Exception
├── training logs < 5 in 30 days?    → throw Exception
│
├── race.isWithin7Days()?
│     └── YES → runner.marathonPoints -= 50 (late penalty)
│
└── create Registration object → save to registrations list
```

---

## 8. Data Flow Diagram

```
CONSOLE INPUT (keyboard)
        │
        ▼
  readStr() / readInt() / readDouble()
        │
        ▼
  Menu Method (handleRegister, addTraining, etc.)
        │
        ▼
  Manager Class (UserMgr, TrainingMgr, etc.)
     Validates → throws Exception if invalid
        │
        ▼
  Entity Object created (User, TrainingLog, etc.)
        │
        ▼
  Stored in ArrayList/HashMap/HashSet
        │
        ▼
  CONSOLE OUTPUT (System.out.println)
```

---

## 9. OOP Concepts Used

| OOP Concept | Where Applied |
| :--- | :--- |
| **Encapsulation** | All fields in User, Race, etc. are grouped inside their class. Logic is inside Manager classes, not scattered around. |
| **Abstraction** | Menus call `UserMgr.register()` without knowing its internal validation steps. |
| **Inheritance** | Java standard: `ArrayList` extends `AbstractList`, etc. |
| **Polymorphism** | Lambda comparators in training dashboard sort: `(a, b) -> b.date.compareTo(a.date)` |
| **Static Members** | Collections and ID counters are shared across all methods via `static` fields. |
| **Inner Classes** | Entities and Managers are `public static` inner classes of `MarathonPortal`. |

---

## 10. Java Features Used

| Feature | Example in Code |
| :--- | :--- |
| `ArrayList<T>` | `users`, `races`, `trainingLogs` |
| `HashMap<K,V>` | `userByEmail` — O(1) email lookup |
| `HashSet<T>` | `usedEmails` — O(1) duplicate check |
| `LocalDate / LocalDateTime` | Training dates, race dates, lockout times |
| `DateTimeFormatter` | `dd-MM-yyyy` and `dd-MM-yyyy HH:mm` |
| `ChronoUnit.HOURS/DAYS/MONTHS` | Race window checks, log age checks |
| `Stream API` | Admin analytics: `.stream().filter(...).count()` |
| `Lambda Expressions` | Sort comparators, `removeIf()` |
| `try-with-resources` | `PrintWriter` in CSV exports, `BufferedReader` for log |
| `String.format()` | Table column formatting |
| `switch` expressions | Menu dispatch in runner and admin menus |
| `Math.max()`, `Math.min()` | Points deduction floor at 0 |
| `Scanner` | All keyboard input |
| `File.mkdirs()` | Creates exports/ and logs/ if missing |
