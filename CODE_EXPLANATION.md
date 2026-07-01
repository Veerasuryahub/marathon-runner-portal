# Marathon Runner Portal — CODE EXPLANATION

> Every important method explained clearly — purpose, logic, validations, and examples. Designed to help you explain the code in a Sprint 1 review.

---

## 1. `User.isLocked()` — Line 63

**Purpose:** Checks if a user's account is currently locked due to too many wrong passwords. Also auto-unlocks if the 30-minute window has passed.

**Logic (step by step):**
```java
public boolean isLocked() {
    if (lockedUntil == null) return false;       // Step 1: No lock set at all → not locked
    if (LocalDateTime.now().isAfter(lockedUntil)) { // Step 2: Current time is past the lock end time?
        lockedUntil = null;                      // Step 3: Clear the lock
        failedAttempts = 0;                      // Step 4: Reset wrong password counter
        if (status.equals("LOCKED")) status = "VERIFIED"; // Step 5: Restore VERIFIED status
        return false;                            // Step 6: Not locked anymore
    }
    return true;                                 // Step 7: Still within lock window → locked
}
```

**Why this design?** Instead of a background timer thread, the lock expires passively. Every time someone tries to log in, the method is called and it checks the time. This works perfectly for a console application without needing threads.

---

## 2. `Race.isRegistrationOpen()` — Line 102

**Purpose:** Returns `true` if runners are still allowed to register (i.e., race starts in 48 or more hours).

```java
public boolean isRegistrationOpen() {
    long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), raceDate);
    return hours >= 48;   // Must have at least 48 hours remaining
}
```

**Example:** If race is tomorrow, `hours = 20` → returns `false` → registration is closed.

---

## 3. `Race.isWithin7Days()` — Line 111

**Purpose:** Detects late registrations that should incur a 50-point penalty.

```java
public boolean isWithin7Days() {
    long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), raceDate);
    return hours <= 168 && hours >= 48; // 168 hours = 7 days; 48 hours = registration cutoff
}
```

The condition `hours >= 48` ensures registration is still open even while the late penalty applies.

---

## 4. `TrainingLog.getPace()` — Line 142

**Purpose:** Converts the decimal pace (e.g., 5.5 min/km) into a human-readable format (e.g., `5:30 min/km`).

```java
public String getPace() {
    int minutes = (int) paceMinPerKm;                          // whole minutes: 5
    int seconds = (int) Math.round((paceMinPerKm - minutes) * 60); // decimal * 60 = seconds: 30
    return minutes + ":" + String.format("%02d", seconds) + " min/km"; // "5:30 min/km"
}
```

`String.format("%02d", seconds)` ensures `5:05` is displayed instead of `5:5`.

---

## 5. `UserMgr.register()` — Line 231

**Purpose:** Validates all registration rules and creates a new User account.

**Parameters:** `username`, `email`, `password`, `age`, `phone`

**Return Type:** `User` object

**Validation Chain:**
```
1. username.length() < 3                    → throw "Username must be at least 3 characters."
2. username doesn't match [a-zA-Z0-9_]+     → throw "Username can only contain letters..."
3. email doesn't end @gmail.com or @yahoo.com → throw "Only gmail.com and yahoo.com..."
4. usedEmails.contains(email)               → throw "This email is already registered."
5. checkPassword(password) != null          → throw "Weak password: ..."
6. age < 18 || age > 40                    → throw "Age must be between 18 and 40."
7. phone doesn't match \d{10}              → throw "Phone number must be exactly 10 digits."
```

**On success:**
```java
String newId = "USR-" + userIdCount++;   // Generate unique ID e.g. USR-1002
User newUser = new User(newId, ...);     // Create User object
users.add(newUser);                      // Store in list
userByEmail.put(lowerEmail, newUser);    // Store in map for fast lookup
usedEmails.add(lowerEmail);             // Add to duplicate-check set
return newUser;
```

**Time Complexity:** O(1) for email lookup (HashSet), O(n) only for password character check.

---

## 6. `UserMgr.checkPassword()` — Line 291

**Purpose:** Private helper that validates password strength.

