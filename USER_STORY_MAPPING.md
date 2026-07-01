# Marathon Runner Portal — USER STORY MAPPING

> Maps every User Story (US01–US20) to the exact methods, variables, and validations used in the code.

---

## US01 — User Registration

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Method** | `UserMgr.register()` (Line 231) |
| **UI Method** | `handleRegister()` (Line 632) |
| **Entity** | `User` class (Line 30) |
| **Collection** | `users` (ArrayList), `userByEmail` (HashMap), `usedEmails` (HashSet) |
| **Validation** | Username ≥3 chars + alphanumeric; email must end `@gmail.com` or `@yahoo.com`; password ≥12 chars + uppercase + digit + special; age 18–40; phone exactly 10 digits |
| **Business Rules** | Duplicate email blocked using `usedEmails` HashSet; status set to `PENDING`; 500 marathon points credited for RUNNER role |

---

## US02 — Login & Role-Based Dashboard

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Methods** | `UserMgr.login()` (Line 256), `handleLogin()` (Line 665), `showRunnerMenu()` (Line 689), `showAdminMenu()` (Line 726) |
| **Validation** | Email lookup in `userByEmail` HashMap; PENDING check; lockout check via `isLocked()`; password match; failed attempts counter |
| **Business Rules** | Account locked after 5 wrong passwords; lock duration 30 minutes via `LocalDateTime.plusMinutes(30)`; admin sees different menu than runner |
| **Variables** | `currentUser` (session), `failedAttempts`, `lockedUntil`, `status` |

---

## US03 — Race Listing & Filtering

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Method** | `viewRaces()` (Line 818) |
| **Validation** | Hides INACTIVE races, hides races where `isRegistrationOpen()` returns false (< 48h to start), hides races runner has already signed up for |
| **Business Rules** | Supports filter by Location (city keyword) and Distance (5K, 10K, Half, Full); shows "Late Penalty" label for races within 7 days |
| **Collections** | Iterates `races` ArrayList; builds `signedUp` HashSet of race IDs runner has registered for |

---

## US04 — Race Registration

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Methods** | `RegMgr.register()` (Line 358), `registerRace()` (Line 866) |
| **Entity** | `Registration` (Line 173) |
| **Collection** | `registrations` (ArrayList) |
| **Validation** | Race must be ACTIVE; registration must be open (≥48h); not full; no double-booking on same date; ≥5 training logs in last 30 days |
| **Business Rules** | Late penalty (−50 points) if within 7 days of race; `race.registrations` counter incremented; `lastModified` timestamp shown if race was edited |

---

## US05 — Training Log

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Methods** | `TrainingMgr.addLog()` (Line 397), `addTraining()` (Line 913), `TrainingMgr.editLog()` (Line 430), `editTraining()` (Line 1004) |
| **Entity** | `TrainingLog` (Line 117) |
| **Collection** | `trainingLogs` (ArrayList) |
| **Validation** | No future dates; distance 1–100 km; pace 3–10 min/km; duplicate detection (same date + distance); edit blocked if log > 6 months old |
| **Business Rules** | `modifiedAt` set on edit → displays `[EDITED]` badge in dashboard |

---

## US06 — Training Progress Dashboard

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Method** | `viewTrainingDashboard()` (Line 939) |
| **Business Rules** | Shows: total workouts, total distance, average distance, best distance, average pace, fastest pace; sort by date/distance/pace; CSV export via `exportTraining()` |
| **Note** | PDF export not implemented (web-only feature) |

---

## US07 — Forum

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Methods** | `ForumMgr.submit()` (Line 459), `postForum()` (Line 1069), `viewForum()` (Line 1050), `adminForum()` (Line 1236) |
| **Entity** | `ForumPost` (Line 149) |
| **Collection** | `forumPosts` (ArrayList) |
| **Validation** | Title ≥5 chars; content ≥10 chars; not forum-banned |
| **Business Rules** | Banned words scanned from BLACKLIST array; 3 flags → `forumBanned = true`; posts with URLs get `containsLink = true` and stay `PENDING` for admin review |

---

## US08 — User Profile

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Method** | `viewProfile()` (Line 760) |
| **Business Rules** | Shows ID, username, email, age, phone, points, forum status, join date; completed races shown with distance and finish time; personal best computed per distance using `HashMap<String, Integer>` with `Math::min`; only age and phone are editable (email/username/ID are locked) |

---

## US09 — Admin Race Management

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Methods** | `adminRaces()` (Line 1159), `RaceMgr.create()` (Line 315), `RaceMgr.edit()` (Line 336), `RaceMgr.deactivate()` (Line 350) |
| **Validation** | Race date ≥7 days in future; name unique per year; distance in approved list only (5K/10K/Half/Full); capacity ≥10 |
| **Business Rules** | Edit blocked within 24h of start; deactivate blocked if registrations > 0; status set to INACTIVE (not deleted) |

---

## US10 — Admin Analytics Dashboard

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Methods** | `adminAnalytics()` (Line 1299), `adminExports()` (Line 1338) |
| **Business Rules** | Shows: total/verified/pending runners, admin count, total/active races, signup count, forum queue; signup count per race; CSV exports for users/races/training logs |
| **Note** | Excel export and PDF not implemented (web-only). Admin authentication required — the admin menu is only shown after verified admin login |

---

## US11 — User Authentication (Audit Log)

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Methods** | `UserMgr.logFailedAttempt()` (Line 306), `adminSecurityLog()` (Line 1363) |
| **Business Rules** | Every wrong password and unknown email is written to `logs/security.log` with timestamp, email, and reason; admin can view the log from the menu |
| **Note** | HTTP status codes not applicable (console app, not REST API) |

---

