# Marathon Runner Portal — REVIEW PREPARATION

> Complete guide for explaining the project in a TCS ILP Sprint 1 review, including expected questions and model answers.

---

## 1. How to Explain the Project in 2 Minutes

> Use this script when an invigilator asks: *"Explain your project."*

---

**"Our project is the Marathon Runner Portal — a Java Console Application built using Core Java 17, with no database, no Spring Boot, and no external libraries.**

**The system allows two types of users: Runners and Admins.**

**Runners can register, log in, browse upcoming races, register for events, track their training workouts, view performance statistics, and post in a community forum.**

**Admins can verify new runner accounts, create and manage race events, moderate forum posts, view system analytics, and export data to CSV files.**

**The entire project is in one file — MarathonPortal.java — with 1505 lines organized into 7 sections: Data Entities, In-Memory Database using Collections, Business Logic Managers, Main Menus, Runner Features, Admin Features, and Helper IO Functions.**

**We used ArrayList for storing users, races, training logs, posts, and registrations. We used HashMap for fast email-to-user lookup and HashSet for preventing duplicate emails.**

**The project covers 18 out of 20 user stories. The 2 web-only features — email SMTP and background schedulers — are documented as console-adapted alternatives: admin manually verifies accounts, and the 48-hour registration window closes automatically."**

---

## 2. Architecture Explanation

> When asked: *"Explain your architecture."*

**"The project follows a layered architecture pattern:**

**Layer 1 — Entities:** User, Race, TrainingLog, ForumPost, Registration — these are simple Java classes with fields and constructors. They represent real-world objects.

**Layer 2 — Data Store:** Static ArrayList and HashMap collections hold all data in memory. No database file is used.

**Layer 3 — Business Logic:** Six Manager classes contain all validation and business rules. Each manager is responsible for one domain: UserMgr for authentication, RaceMgr for events, RegMgr for race signups, TrainingMgr for workout logs, ForumMgr for community posts, and AnalyticsMgr for CSV exports.

**Layer 4 — UI / Menus:** Methods like showMainMenu, showRunnerMenu, showAdminMenu collect input from the user and delegate to the Manager classes.

**Layer 5 — IO Helpers:** readStr, readInt, readDouble, println, ok, err, warn provide consistent, crash-safe input reading and output formatting."

---

## 3. Expected Invigilator Questions and Answers

### 📋 Project Questions

**Q: Why is everything in one file?**
A: "For simplicity and portability. Any team member can copy one file, run two commands, and the entire application works. Since this is a console application with no build tools like Maven or Gradle, a single file is the cleanest approach."

**Q: What happens to data when the program exits?**
A: "All data is lost because we use in-memory collections — ArrayList, HashMap. There is no file-based storage or database. This is by design for a Core Java Console Application where the scope excludes JDBC and MySQL."

**Q: How does the application know which menu to show?**
A: "After login, we check the `role` field of the User object. If `role.equals("ADMIN")`, we call `showAdminMenu()`. Otherwise we call `showRunnerMenu()`. The two methods have completely separate menu options."

---

### ☕ Java Questions

**Q: What is the difference between ArrayList and HashMap?**
A: "ArrayList is an ordered list — we use it to store all users, races, and training logs. Lookup by index or by looping is O(n). HashMap stores key-value pairs — we use `userByEmail` as a HashMap for O(1) email lookup during login. If we looped through the ArrayList every time someone logged in, it would be slower."

**Q: What is HashSet used for?**
A: "We use `usedEmails` as a HashSet to prevent duplicate email registrations. `HashSet.contains()` runs in O(1) on average, making duplicate checking instant even with thousands of users."

**Q: What is `try-with-resources`?**
A: "It is a Java 7+ feature. When you write `try (PrintWriter w = new PrintWriter(...))`, Java automatically closes the writer when the block ends — even if an exception occurs. We use it in all CSV export methods to ensure the file is always properly closed."

**Q: What is a lambda expression?**
A: "A lambda is a short way to write an anonymous function. We use them in the training dashboard to sort workouts: `(a, b) -> b.date.compareTo(a.date)`. This means 'compare by date in descending order' without writing a full Comparator class."

**Q: What is `ChronoUnit`?**
A: "It's part of the `java.time.temporal` package. We use `ChronoUnit.HOURS.between(now, raceDate)` to calculate the exact number of hours between the current time and the race start. This is how we enforce the 48-hour registration cutoff and the 24-hour edit block."

**Q: What is Stream API?**
A: "Stream API is a Java 8+ feature for functional-style data processing. In `adminAnalytics()` we use `users.stream().filter(u -> u.role.equals("RUNNER")).count()` to count all runners in one readable line instead of writing a loop."

---

### 🏛️ OOP Questions

**Q: Where is Encapsulation used?**
A: "All data (username, password, email) is grouped inside the `User` class. The validation logic is grouped inside `UserMgr`. A menu method calls `UserMgr.register()` without knowing how validation works internally — that's encapsulation."

**Q: Where is Abstraction used?**
A: "`showRunnerMenu()` calls `addTraining()` which calls `TrainingMgr.addLog()`. The menu doesn't know how pace is calculated or how the outlier flag works. It just calls the method and gets a result. The complexity is hidden — that's abstraction."

