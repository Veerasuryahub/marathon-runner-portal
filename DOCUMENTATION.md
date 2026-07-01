# Marathon Runner Portal — Project Documentation

Short guide: analysis, how to run, and how to test the console app.

**Project Overview:**
- Single-file Java console application implementing a small in-memory "portal" for runners and admins.
- Main application: [src/MarathonPortal.java](src/MarathonPortal.java)
- Automated test suite: [src/TestPortal.java](src/TestPortal.java)

**Key Features Implemented**
- User registration and login with role-based dashboards (RUNNER / ADMIN).
- Race management (create/edit/deactivate) and registration rules.
- Training log CRUD, pace validation, and auto-flagging outliers.
- Forum with content filtering and moderation queue.
- CSV exports (exports/), security audit log (logs/security.log).

**Default Seed Accounts**
- Admin: email `admin@gmail.com` | password `Admin@Pass123`
- Runner: email `john@gmail.com` | password `Runner@Pass123`

**How to Run (compile + execute)**

Prerequisites:
- Java Development Kit (JDK) 17 or newer installed and on `PATH`.

From a terminal (Windows PowerShell / cmd / macOS / Linux):

```bash
# change to source directory
cd "Marathon Runner Portal/src"

# compile the main app
javac MarathonPortal.java

# run the console application
java MarathonPortal
```

Notes:
- The program creates `exports/` and `logs/` directories in the project root at startup.
- All data is in-memory; exiting the program clears state unless you export CSV files.

**How to Run Automated Tests**

The repository includes `TestPortal.java`, a small program that calls the application's APIs and prints a 14-test summary.

```bash
cd "Marathon Runner Portal/src"
javac TestPortal.java
java TestPortal
```

Expected: tests exercise registration, login lockouts, training validations, race registration rules, forum moderation, and export behaviors. The included `test_report.md` documents the passing run observed in the workspace.

**Files of Interest**
- [src/MarathonPortal.java](src/MarathonPortal.java) — full application source. Main entrypoint and menus.
- [src/TestPortal.java](src/TestPortal.java) — automated QA verification suite.
- [test_report.md](test_report.md) — sample test report output produced by the test run.
- [README.md](README.md) — original README with run instructions and PRD.

**Run-time Artifacts**
- `exports/` — CSV files produced by analytics/export features.
- `logs/security.log` — appended on failed login attempts.

**Limitations & Notes**
- No external database; everything is stored in memory; restarting resets state.
- No network, web UI, or background schedulers; admin actions are manual.
- Passwords are stored in plain text in memory (for demo only). Do not use real credentials.
- Designed for demonstration and educational use only.

**Quick troubleshooting**
- If `javac` is not found, ensure the JDK is installed and `java -version` works.
- If the program cannot write CSV or logs, ensure the current user has write permission in the project folder.

**Next recommended steps (optional)**
- Add unit tests with JUnit to exercise individual classes instead of an integration-style `TestPortal`.
- Replace plaintext password storage with hashed passwords (BCrypt) and add persistent storage.
- Add a build script (Gradle or Maven) and a CI job to run tests automatically.

---
Generated on: 2026-06-30
