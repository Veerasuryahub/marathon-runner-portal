# Marathon Runner Portal — TEST REPORT

**Sprint:** Sprint 1  
**Date:** 2026-07-01  
**Tester:** Automated QA Suite (TestPortal.java) + Manual Code Review  
**Overall Result:** ✅ 14/14 PASS — Zero Failures

---

## How to Run the Tests

```bash
# Step 1: Navigate to the src folder
cd "Marathon Runner Portal/src"

# Step 2: Compile both files
javac MarathonPortal.java
javac TestPortal.java

# Step 3: Run tests
java TestPortal
```

Expected output: All 14 lines will show `PASS`.

---

## Test Summary

| Category | Tests | Passed | Failed |
| :--- | :---: | :---: | :---: |
| User Registration | 4 | 4 | 0 |
| Authentication & Lockout | 2 | 2 | 0 |
| Training Log Validation | 3 | 3 | 0 |
| Race Registration Validation | 3 | 3 | 0 |
| Forum Moderation | 2 | 2 | 0 |
| **TOTAL** | **14** | **14** | **0** |

---

## Detailed Test Cases

### 🔐 User Registration Tests

| Test ID | Feature | Input | Expected Output | Actual Output | Status |
| :--- | :--- | :--- | :--- | :--- | :---: |
| TC-001 | Valid Runner Signup | username=david_test, email=david@gmail.com, pwd=David@Pass12345, age=25, phone=9876543210 | Account created, ID=USR-1002, Points=500, Status=PENDING | PASS (ID: USR-1002, Pts: 500, Status: PENDING) | ✅ PASS |
| TC-002 | Duplicate Email Block | Same email as TC-001 | Exception: "This email is already registered." | Blocked: This email is already registered. | ✅ PASS |
| TC-003 | Weak Password | pwd=pwd123 (6 chars, no uppercase, no special) | Exception: "Must be at least 12 characters long." | Blocked: Weak password: Must be at least 12 characters long. | ✅ PASS |
| TC-004 | Age Out of Range | age=16 | Exception: "Age must be between 18 and 40." | Blocked: Age must be between 18 and 40. | ✅ PASS |

---

### 🔑 Authentication & Lockout Tests

| Test ID | Feature | Input | Expected Output | Actual Output | Status |
| :--- | :--- | :--- | :--- | :--- | :---: |
| TC-005 | Block Unverified Login | email=david@gmail.com (PENDING status) | Exception: "Your account is pending verification." | Blocked: Your account is pending verification. Contact an admin to approve. | ✅ PASS |
| TC-006 | Lockout After 5 Fails | john@gmail.com with wrong password 5 times | Account locked, message "ACCOUNT LOCKED!", status=LOCKED | PASS (Blocked: ACCOUNT LOCKED! Wrong password entered 5 times. Locked for 30 minutes., Status: LOCKED) | ✅ PASS |

---

### 🏃 Training Log Validation Tests

| Test ID | Feature | Input | Expected Output | Actual Output | Status |
| :--- | :--- | :--- | :--- | :--- | :---: |
| TC-007 | Future Date Block | date = tomorrow | Exception: "Cannot log training for a future date." | Blocked: Cannot log training for a future date. | ✅ PASS |
| TC-008 | Unrealistic Pace (<3 min/km) | dist=5.0 km, duration=10 min (pace=2.0 min/km) | Exception: "Unrealistic pace (2.0 min/km)." | Blocked: Unrealistic pace (2.0 min/km). Must be between 3.0 and 10.0 min/km. | ✅ PASS |
| TC-009 | Outlier Distance Auto-Flag (≥10x avg) | First log: 5 km average, then log 55 km | flagged=true on the 55 km entry | PASS (Outlier flagged) | ✅ PASS |

---

### 🏁 Race Registration Tests

| Test ID | Feature | Input | Expected Output | Actual Output | Status |
| :--- | :--- | :--- | :--- | :--- | :---: |
| TC-010 | Eligibility Check (< 5 logs) | New runner with 0 training logs tries to register | Exception: "Eligibility check failed! ...logs in last 30 days (logged: 0)." | Blocked: Eligibility check failed! You need ≥5 training logs in last 30 days (logged: 0). | ✅ PASS |
| TC-011 | Double Booking Block | john registers for same race twice | Exception: "Double booking! You have another race on..." | Blocked: Double booking! You have another race on 31-07-2026 | ✅ PASS |
| TC-012 | Late Registration Penalty | Race within 7 days, runner has ≥5 logs | latePenalty=true, 50 points deducted | PASS (50 pts deducted) | ✅ PASS |

---

### 💬 Forum Moderation Tests

| Test ID | Feature | Input | Expected Output | Actual Output | Status |
| :--- | :--- | :--- | :--- | :--- | :---: |
| TC-013 | Spam Word Detection | Title="Free scam cash", Content containing "illegal" | Post status=FLAGGED | PASS (Post flagged) | ✅ PASS |
| TC-014 | URL Link Routing | Content with "http://running.com" | containsLink=true, status=PENDING | PASS (URL flagged for review) | ✅ PASS |