**Q: Where is Polymorphism used?**
A: "In the training dashboard sorting, we pass different lambda comparators to `sort()`. The same `sort()` method behaves differently depending on which comparator is passed. This is runtime polymorphism through functional interfaces."

**Q: Where is Inheritance used?**
A: "Our entity classes don't extend each other, but we use Java's built-in inheritance: `ArrayList` extends `AbstractList`, `HashMap` extends `AbstractMap`. We also benefit from `LocalDateTime` and `LocalDate` being part of the `java.time` hierarchy."

---

### 📦 Collections Questions

**Q: Why did you use ArrayList instead of LinkedList?**
A: "ArrayList is better for random access and iteration — both of which we do often (display all races, display all logs). LinkedList is faster for frequent insertions/deletions in the middle, which we don't need."

**Q: Could you have used one collection for all data?**
A: "No — different data types need separate lists. A `List<User>` can only store User objects. We need separate lists for Race, TrainingLog, ForumPost, and Registration."

**Q: How do you find a race by ID?**
A: "We use `findRaceById(id)` which loops through the `races` ArrayList and returns the first match. This is O(n). If performance was critical, we would use a `HashMap<String, Race>` for O(1) lookup — similar to how we handle user email lookup."

---

### ⚠️ Exception Questions

**Q: How do you handle exceptions?**
A: "Manager methods throw checked exceptions with meaningful messages like 'Age must be between 18 and 40.' Menu methods wrap manager calls in `try-catch` blocks and display the error using `err(e.getMessage())`. The user is then prompted to press Enter and the menu re-displays."

**Q: What is a checked vs. unchecked exception?**
A: "Checked exceptions must be declared with `throws` or caught — like `IOException` from file writing. Unchecked exceptions extend `RuntimeException` — like `NumberFormatException`, which we catch in `readInt()` to handle invalid user input."

---

### 📖 Validation Questions

**Q: How do you prevent duplicate email registrations?**
A: "We maintain a `HashSet<String> usedEmails`. Before creating any new account, we call `usedEmails.contains(lowerEmail)`. If it returns `true`, we throw an exception. After creating the account, we call `usedEmails.add(lowerEmail)`."

**Q: How does the account lockout work?**
A: "Every wrong password increments `failedAttempts`. When it reaches 5, we set `lockedUntil = LocalDateTime.now().plusMinutes(30)` and change `status = "LOCKED"`. On every subsequent login attempt, `isLocked()` checks if the current time is still before `lockedUntil`. If yes → blocked. If no → auto-unlocked."

**Q: How do you detect spam in forum posts?**
A: "We maintain a `String[] BLACKLIST` with words like `spam, scam, fake, fraud`. Before saving any post, we convert the content to lowercase and call `String.contains()` for each blacklisted word. If a match is found, the post is flagged and the author's `flaggedPosts` counter is incremented."

---

### 📘 User Story Questions

**Q: Which user stories did you not implement?**
A: "US16 (Email Verification) and US18 (Registration Scheduler) cannot be implemented in a console application because they require SMTP servers and background threads. We adapted them: admin manually verifies accounts via the user management menu, and the 48-hour registration window closes automatically based on time calculation."

**Q: How does a runner become eligible to register for a race?**
A: "The runner must have logged at least 5 training workouts in the last 30 days. In `RegMgr.register()`, we loop through `trainingLogs`, filter by `runnerId`, and count entries where `log.date >= today - 30 days`. If the count is less than 5, we throw an exception."

**Q: What is the late registration penalty?**
A: "If a runner registers for a race that starts within 7 days but more than 48 hours away, 50 marathon points are deducted from their balance. The `latePenalty` flag is set to `true` on the Registration object to record this."

---

## 4. How to Handle Difficult Questions

| Situation | Response |
| :--- | :--- |
| You don't know an answer | "That's a great question. In our current design, we handled it by [explain what you did]. A future enhancement could be [mention a better approach]." |
| Invigilator asks about missing feature | "This feature requires [web/database/SMTP], which is outside the scope of a Core Java Console Application. We documented it as a future enhancement." |
| Code doesn't work during demo | "Let me check — it may be a data state issue since all data resets on restart. Our automated test suite (`TestPortal.java`) verifies all business rules and passes 14/14 test cases." |

---

## 5. Key Numbers to Remember

| Metric | Value |
| :--- | :--- |
| Total lines of code | 1,505 |
| Number of Java classes | 12 (5 entities + 6 managers + 1 main) |
| User stories covered | 18 of 20 (2 console-adapted) |
| QA test cases | 14, all passing |
| Default admin password | Admin@Pass123 |
| Default runner password | Runner@Pass123 |
| Seed races | 4 (Mumbai, Delhi, Bangalore, Pune) |
| Seed training logs | 7 (for john_runner) |
| Lockout threshold | 5 wrong passwords |
| Lockout duration | 30 minutes |
| Forum ban threshold | 3 flagged posts |
| Late registration cutoff | Within 7 days |
| Registration close window | 48 hours before race |
| Training log edit lock | 6 months old |
| Password minimum length | 12 characters |
| Runner age range | 18 to 40 |
