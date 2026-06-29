# Marathon Runner Portal

The Marathon Runner Portal is a Java Console Application designed to assist runners in registering for events, maintaining logs of training workouts, checking metrics dashboards, participating in a moderated forum board, and managing files/statistics. Administrators have dedicated tools to configure races, manage user registrations, moderate forum boards, audit security records, and export CSV spreadsheets.

The entire application runs in the console terminal (no database, no Spring Boot, no external dependencies required) and is fully contained within [src/MarathonPortal.java](file:///c:/Users/USER/OneDrive/Desktop/Marathon%20Runner%20Portal/src/MarathonPortal.java).

---

## 🚀 How to Run the Project (Step-by-Step)

Follow these instructions to compile and launch the application on any system:

### 1. Prerequisites
- **Java Development Kit (JDK) 17 or higher** installed.
- Verify your Java installation:
  ```bash
  java -version
  ```

### 2. Execution Commands

#### **On Windows (PowerShell or Command Prompt):**
```powershell
# Step 1: Navigate to the source folder
cd "Marathon Runner Portal/src"

# Step 2: Compile the single Java file
javac MarathonPortal.java

# Step 3: Run the Application
java MarathonPortal
```

#### **On macOS / Linux Terminal:**
```bash
# Step 1: Navigate to the source folder
cd "Marathon Runner Portal/src"

# Step 2: Compile the Java file
javac MarathonPortal.java

# Step 3: Run the application
java MarathonPortal
```

---

## 👥 Default Accounts (Pre-Loaded Seed Data)

Use these credentials to log in and test all system behaviors instantly without needing to register first:

| Role | Email Address | Password |
|---|---|---|
| **System Administrator** | `admin@gmail.com` | `Admin@Pass123` |
| **Portal Runner** | `john@gmail.com` | `Runner@Pass123` |

---

# 📋 Project Requirement Document (PRD)

Below are the details of the project requirements and user stories implemented in this application:

## User Stories

### US01 - User Registration
- **Description:** A new runner can create a login account.
- **Console Rules & Checks:**
  - Email address must be unique (checked in constant time using `HashSet`).
  - Passwords must be at least 12 characters and contain at least one uppercase letter, one digit, and one special character.
  - Email domain must end with `@gmail.com` or `@yahoo.com`.
  - Runners must be between 18 and 40 years old.
  - Account status starts as `PENDING` (runners cannot log in until an admin verifies their account via the verification panel).
  - Creation dates are stored in the system.
  - Verified runners automatically receive 500 **Marathon Points** on registration.

### US02 - Login & Role-Based Dashboard
- **Description:** Users log in to access specific menus.
- **Console Rules & Checks:**
  - Multiple failed passwords trigger account lockouts. On the 5th failed login attempt, the account is locked for 30 minutes.
  - Runners view race registers, workout logs, training stats, and community boards.
  - Admins view verification panels, race configuration panels, forum reviews, security logs, and exports.

### US03 - Race Listing & Filtering
- **Description:** Runners search for upcoming events.
- **Console Rules & Checks:**
  - Hides races that are inactive or starting within 48 hours.
  - Hides races that the runner is already registered for.
  - Supports filters by location and distance (5K, 10K, Half Marathon, Full Marathon).

### US04 - Race Registration
- **Description:** Runners sign up for active races.
- **Console Rules & Checks:**
  - Runner must have logged at least 5 training sessions in the last 30 days.
  - Cannot register for two different races scheduled on the same calendar day.
  - Displays last modified timestamps if an admin updated the race details.

### US05 - Training Log
- **Description:** Runners log their daily training runs.
- **Console Rules & Checks:**
  - Date of training cannot be in the future.
  - Distance must be between 1 km and 100 km (protects against typings).
  - Prevents duplicate log entries (same date + same distance).
  - Lockout edits on logs older than 6 months.
  - Displays recency display indicators (e.g. *"Logged 3 days ago"*, *"Logged today"*).
  - Appends an `[EDITED]` badge to logs that have been modified.

### US06 - Training Progress Dashboard
- **Description:** Runners monitor workouts.
- **Console Rules & Checks:**
  - Displays total workouts, cumulative km, average pace (formatted as `minutes:seconds min/km`), best distance, and fastest pace.
  - Sorts workouts by date, distance, and pace.
  - Exports training logs to local CSV files.

### US07 - Forum
- **Description:** Runners post in community boards.
- **Console Rules & Checks:**
  - Scans content against inappropriate words (spam, scam, fake, fraud, hack, abuse, illegal). Flagged posts are held from publication.
  - Admins must approve posts before they become visible to other runners.
  - If a runner gets 3 flagged posts, they are automatically forum banned.

### US08 - User Profile
- **Description:** Runners view past race histories.
- **Console Rules & Checks:**
  - Displays completed races only.
  - Displays personal best times grouped by distance category.
  - Runners can update age and phone but cannot modify email, username, or user ID.

### US09 - Admin Race Management
- **Description:** Admins manage races.
- **Console Rules & Checks:**
  - Prevents deactivation or deletion of races if runners have already registered.
  - Distances must match: 5K, 10K, Half Marathon, Full Marathon.

### US10 - Admin Analytics Dashboard
- **Description:** Admins view system-wide metrics.
- **Console Rules & Checks:**
  - Tracks total user roles, verified count, pending count, active races, signup slots filled, forum queue size, and logs.
  - Exports data tables to CSV reports.

### US11 - User Authentication (Failed attempts audit log)
- Writes all password errors and unauthorized login attempts to `logs/security.log`.

### US12 - Race CRUD Validation
- New race dates must be at least 7 days in the future.
- Race names must be unique within the same year.
- Admins cannot edit race details if it starts in less than 24 hours.

### US13 - Race Registration Validation
- Limits signups based on max capacity.
- Deducts 50 marathon points for late registrations (signing up within 7 days of the race date).

### US14 - Training Log Validation
- Rejects pace values faster than 3 min/km or slower than 10 min/km.
- Auto-flags training workouts if the logged distance is $\ge 10\times$ the runner's running average.

### US15 - Forum Moderation
- Forum posts containing URLs/hyperlinks require manual admin approval before publication.
- Deletes posts older than 1 year.

---

# 💾 Database Schema (In-Memory collections)

The application models are structured in Java using memory-safe objects:

```java
// Models.User
String id;
String username;
String email;
String password;
String phone;
String role;
String status;
int age;
int marathonPoints;
int flaggedPosts;
boolean forumBanned;
LocalDateTime createdAt;

// Models.Race
String id;
String name;
String location;
String distance;
String status;
LocalDateTime raceDate;
int maxCapacity;
int registrations;

// Models.TrainingLog
String id;
String runnerId;
LocalDate date;
double distanceKm;
int durationMinutes;
double paceMinPerKm;
boolean flagged;
LocalDateTime modifiedAt;

// Models.ForumPost
String id;
String authorId;
String authorName;
String title;
String content;
String status;
String flagReason;
boolean containsLink;

// Models.Registration
String id;
String runnerId;
String raceId;
String status;
boolean latePenalty;
int completionMinutes;
```

---

# 💻 Console Application Scope & Limitations

This project is a dedicated **Java Console Application**. Features that require web architecture or external server configurations are not coded in the source files, but are documented below as limitations of a terminal application:

### 🚫 Features Not Implemented (Web-Only)
- **Database (MySQL/MongoDB):** The data is held in-memory within Java Collections. All registrations and logs reset when the terminal process exits.
- **REST APIs & JWT:** User session tracking is managed locally via session classes instead of JWT tokens.
- **Email Service (SMTP):** Runner account verification is performed manually by system admins via the user manager menu instead of automated activation emails.
- **Browser Cookies/Session Timers:** The terminal app stays active until the user selects option 0, rather than expiring cookies.
- **Scheduled Background Jobs:** Automatic post deletion and race closures are triggered manually by admins through menu selections, avoiding background thread requirements.
- **PDF & Excel Libraries:** Reports are generated in `.csv` format (universally openable by Excel), which removes the need for large external libraries like Apache POI or iText.
