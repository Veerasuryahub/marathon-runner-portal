# Marathon Runner Portal — PROGRAM STRUCTURE

> A beginner-friendly explanation of every part of the program, from startup to exit.

---

## 1. Application Starting Point

The application begins at one place and one place only:

```java
public static void main(String[] args)   // Line 558
```

When you run `java MarathonPortal`, Java automatically calls this method. Here is exactly what happens in order:

```
main()
  ├── Creates folders: exports/ and logs/  (line 559–560)
  ├── loadSeedData()                       (line 561) — fills demo accounts and races
  ├── printBanner()                        (line 562) — prints the title box
  └── while(true) loop → showMainMenu()   (line 563–565) — keeps the app running
```

---

## 2. The 7 Sections of the Program

The 1,505-line file is divided into 7 clearly labelled sections:

| Section | Line Range | What It Contains |
| :--- | :---: | :--- |
| **Section 1** | 26–191 | Data Entities (5 classes: User, Race, TrainingLog, ForumPost, Registration) |
| **Section 2** | 193–223 | In-Memory Database (ArrayList, HashMap, HashSet collections + counters) |
| **Section 3** | 225–556 | Business Logic (6 manager classes: UserMgr, RaceMgr, RegMgr, TrainingMgr, ForumMgr, AnalyticsMgr) |
| **Section 4** | 606–754 | Main UI Menus (showMainMenu, handleRegister, handleLogin, showRunnerMenu, showAdminMenu) |
| **Section 5** | 756–1104 | Runner Sub-Features (viewProfile, viewRaces, registerRace, addTraining, viewTrainingDashboard, editTraining, viewForum, postForum, exportTraining) |
| **Section 6** | 1106–1424 | Admin Sub-Features (adminUsers, adminRaces, adminForum, adminAnalytics, adminExports, adminSecurityLog, adminMarkCompleted) |
| **Section 7** | 1426–1503 | Helper IO Functions (readStr, readInt, readDouble, println, ok, err, warn, pause, printBanner) |

---

## 3. Data Entities — Section 1 (Lines 26–191)

These are the 5 classes that represent real-world objects stored in memory.

### User (Line 30)
Represents a person (Admin or Runner).

| Field | Type | Purpose |
| :--- | :--- | :--- |
| `id` | String | Unique key: USR-1001 |
| `username` | String | Display name |
| `email` | String | Login key |
| `password` | String | Login credential |
| `role` | String | "ADMIN" or "RUNNER" |
| `status` | String | "PENDING", "VERIFIED", or "LOCKED" |
| `marathonPoints` | int | Reward currency, starts at 500 for runners |
| `failedAttempts` | int | Tracks consecutive wrong passwords |
| `lockedUntil` | LocalDateTime | Set when account locks |
| `forumBanned` | boolean | True after 3 flagged posts |
| `flaggedPosts` | int | Count of banned/rejected posts |

**Key Method:** `isLocked()` — checks if the 30-minute lockout window has expired and auto-unlocks.

### Race (Line 75)
Represents a marathon event.

| Field | Type | Purpose |
| :--- | :--- | :--- |
| `id` | String | RACE-101 |
| `name` | String | "Mumbai Marathon 2026" |
| `raceDate` | LocalDateTime | Scheduled event time |
| `distance` | String | "5K", "10K", "Half Marathon", "Full Marathon" |
| `status` | String | "ACTIVE" or "INACTIVE" |
| `registrations` | int | Count of signed-up runners |
| `maxCapacity` | int | Limit of runners allowed |
| `lastModified` | LocalDateTime | Timestamps edits for display |

**Key Methods:** `isRegistrationOpen()` checks 48-hour cutoff. `isFull()` checks capacity. `isWithin7Days()` detects late registrations.

### TrainingLog (Line 117)
Represents one workout entry.

| Field | Type | Purpose |
| :--- | :--- | :--- |
| `id` | String | TRN-5001 |
| `runnerId` | String | Links to a User |
| `date` | LocalDate | When the run happened |
| `distanceKm` | double | How far (1–100 km) |
| `durationMinutes` | int | How long (1–1440 min) |
| `paceMinPerKm` | double | Auto-calculated from distance/duration |
| `flagged` | boolean | True if distance is ≥10x the runner's average |
| `modifiedAt` | LocalDateTime | Non-null means it was edited — shows `[EDITED]` badge |

**Key Method:** `getPace()` formats the decimal pace as `5:30 min/km`.

### ForumPost (Line 149)

| Field | Type | Purpose |
| :--- | :--- | :--- |
| `status` | String | "PENDING", "APPROVED", "REJECTED", or "FLAGGED" |
| `containsLink` | boolean | True if post has http:// or www. |
| `flagReason` | String | Explains why it was flagged |

### Registration (Line 173)

| Field | Type | Purpose |
| :--- | :--- | :--- |
| `status` | String | "REGISTERED" or "COMPLETED" |
| `latePenalty` | boolean | True if signed up within 7 days of race |
| `completionMinutes` | int | Admin-recorded finish time |

