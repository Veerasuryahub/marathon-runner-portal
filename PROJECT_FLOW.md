# Marathon Runner Portal — PROJECT FLOW

> A step-by-step walkthrough of the entire program from first execution to exit.

---

## 1. Startup Sequence

When you run `java MarathonPortal`:

**Step 1.** JVM calls `main()` at Line 558.

**Step 2.** `new File("exports").mkdirs()` — creates the `exports/` folder if it doesn't exist. This folder holds all exported CSV files.

**Step 3.** `new File("logs").mkdirs()` — creates the `logs/` folder for the security audit log.

**Step 4.** `loadSeedData()` — populates all in-memory lists with starting demo data:
- 1 Admin account (admin@gmail.com)
- 1 Runner account (john@gmail.com, VERIFIED status)
- 4 Races (30, 45, 60, 20 days from today)
- 7 Training logs for john_runner
- 1 Approved forum post

**Step 5.** `printBanner()` — draws the title banner on screen.

**Step 6.** Enters `while(true)` infinite loop that keeps calling `showMainMenu()` until `System.exit(0)` is called.

---

## 2. Main Menu Flow

```
showMainMenu() prints:
  [1] Register as New Runner
  [2] Login
  [0] Exit

User types a number → readInt() validates the range [0–2]
```

**Choice 1 → Registration Flow:**
1. `handleRegister()` is called.
2. User types: Username → Email → Password → Confirm Password → Age → Phone.
3. `UserMgr.register()` runs all validations.
4. If valid: `User` object created, added to `users` list, email added to `userByEmail` and `usedEmails`.
5. Success message printed. Status is `PENDING`.

**Choice 2 → Login Flow:**
1. `handleLogin()` is called.
2. User types: Email → Password.
3. `UserMgr.login()` runs authentication:
   - Looks up email in `userByEmail` HashMap (O(1) lookup).
   - Checks `isLocked()` — if 30-minute window passed, auto-unlocks.
   - Checks `status == "PENDING"` — rejects unverified accounts.
   - Checks password match — increments `failedAttempts`; locks at 5.
4. If valid: `currentUser = u` sets the session.
5. Role check: `ADMIN` → `showAdminMenu()`, `RUNNER` → `showRunnerMenu()`.
6. On logout: `currentUser = null`, returns to main menu.

**Choice 0 → Exit:**
1. Goodbye message printed.
2. `sc.close()` closes the Scanner.
3. `System.exit(0)` terminates the JVM.

---

## 3. Runner Feature Flows

### 3.1 Register for a Race (`registerRace()`)

```
User types Race ID (e.g. RACE-101)
→ findRaceById() searches races ArrayList
→ RegMgr.register(currentUser, race):
    → Check race.status == "ACTIVE"
    → Check race.isRegistrationOpen() [≥48h to start]
    → Check !race.isFull() [capacity check]
    → Loop registrations: check no same-date booking
    → Loop trainingLogs: count logs in last 30 days [must be ≥5]
    → Check race.isWithin7Days() → deduct 50 pts if true
    → Create Registration object → add to registrations list
    → race.registrations++ [update counter]
→ Print Registration ID and any penalty warning
```

### 3.2 Add Training Log (`addTraining()`)

```
User types: Date (dd-MM-yyyy) → Distance → Duration → Notes
→ TrainingMgr.addLog():
    → date.isAfter(today)?      → throw Exception
    → distance < 1 or > 100?   → throw Exception
    → pace < 3 or pace > 10?   → throw Exception
    → duplicate date+distance?  → throw Exception
    → compute average distance of all existing logs
    → distance >= average * 10? → flagged = true
    → Create TrainingLog → add to trainingLogs
→ Print Log ID, Pace, and [FLAG] warning if needed
```

### 3.3 View Training Dashboard (`viewTrainingDashboard()`)

```
Filter trainingLogs by currentUser.id
→ Compute: total distance, avg distance, best distance, avg pace, best pace
→ Print statistics table
→ User chooses sort order: Date / Distance / Pace
→ Sort the list using Collections.sort() with lambda comparator
→ Print sorted table with [EDITED] and [FLAG] badges
```

### 3.4 Post in Forum (`postForum()`)