---

## Manual Feature Test Coverage

### ✅ Positive Tests (Features Working as Expected)

| Feature | Test Scenario | Result |
| :--- | :--- | :---: |
| Admin login | admin@gmail.com + Admin@Pass123 → Admin menu | ✅ PASS |
| Runner login | john@gmail.com + Runner@Pass123 → Runner menu | ✅ PASS |
| View available races | Shows 4 seed races, filters by location/distance | ✅ PASS |
| Add training log | Valid date, distance 10 km, duration 60 min | ✅ PASS |
| View training dashboard | Shows stats + sorted workout table | ✅ PASS |
| Edit training log | Updates distance/duration, sets modifiedAt | ✅ PASS |
| View approved posts | Shows 1 seed post by john_runner | ✅ PASS |
| Admin: list users | Shows admin + john_runner in table format | ✅ PASS |
| Admin: add race | Valid future date, unique name → race created | ✅ PASS |
| Admin: edit race | Updates location/description, sets lastModified | ✅ PASS |
| Admin: approve forum post | Sets status to APPROVED | ✅ PASS |
| Admin: analytics | Shows runner/race/forum counts | ✅ PASS |
| Admin: export users CSV | File created in exports/ folder | ✅ PASS |
| Admin: security log | Reads and displays logs/security.log | ✅ PASS |
| Mark race completed | Sets status=COMPLETED + records time | ✅ PASS |

### 🔴 Negative / Boundary Tests

| Feature | Test Scenario | Expected | Result |
| :--- | :--- | :--- | :---: |
| Registration | Age = 17 (below minimum) | Rejected | ✅ PASS |
| Registration | Age = 41 (above maximum) | Rejected | ✅ PASS |
| Registration | Phone = "12345" (5 digits) | Rejected | ✅ PASS |
| Registration | Email = "test@hotmail.com" | Rejected (not gmail/yahoo) | ✅ PASS |
| Training | Distance = 0.5 km (below 1 km minimum) | Rejected | ✅ PASS |
| Training | Distance = 101 km (above 100 km maximum) | Rejected | ✅ PASS |
| Race | Date = tomorrow (< 7 days from today) | Rejected | ✅ PASS |
| Race | Duplicate name same year | Rejected | ✅ PASS |
| Race edit | Race starting in 5 hours | Rejected (< 24h) | ✅ PASS |
| Race deactivate | Race with 1+ registrations | Rejected | ✅ PASS |
| Forum | Title = "Hi" (< 5 chars) | Rejected | ✅ PASS |
| Forum | Content = "Ok" (< 10 chars) | Rejected | ✅ PASS |
| Forum | 3 flagged posts → next post attempt | Banned | ✅ PASS |

---

## Final Compilation Check

```
Command: javac MarathonPortal.java
Result:  0 errors, 0 warnings
Status:  ✅ CLEAN COMPILATION
```

---

## Automated Test Console Output

```
==================================================
   MARATHON PORTAL - AUTOMATED QA TEST RESULTS
==================================================
[VERIFY] Seed Data loaded: OK.
[TEST 01] Valid Runner Signup: PASS (ID: USR-1002, Pts: 500, Status: PENDING)
[TEST 02] Block Duplicate Email: PASS (Blocked: This email is already registered.)
[TEST 03] Password Strength Rules: PASS (Blocked: Weak password: Must be at least 12 characters long.)
[TEST 04] Verify Age Limit Range: PASS (Blocked: Age must be between 18 and 40.)
[TEST 05] Block Login for Unverified: PASS (Blocked: Your account is pending verification. Contact an admin to approve.)
[TEST 06] Login Lockout after 5 Wrong Passwords: PASS (Blocked: ACCOUNT LOCKED! Wrong password entered 5 times. Locked for 30 minutes., Status: LOCKED)
[TEST 07] Future Workout Date Block: PASS (Blocked: Cannot log training for a future date.)
[TEST 08] Pace Validation Rules (<3 min/km): PASS (Blocked: Unrealistic pace (2.0 min/km). Must be between 3.0 and 10.0 min/km.)
[TEST 09] Auto-Flag Distance Outliers (10x Avg): PASS (Outlier flagged)
[TEST 10] Race Eligibility Logs Check: PASS (Blocked: Eligibility check failed! You need ≥5 training logs in last 30 days (logged: 0).)
[TEST 11] Same-day Booking Check: PASS (Blocked: Double booking! You have another race on 31-07-2026)
[TEST 12] Late Registration point deduction: PASS (50 pts deducted)
[TEST 13] Forum Post Spam Detection: PASS (Post flagged)
[TEST 14] Forum Post URL Approval Routing: PASS (URL flagged for review)
==================================================
        TEST REPORT PROCESS COMPLETE
==================================================
```
