# Marathon Runner Portal — Multi-File Code Splitting Guide

If your team members want to split the single-file `MarathonPortal.java` (1,505 lines) into 9 modular, readable files for separate development, use this guide. It lists the exact line ranges from the 1,505-line file to cut and paste into separate files.

---

## 📂 Splitting Structure (Line-by-Line Guide)

When creating these files, make sure to add package imports at the top and change nested inner classes (`public static class Name`) to regular top-level classes (`public class Name`).

### 1. `Models.java`
- **What to Copy:**
  - Imports: `java.time.*`, `java.time.format.*`, `java.time.temporal.ChronoUnit`
  - Lines: **26 to 192** (Includes `User`, `Race`, `TrainingLog`, `ForumPost`, and `Registration` entities).
- **Modification Note:** Remove the `static` keyword from class definitions (e.g. change `public static class User` to `public class User` or keep them nested inside `public class Models` to access them as `Models.User`).

### 2. `DataStore.java`
- **What to Copy:**
  - Imports: `java.util.*`, `java.time.*`
  - Lines: **193 to 224** (Lists, maps, counters, date formatters, and word filter blacklist).
  - Lines: **568 to 607** (The `loadSeedData()` method).
- **Modification Note:** Change the static list types from `User` to `Models.User` (or imports).

### 3. `UserManagement.java`
- **What to Copy:**
  - Lines: **230 to 313** (The `UserMgr` class containing runner signup, password policy validation, login attempts, locked accounts, and security log writer).
- **Modification Note:** Rename `public static class UserMgr` to `public class UserManagement`.

### 4. `RaceManagement.java`
- **What to Copy:**
  - Lines: **314 to 356** (The `RaceMgr` class managing event schedules, capacity checks, and deactivation rules).
- **Modification Note:** Rename `public static class RaceMgr` to `public class RaceManagement`.

### 5. `RegistrationManagement.java`
- **What to Copy:**
  - Lines: **357 to 395** (The `RegMgr` class containing double booking checks, prerequisite log checks, capacity thresholds, and late fee deductions).
- **Modification Note:** Rename `public static class RegMgr` to `public class RegistrationManagement`.

### 6. `TrainingManagement.java`
- **What to Copy:**
  - Lines: **396 to 457** (The `TrainingMgr` class checking workout limits, pace boundaries, and outlier distance flags).
- **Modification Note:** Rename `public static class TrainingMgr` to `public class TrainingManagement`.

### 7. `ForumManagement.java`
- **What to Copy:**
  - Lines: **458 to 498** (The `ForumMgr` class managing posts, banned words flagging, and URL manual approval queue).
- **Modification Note:** Rename `public static class ForumMgr` to `public class ForumManagement`.

### 8. `AnalyticsManagement.java`
- **What to Copy:**
  - Lines: **499 to 557** (The `AnalyticsMgr` class exporting runner workouts, master databases, and scheduled events to CSV files).
- **Modification Note:** Rename `public static class AnalyticsMgr` to `public class AnalyticsManagement`.

### 9. `MarathonPortal.java` (Main UI Orchestrator)
- **What to Keep:**
  - Imports: `java.io.*`, `java.time.*`, `java.util.*`
  - Lines: **1 to 25** (Main class declaration).
  - Lines: **558 to 567** (The `main()` method).
  - Lines: **610 to 1505** (Menus, profile details, registration panels, dashboards, and input reading helpers).

---

## ⚙️ How to Compile and Run the Multi-File Version

Once your team has split the files in the `src/` folder, compile and run them using the following commands:

### On Windows (PowerShell/CMD):
```powershell
# Compile all files at once
javac Models.java DataStore.java UserManagement.java RaceManagement.java RegistrationManagement.java TrainingManagement.java ForumManagement.java AnalyticsManagement.java MarathonPortal.java

# Run the coordinator class
java MarathonPortal
```

### On macOS / Linux Terminal:
```bash
# Compile all source files
javac *.java

# Run the coordinator class
java MarathonPortal
```
