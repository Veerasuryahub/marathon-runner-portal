# Marathon Runner Portal — QA Test Execution Report

This document reports the testing execution and validation results for all Agile User Stories and Business Rules implemented in the Marathon Runner Portal console application.

---

## 📊 Summary of Test Execution
- **Total Test Cases Run:** 14
- **Passed:** 14
- **Failed:** 0
- **Overall Status:** **🟢 PASS**

---

## ⚙️ How to Run the Automated QA Tests

You can run the test suite yourself from the terminal to verify the portal logic:

1. Open your terminal and navigate to the project `src` folder:
   ```bash
   cd "Marathon Runner Portal/src"
   ```
2. Compile the test suite class:
   ```bash
   javac TestPortal.java
   ```
3. Execute the tests:
   ```bash
   java TestPortal
   ```

---

## 🧪 Detailed Test Results

Below is the verification trace showing each test ID, mapped User Story (US), target rule, and final pass status:

| Test ID | Mapped User Story | Target Verification Rule | Status | Observed Behavior |
| :--- | :--- | :--- | :---: | :--- |
| **TEST 01** | **US01** (Registration) | Valid Runner Signup flow | **🟢 PASS** | Account created in PENDING status, credited 500 Marathon Points. |
| **TEST 02** | **US01** (Registration) | Block duplicate email registrations | **🟢 PASS** | Blocked registration with message: *"This email is already registered."* |
| **TEST 03** | **US01** (Registration) | Enforce 12+ character passwords | **🟢 PASS** | Blocked weak password. |
| **TEST 04** | **US01** (Registration) | Enforce age boundary (18–40) | **🟢 PASS** | Blocked registration of a 16-year-old. |
| **TEST 05** | **US01** (Registration) | Block unverified user login | **🟢 PASS** | Prevented PENDING runner from logging in before admin approval. |
| **TEST 06** | **US02** (Authentication) | Account lockout on 5 failed attempts | **🟢 PASS** | Account locked for 30 minutes. Status changed to `LOCKED`. |
| **TEST 07** | **US05** (Training Log) | Block logging future workout dates | **🟢 PASS** | Blocked run logged with a future date. |
| **TEST 08** | **US14** (Training Pace) | Block unrealistic paces (<3 min/km) | **🟢 PASS** | Rejected run with a pace of 2.0 min/km. |
| **TEST 09** | **US14** (Training Pace) | Auto-flag distance outliers (10x average) | **🟢 PASS** | Logged a 55 km run after a 5 km run average; run auto-flagged. |
| **TEST 10** | **US04** (Race Signup) | Block signup with <5 logs in 30 days | **🟢 PASS** | Runner registration rejected due to insufficient recent workouts. |
| **TEST 11** | **US04** (Race Signup) | Prevent same-day double bookings | **🟢 PASS** | Blocked runner from registering for a second race on the same date. |
| **TEST 12** | **US13** (Late Signup) | Apply late fee within 7 days | **🟢 PASS** | Runner credited with late fee flag; 50 points deducted from balance. |
| **TEST 13** | **US07** (Forum Board) | Flag posts containing spam words | **🟢 PASS** | Spam post automatically flagged and runner's flagged count incremented. |
| **TEST 14** | **US15** (Forum Review) | Queue posts with URL hyperlinks | **🟢 PASS** | Routed post containing *http://* link to admin review queue. |

---

## 📝 Test Execution Console Logs

Here is the exact stdout printout generated during the execution of the verification suite:

```text
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
[TEST 10] Race Eligibility Logs Check: PASS (Blocked: Eligibility check failed! You need $\ge 5$ training logs in last 30 days (logged: 0).)
[TEST 11] Same-day Booking Check: PASS (Blocked: Double booking! You have another race on 29-07-2026)
[TEST 12] Late Registration point deduction: PASS (50 pts deducted)
[TEST 13] Forum Post Spam Detection: PASS (Post flagged)
[TEST 14] Forum Post URL Approval Routing: PASS (URL flagged for review)
==================================================
        TEST REPORT PROCESS COMPLETE
==================================================
```