## US12 — Race CRUD Validation

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Methods** | `RaceMgr.create()` (Line 315), `RaceMgr.edit()` (Line 336) |
| **Validation** | Race date ≥7 days future; admin cannot edit within 24h of start; race name must be unique per year |

---

## US13 — Race Registration Validation

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Method** | `RegMgr.register()` (Line 358) |
| **Validation** | Minimum 5 training logs in last 30 days; full capacity check; late registration penalty (−50 points) if within 7 days |

---

## US14 — Training Log Validation

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Methods** | `TrainingMgr.addLog()` (Line 397), `TrainingMgr.editLog()` (Line 430) |
| **Validation** | Distance 1–100 km; pace must be 3.0–10.0 min/km (auto-calculated); outlier auto-flag if distance ≥10× running average |

---

## US15 — Forum Moderation

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Methods** | `ForumMgr.submit()` (Line 459), `ForumMgr.cleanup()` (Line 491), `adminForum()` (Line 1236) |
| **Business Rules** | Posts with links routed to review queue; `cleanup()` removes posts older than 365 days; admin can approve, reject, or trigger cleanup |

---

## US16 — Email Verification

| Attribute | Detail |
| :--- | :--- |
| **Status** | ⚠️ Partially Implemented (console-adapted) |
| **How Implemented** | SMTP/email is a web feature. In the console version, admin manually verifies accounts via `adminUsers()` → option 2 "Verify Pending Account" |
| **Reason** | Email sending requires SMTP server and external libraries — outside scope of a Core Java Console Application |

---

## US17 — Analytics

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Method** | `adminAnalytics()` (Line 1299), `viewTrainingDashboard()` (Line 939) |
| **Business Rules** | Admin analytics: total runners, pending count, active races, forum queue; runner analytics: avg distance, avg pace, best pace, total distance; injury risk detection via auto-flag when outlier distance logged |
| **Note** | "Top 3 runners" leaderboard not implemented (data exists but leaderboard ranking not added — future enhancement) |

---

## US18 — Registration Scheduler

| Attribute | Detail |
| :--- | :--- |
| **Status** | ⚠️ Partially Implemented (console-adapted) |
| **How Implemented** | Registration window automatically closes 48h before race (checked in `Race.isRegistrationOpen()`); refund for cancelled races not implemented as race cancellation flow doesn't exist |
| **Reason** | Background schedulers require threads or cron jobs — outside scope of console application |

---

## US19 — Role-Based Authorization

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Methods** | `handleLogin()` (Line 665), `showRunnerMenu()` (Line 689), `showAdminMenu()` (Line 726) |
| **Business Rules** | After login, role is checked: ADMIN → admin menu, RUNNER → runner menu; runners cannot access admin functions because the admin menu is only reachable through the admin code path |
| **Note** | Activity logging for admin actions (audit trail) not implemented — future enhancement |

---

## US20 — Data Export

| Attribute | Detail |
| :--- | :--- |
| **Status** | ✅ Implemented |
| **Related Methods** | `AnalyticsMgr.exportUsers()` (Line 517), `AnalyticsMgr.exportRaces()` (Line 530), `AnalyticsMgr.exportAllLogs()` (Line 543), `AnalyticsMgr.exportTraining()` (Line 500), `adminExports()` (Line 1338), `exportTraining()` (Line 1092) |
| **Business Rules** | Admin can export: users, races, all training logs. Runner can export own training logs. CSV files saved to `exports/` folder with timestamp. Only admin reaches `adminExports()` through role-based menu routing |
| **Note** | Forum post export not implemented as a separate CSV (future enhancement). Excel/PDF export not applicable (web-only) |

---

## Summary Table

| User Story | Status | Key Method(s) |
| :--- | :--- | :--- |
| US01 User Registration | ✅ Implemented | `UserMgr.register()`, `handleRegister()` |
| US02 Login & Dashboard | ✅ Implemented | `UserMgr.login()`, `showRunnerMenu()`, `showAdminMenu()` |
| US03 Race Listing | ✅ Implemented | `viewRaces()` |
| US04 Race Registration | ✅ Implemented | `RegMgr.register()`, `registerRace()` |
| US05 Training Log | ✅ Implemented | `TrainingMgr.addLog()`, `TrainingMgr.editLog()` |
| US06 Training Dashboard | ✅ Implemented | `viewTrainingDashboard()` |
| US07 Forum | ✅ Implemented | `ForumMgr.submit()`, `viewForum()`, `postForum()` |
| US08 User Profile | ✅ Implemented | `viewProfile()` |
| US09 Admin Race Management | ✅ Implemented | `adminRaces()`, `RaceMgr.*` |
| US10 Admin Analytics | ✅ Implemented | `adminAnalytics()`, `adminExports()` |
| US11 Auth Audit Log | ✅ Implemented | `logFailedAttempt()`, `adminSecurityLog()` |
| US12 Race CRUD Validation | ✅ Implemented | `RaceMgr.create()`, `RaceMgr.edit()` |
| US13 Registration Validation | ✅ Implemented | `RegMgr.register()` |
| US14 Training Validation | ✅ Implemented | `TrainingMgr.addLog()`, `TrainingMgr.editLog()` |
| US15 Forum Moderation | ✅ Implemented | `ForumMgr.cleanup()`, `adminForum()` |
| US16 Email Verification | ⚠️ Console-Adapted | Admin manual verify via `adminUsers()` |
| US17 Analytics | ✅ Implemented | `adminAnalytics()`, `viewTrainingDashboard()` |
| US18 Registration Scheduler | ⚠️ Console-Adapted | `Race.isRegistrationOpen()` auto-closes window |
| US19 Role-Based Authorization | ✅ Implemented | Role check in `handleLogin()` |
| US20 Data Export | ✅ Implemented | `AnalyticsMgr.*`, `adminExports()` |