```
User types: Title → Content
→ ForumMgr.submit(currentUser, title, content):
    → forumBanned == true?        → throw Exception
    → title.length < 5?           → throw Exception
    → content.length < 10?        → throw Exception
    → scan content for BLACKLIST words
    → check for http:// https:// www. links
    → if banned word found:
        → flagReason set, status = "FLAGGED"
        → runner.flaggedPosts++
        → if flaggedPosts >= 3: runner.forumBanned = true
    → else if link found:
        → status = "PENDING" (needs admin review)
    → Create ForumPost → add to forumPosts
→ Print result (flagged / pending / submitted)
```

---

## 4. Admin Feature Flows

### 4.1 Verify Runner Account (`adminUsers()`)

```
Show PENDING accounts list
User types User ID
→ Loop users: find matching id with status == "PENDING"
→ user.status = "VERIFIED"
→ Runner can now log in
```

### 4.2 Create Race (`adminRaces()` → add)

```
Admin types: Name → Location → Date → Distance → Capacity → Description
→ RaceMgr.create():
    → name.length < 5?              → throw Exception
    → duplicate name in same year?  → throw Exception (loops races list)
    → days until race < 7?          → throw Exception
    → capacity < 10?                → throw Exception
    → Create Race object → add to races list
→ Print new Race ID
```

### 4.3 Moderate Forum (`adminForum()`)

```
Show all PENDING and FLAGGED posts
Admin types Post ID
→ [1] Approve: post.status = "APPROVED", flagReason = null
→ [2] Reject:  post.status = "REJECTED", author.flaggedPosts++, ban if ≥3
→ [3] Cleanup: ForumMgr.cleanup() removes posts older than 365 days
```

### 4.4 Export CSV Reports (`adminExports()`)

```
Admin chooses export type:
→ [1] exportUsers()    → writes exports/users_TIMESTAMP.csv
→ [2] exportRaces()    → writes exports/races_TIMESTAMP.csv
→ [3] exportAllLogs()  → writes exports/training_all_TIMESTAMP.csv

Each export uses try-with-resources PrintWriter → file is auto-closed
Comma-separated values written row by row
```

### 4.5 View Security Log (`adminSecurityLog()`)

```
BufferedReader reads logs/security.log line by line
→ Each line printed: "TIMESTAMP | EMAIL | REASON"
→ If file doesn't exist: "No security audit log file exists."
```

---

## 5. Input Reading Flow

All user input goes through helper methods that prevent crashes:

```
readInt(prompt, min, max):
    while(true):
        print prompt
        try:
            parse to integer
            if in range → return value
            else → "Enter a number between X and Y"
        catch NumberFormatException:
            "Invalid format. Enter a whole number."

readStr(prompt):
    while(true):
        print prompt
        read line
        if not empty → return value
        else → "Input cannot be empty"

readStrOptional(prompt):
    print prompt
    read line (may be empty) → return immediately
```

---

## 6. Exception Handling Flow

```
Business logic method throws Exception with a meaningful message
     ↓
Menu method catches it:
     try {
         SomeMgr.doSomething(...);
         ok("Success message");
     } catch (Exception e) {
         err(e.getMessage());    ← displays ✗ ERROR: <message>
     }
     pause();                   ← waits for Enter before returning to menu
```

This pattern is used in: `handleRegister()`, `registerRace()`, `addTraining()`, `editTraining()`, `postForum()`, all admin race operations, all export operations.

---

## 7. Session Management Flow

```
Session starts:    currentUser = <User object>  (after successful login)
Session active:    all menu methods read currentUser.id, .role, .username
Session ends:      currentUser = null            (after logout)
```

There is no token, no timeout timer, and no cookie. The session is simply the Java variable `currentUser`. If the user closes the terminal, everything is lost.

---

## 8. Data Lifecycle

| Event | What Happens |
| :--- | :--- |
| Register user | `User` object created → added to `users` list and `userByEmail` map |
| Login | `userByEmail.get(email)` retrieves user in O(1) |
| Add training log | `TrainingLog` created → added to `trainingLogs` list |
| Register for race | `Registration` created → `race.registrations` incremented |
| Edit profile | Direct field mutation on `User` object |
| Edit training | Direct field mutation on `TrainingLog` object |
| Verify account | Direct `user.status = "VERIFIED"` on User object |
| Deactivate race | Direct `race.status = "INACTIVE"` on Race object |
| Approve post | Direct `post.status = "APPROVED"` on ForumPost object |
| Export CSV | Loop list → write rows to file using `PrintWriter` |
| Program exit | All data lost (in-memory only) |