```java
private static String checkPassword(String pwd) {
    if (pwd == null || pwd.length() < 12) return "Must be at least 12 characters long.";
    boolean hasUpper = false, hasDigit = false, hasSpecial = false;
    String specialChars = "!@#$%^&*()_+-=[]{}|;'\",./<>?";
    for (char c : pwd.toCharArray()) {   // O(n) scan of each character
        if (Character.isUpperCase(c)) hasUpper = true;
        if (Character.isDigit(c)) hasDigit = true;
        if (specialChars.indexOf(c) >= 0) hasSpecial = true;
    }
    if (!hasUpper)   return "Must contain at least one uppercase letter.";
    if (!hasDigit)   return "Must contain at least one numeric digit.";
    if (!hasSpecial) return "Must contain at least one special character.";
    return null; // null means password is valid
}
```

Returns `null` on success, or an error message string on failure. The caller checks the return value.

---

## 7. `UserMgr.login()` — Line 256

**Purpose:** Authenticates user credentials and enforces lockout policy.

**Logic flow:**
```
1. email not found in userByEmail?   → logFailedAttempt() + throw Exception
2. user.isLocked() == true?          → throw "Account is locked until ..."
3. user.status == "PENDING"?         → throw "Account pending verification"
4. password wrong?
       → failedAttempts++
       → logFailedAttempt()
       → failedAttempts >= 5?
           → lockedUntil = now + 30 min
           → status = "LOCKED"
           → throw "ACCOUNT LOCKED!"
       → else: throw "Wrong password. X attempts remaining."
5. password correct?
       → failedAttempts = 0 (reset counter on success)
       → return User object
```

---

## 8. `RaceMgr.create()` — Line 315

**Purpose:** Creates a new race event with all business rule validations.

**Key validations:**
```java
// Unique name per year check
for (Race r : races) {
    if (r.name.equalsIgnoreCase(name) && r.raceDate.getYear() == targetYear)
        throw new Exception("A race named '...' already exists in " + targetYear);
}

// Must be at least 7 days in future
long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), date);
if (daysUntil < 7) throw new Exception("Race date must be at least 7 days in future.");
```

Race time is always set to 07:00 AM: `date.atTime(7, 0)`.

**Time Complexity:** O(n) for duplicate name check (loops all existing races).

---

## 9. `RegMgr.register()` — Line 358

**Purpose:** Validates and creates a race registration with prerequisite checks.

**Most complex method in the project — 5 validation steps:**

```java
// Step 1: Race must be active and open
if (!race.status.equals("ACTIVE")) throw ...
if (!race.isRegistrationOpen())    throw ...
if (race.isFull())                 throw ...

// Step 2: No double booking on same date
LocalDate targetDate = race.raceDate.toLocalDate();
for (Registration reg : registrations) {
    if (reg.runnerId.equals(runner.id) && reg.status.equals("REGISTERED")) {
        Race r = findRaceById(reg.raceId);
        if (r != null && r.raceDate.toLocalDate().equals(targetDate))
            throw new Exception("Double booking!");
    }
}

// Step 3: Minimum 5 training logs in last 30 days
int logsCount = 0;
LocalDate cutoff = LocalDate.now().minusDays(30);
for (TrainingLog log : trainingLogs) {
    if (log.runnerId.equals(runner.id) && !log.date.isBefore(cutoff))
        logsCount++;
}
if (logsCount < 5) throw ...

// Step 4: Late penalty check
boolean latePenalty = race.isWithin7Days();
if (latePenalty) runner.marathonPoints = Math.max(0, runner.marathonPoints - 50);

// Step 5: Create and store registration
String regId = "REG-" + regIdCount++;
Registration newReg = new Registration(regId, runner.id, race.id, latePenalty);
registrations.add(newReg);
race.registrations++;
```

**Time Complexity:** O(n) for double-booking check + O(m) for training log count.

---

## 10. `TrainingMgr.addLog()` — Line 397

**Purpose:** Validates and records a new training workout.

**Pace Calculation:**
```java
double pace = (double) durationMinutes / distanceKm;
// Example: 60 minutes / 10 km = 6.0 min/km
```

