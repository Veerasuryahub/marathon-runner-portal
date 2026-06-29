# Marathon Runner Portal — Java Console Application

A simple, complete, and self-contained Java 17 Console Application for managing marathon runner signups, training schedules, event races, and a community board.

The entire application is written in a **single file** (`MarathonPortal.java`). This makes it extremely easy to copy-paste, compile, run, and explain to your team members on any system without dealing with multiple files or package configurations.

---

## 🚀 How to Run the Project (Step-by-Step)

Follow these simple instructions to compile and run the portal on your system.

### Step 1: Open Terminal
Open your terminal (macOS/Linux) or Command Prompt/PowerShell (Windows).

### Step 2: Go to the Folder
Navigate to the directory where `MarathonPortal.java` is saved.
```bash
cd "Marathon Runner Portal/src"
```

### Step 3: Compile the File
Compile the single Java file:
```bash
javac MarathonPortal.java
```

### Step 4: Run the Application
Start the portal:
```bash
java MarathonPortal
```

---

## 👥 Default Test Accounts

Use these pre-loaded credentials to log in and test all portal features instantly:

| Account Type | Email | Password |
|---|---|---|
| **System Admin** | `admin@gmail.com` | `Admin@Pass123` |
| **Runner** | `john@gmail.com` | `Runner@Pass123` |

---

## 📂 Code Structure & Organization

The [MarathonPortal.java](file:///c:/Users/USER/OneDrive/Desktop/Marathon%20Runner%20Portal/src/MarathonPortal.java) file is organized into clear, sequential sections that you can explain to your team members:

1. **SECTION 1: DATA ENTITIES (Models)**
   - Defines simple classes for `User`, `Race`, `TrainingLog`, `ForumPost`, and `Registration`.
2. **SECTION 2: DATA STORAGE (In-Memory Arrays)**
   - Houses lists and hash maps (e.g. `users`, `races`, `trainingLogs`) that serve as our in-memory database.
3. **SECTION 3: BUSINESS LOGIC MODULES**
   - **`UserMgr`:** Handles registration checks, password strength rules (12+ characters, uppercase, digit, symbol), login lockout on 5 consecutive failures, and logs.
   - **`RaceMgr`:** Manages race creation, location/description edits, and deactivation constraints.
   - **`RegMgr`:** Handles race signup eligibility (must have $\ge 5$ logs in 30 days) and same-day scheduling check.
   - **`TrainingMgr`:** Manages workout entries, pace validation (3-10 min/km), and auto-flagging of outlier runs.
   - **`ForumMgr`:** Handles community board posts, banned word scans, and URL routing for review.
   - **`AnalyticsMgr`:** Generates database reports and exports them to `.csv` files.
4. **SECTION 4: USER INTERFACE MENUS**
   - Coordinates the text-based menus (`showMainMenu`, `showRunnerMenu`, `showAdminMenu`).
5. **SECTION 5 & 6: FEATURE IMPLEMENTATIONS**
   - Handles the specific actions triggered by runner and admin choices.
6. **SECTION 7: HELPER IO FUNCTIONS**
   - Simple utilities to read safe inputs from the console keyboard (`readStr`, `readInt`, etc.).

---

## 📝 Not Implemented in Console Version (Web/DB Specific Notes)

The following features cannot be implemented in a simple console-based terminal application and are documented here as notes:
- **Email Verification:** We simulate user registration status. A new runner account starts as `PENDING` and must be manually verified by the admin via option 1 in the admin menu.
- **Database Persistence:** The app uses in-memory collections. All modifications are reset when you exit.
- **Rich Graphics/UI:** All menus are presented using clean ASCII borders.
- **PDF/Excel Export:** Reports are exported as standard `.csv` spreadsheets (saved under `src/exports/`), avoiding external heavy PDF or Excel libraries.