---

## 4. In-Memory Database — Section 2 (Lines 193–223)

No database is used. All data lives in Java collections:

```java
List<User>         users         = new ArrayList<>();   // All users
List<Race>         races         = new ArrayList<>();   // All races
List<TrainingLog>  trainingLogs  = new ArrayList<>();   // All workout logs
List<ForumPost>    forumPosts    = new ArrayList<>();   // All posts
List<Registration> registrations = new ArrayList<>();   // All signups

Map<String, User>  userByEmail   = new HashMap<>();     // Fast email lookup
Set<String>        usedEmails    = new HashSet<>();     // Duplicate email guard
```

**ID Counters** auto-increment for each new record:
```
userIdCount = 1002  → USR-1002, USR-1003 ...
raceIdCount = 105   → RACE-105, RACE-106 ...
logIdCount  = 5010  → TRN-5010, TRN-5011 ...
postIdCount = 3002  → POST-3002, POST-3003 ...
regIdCount  = 8001  → REG-8001, REG-8002 ...
```

---

## 5. Business Logic Managers — Section 3 (Lines 225–556)

Six manager classes contain all rules:

### UserMgr (Line 230)
- `register()` — validates and creates runner accounts
- `login()` — verifies credentials, enforces lockout
- `checkPassword()` — private helper, checks 12-char/uppercase/digit/special
- `logFailedAttempt()` — writes to `logs/security.log`

### RaceMgr (Line 314)
- `create()` — validates and creates race events (7-day future rule, unique name per year)
- `edit()` — updates race details (blocked within 24h of start)
- `deactivate()` — marks race INACTIVE (blocked if registrations exist)

### RegMgr (Line 357)
- `register()` — checks ACTIVE status, 48h window, capacity, double-booking, 5-log prerequisite, late penalty

### TrainingMgr (Line 396)
- `addLog()` — validates date (no future), distance (1–100 km), pace (3–10 min/km), no duplicate, outlier flag
- `editLog()` — validates 6-month edit lockout, re-validates pace after changes

### ForumMgr (Line 458)
- `submit()` — checks ban, title/content length, scans blacklist words, detects links, manages flagged count and ban
- `cleanup()` — removes posts older than 365 days

### AnalyticsMgr (Line 499)
- `exportTraining()` — runner's logs to CSV
- `exportUsers()` — full user list to CSV
- `exportRaces()` — race schedule to CSV
- `exportAllLogs()` — all training data to CSV

---

## 6. Menu Flow — Section 4 (Lines 606–754)

```
main()
  └── while(true)
        └── showMainMenu()
              ├── [1] handleRegister()  → collect info → UserMgr.register()
              ├── [2] handleLogin()     → UserMgr.login()
              │         ├── ADMIN → showAdminMenu()  (loop until logout)
              │         └── RUNNER → showRunnerMenu() (loop until logout)
              └── [0] System.exit(0)
```

---

## 7. How Objects Are Created

```java
// 1. Data is read from console
String username = readStr("Username");

// 2. Manager validates and builds the object
User u = UserMgr.register(username, email, password, age, phone);

// 3. Object is stored in the List and Map
users.add(u);
userByEmail.put(email, u);
usedEmails.add(email);
```

---

## 8. How Objects Are Stored

All objects live in **static lists** inside `MarathonPortal`. Because they are static, every method in the class can access them directly.

```java
public static List<User> users = new ArrayList<>();
```

There is no file, no database, and no network. If the program exits, all data is lost — which is expected for an in-memory console application.

---

## 9. How Objects Are Updated

Objects are retrieved from the list, then their fields are modified directly:

```java
// Find the user
User user = findUserById(id);
// Change the field
user.age = newAge;
// No need to "save" — the list already holds a reference to the same object
```

---

## 10. How Objects Are Displayed

Data is formatted using `System.out.println()` through the `println()` helper, with `String.format()` for table columns:

```java
println(String.format("  %-10s %-15s %-28s", u.id, u.username, u.email));
```

---

## 11. Helper IO Functions — Section 7 (Lines 1426–1503)

| Method | Purpose |
| :--- | :--- |
| `readStr(prompt)` | Keeps asking until user types something non-empty |
| `readStrOptional(prompt)` | Accepts empty input (for optional fields) |
| `readInt(prompt, min, max)` | Keeps asking until a valid integer in range is given |
| `readDouble(prompt, min, max)` | Same but for decimal numbers |
| `println(msg)` | Shorthand for `System.out.println()` |
| `ok(msg)` | Prints green-style `✓ SUCCESS:` message |
| `err(msg)` | Prints `✗ ERROR:` message |
| `warn(msg)` | Prints `⚠ WARNING:` message |
| `pause()` | Prints "Press Enter to continue..." and waits |
| `line()` | Prints a horizontal divider of 60 dashes |
| `printBanner()` | Prints the application title box on startup |
| `findRaceById(id)` | Searches `races` list for matching ID |
| `findUserById(id)` | Searches `users` list for matching ID |