**Outlier Detection:**
```java
double sum = 0; int count = 0;
for (TrainingLog log : trainingLogs) {
    if (log.runnerId.equals(runnerId)) { sum += log.distanceKm; count++; }
}
double average = sum / count;  // e.g., 10 km average
if (count > 0 && distanceKm >= average * 10) flagged = true; // 100+ km would be flagged
```

---

## 11. `ForumMgr.submit()` — Line 459

**Purpose:** Submits a forum post with spam detection and link routing.

**Blacklist scan:**
```java
String[] BLACKLIST = {"spam", "scam", "fake", "fraud", "hack", "abuse", "illegal"};
String lowerContent = content.toLowerCase();
for (String word : BLACKLIST) {
    if (lowerContent.contains(word)) { foundBannedWord = word; break; }
}
```

**Link detection:**
```java
boolean hasLink = lowerContent.contains("http://") || 
                  lowerContent.contains("https://") || 
                  lowerContent.contains("www.");
```

**Forum ban logic:**
```java
if (flagged) {
    runner.flaggedPosts++;
    if (runner.flaggedPosts >= 3) runner.forumBanned = true; // 3 strikes → banned
}
```

---

## 12. `viewTrainingDashboard()` — Line 939

**Purpose:** Displays training statistics and a sortable list of workouts.

**Statistics computation:**
```java
double totalDist = 0, totalPace = 0;
double bestDist = 0, bestPace = Double.MAX_VALUE;

for (TrainingLog l : logs) {
    totalDist += l.distanceKm;
    totalPace += l.paceMinPerKm;
    if (l.distanceKm > bestDist) bestDist = l.distanceKm;
    if (l.paceMinPerKm < bestPace) bestPace = l.paceMinPerKm; // lower pace = faster
}

double avgDist = totalDist / logs.size();
double avgPace = totalPace / logs.size();
```

**Sorting with lambdas:**
```java
if (sort == 1) sorted.sort((a, b) -> b.date.compareTo(a.date));          // Newest first
if (sort == 2) sorted.sort((a, b) -> Double.compare(b.distanceKm, a.distanceKm)); // Longest first
if (sort == 3) sorted.sort((a, b) -> Double.compare(a.paceMinPerKm, b.paceMinPerKm)); // Fastest first
```

---

## 13. `adminAnalytics()` — Line 1299

**Purpose:** Generates a real-time summary of all system data for the admin.

**Uses Java Stream API:**
```java
long runners  = users.stream().filter(u -> u.role.equals("RUNNER")).count();
long verified = users.stream().filter(u -> u.role.equals("RUNNER") && u.status.equals("VERIFIED")).count();
long pending  = users.stream().filter(u -> u.role.equals("RUNNER") && u.status.equals("PENDING")).count();
```

Stream API is cleaner and more readable than writing a manual loop for each counter.

---

## 14. `readInt()` — Line 1454

**Purpose:** Safely reads an integer from the keyboard within a valid range.

**Why it's important:** Without this method, `Integer.parseInt()` would crash with a `NumberFormatException` if the user types a letter instead of a number.

```java
private static int readInt(String prompt, int min, int max) {
    while (true) {                             // Keep looping until valid input
        System.out.print("> " + prompt + " [" + min + "-" + max + "]: ");
        try {
            int val = Integer.parseInt(sc.nextLine().trim());
            if (val >= min && val <= max) return val;  // Valid → return
            println("Enter a number between " + min + " and " + max + ".");
        } catch (NumberFormatException e) {
            println("Invalid format. Enter a whole number.");
        }
    }
}
```

---

## 15. `ForumMgr.cleanup()` — Line 491

**Purpose:** Removes forum posts that are more than 1 year old.

```java
public static int cleanup() {
    int before = forumPosts.size();
    // removeIf() is a Java 8+ List method that removes elements matching the condition
    forumPosts.removeIf(post -> ChronoUnit.DAYS.between(post.createdAt.toLocalDate(), LocalDate.now()) > 365);
    return before - forumPosts.size(); // Returns how many were deleted
}
```

`removeIf()` is more efficient than manually looping and using an iterator to avoid `ConcurrentModificationException`.
