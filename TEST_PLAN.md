# Marathon Runner Portal — Test Plan
**Project**: Marathon Runner Portal  
**Technology**: Java 17 Console Application  
**Version**: 1.0.0  
**Date**: 2026-06-29  
**Test Strategy**: Manual Console + Code Review  

---

## 1. Testing Scope

All implemented modules are tested:
- Authentication (Login, Register, Lockout)
- User Management (Admin functions)
- Race Management (CRUD)
- Race Registration (with validation)
- Training Logs (creation, edit, validation)
- Forum (post, moderate, flag)
- Analytics (stats, export)
- Profile (view, edit)
- Export (CSV generation)

---

## 2. Test Case Format

```
TC-XXX | Module | Type | Input | Expected | Status
```

---

## MODULE 1: User Registration

### TC-001 | Positive | Valid Registration
- **Input**: username=john_doe, email=john@gmail.com, password=SecurePass@123, age=25
- **Expected**: User registered successfully, 500 points credited, status=PENDING
- **Result**: [ ] PASS / [ ] FAIL

### TC-002 | Positive | Valid Yahoo Email Registration
- **Input**: email=jane@yahoo.com, all other valid fields
- **Expected**: Registration succeeds
- **Result**: [ ] PASS / [ ] FAIL

### TC-003 | Negative | Duplicate Email
- **Input**: Register with john@gmail.com (already exists)
- **Expected**: DuplicateEmailException thrown, "Email already registered" message
- **Result**: [ ] PASS / [ ] FAIL

### TC-004 | Negative | Weak Password (no special char)
- **Input**: password=SimplePassword1
- **Expected**: WeakPasswordException, "Password must have 1 special character"
- **Result**: [ ] PASS / [ ] FAIL

### TC-005 | Negative | Short Password (<12 chars)
- **Input**: password=Short@1
- **Expected**: WeakPasswordException, "Password must be at least 12 characters"
- **Result**: [ ] PASS / [ ] FAIL

### TC-006 | Negative | Age Below Minimum
- **Input**: age=17
- **Expected**: InvalidAgeException, "Age must be between 18 and 40"
- **Result**: [ ] PASS / [ ] FAIL

### TC-007 | Negative | Age Above Maximum
- **Input**: age=41
- **Expected**: InvalidAgeException, "Age must be between 18 and 40"
- **Result**: [ ] PASS / [ ] FAIL

### TC-008 | Boundary | Age Exactly 18
- **Input**: age=18
- **Expected**: Registration succeeds
- **Result**: [ ] PASS / [ ] FAIL

### TC-009 | Boundary | Age Exactly 40
- **Input**: age=40
- **Expected**: Registration succeeds
- **Result**: [ ] PASS / [ ] FAIL

### TC-010 | Negative | Invalid Email Domain
- **Input**: email=user@hotmail.com
- **Expected**: ValidationException, "Only gmail.com and yahoo.com domains allowed"
- **Result**: [ ] PASS / [ ] FAIL

### TC-011 | Negative | Empty Email
- **Input**: email=""
- **Expected**: ValidationException, "Email cannot be empty"
- **Result**: [ ] PASS / [ ] FAIL

### TC-012 | Validation | Timestamp Created At
- **Input**: Valid registration
- **Expected**: Profile shows "Account Created at: [current timestamp]"
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 2: Login & Authentication

### TC-013 | Positive | Valid Login (Runner)
- **Input**: email=john@gmail.com, password=SecurePass@123, status=VERIFIED
- **Expected**: Login success, Runner menu displayed
- **Result**: [ ] PASS / [ ] FAIL

### TC-014 | Positive | Valid Login (Admin)
- **Input**: admin email and password
- **Expected**: Login success, Admin menu displayed
- **Result**: [ ] PASS / [ ] FAIL

### TC-015 | Negative | Wrong Password
- **Input**: Correct email, wrong password
- **Expected**: "Invalid credentials", failed attempt counter incremented
- **Result**: [ ] PASS / [ ] FAIL

### TC-016 | Negative | Non-existent Email
- **Input**: email=nobody@gmail.com
- **Expected**: "User not found" error
- **Result**: [ ] PASS / [ ] FAIL

### TC-017 | Boundary | 5th Failed Attempt (Account Locks)
- **Input**: 5 consecutive wrong passwords
- **Expected**: Account locked for 30 minutes, "AccountLockedException" message
- **Result**: [ ] PASS / [ ] FAIL

### TC-018 | Negative | Login with Locked Account
- **Input**: Attempt login on locked account within 30 min window
- **Expected**: "Account locked until [time]" message
- **Result**: [ ] PASS / [ ] FAIL

### TC-019 | Negative | Login with PENDING Status
- **Input**: email=unverified@gmail.com (status=PENDING)
- **Expected**: "Account not verified. Please wait for admin verification."
- **Result**: [ ] PASS / [ ] FAIL

### TC-020 | Positive | Account Auto-Unlock After 30 Minutes
- **Input**: Login attempt after 30-minute window expires
- **Expected**: Login proceeds normally (failed counter resets)
- **Result**: [ ] PASS / [ ] FAIL

### TC-021 | Positive | Security Log Written on Failed Login
- **Input**: Wrong password attempt
- **Expected**: Entry appended to logs/security.log with timestamp
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 3: Race Listing and Filtering

### TC-022 | Positive | View All Available Races
- **Input**: Runner views races
- **Expected**: Only ACTIVE races with date >48 hours from now shown
- **Result**: [ ] PASS / [ ] FAIL

### TC-023 | Filter | By Location
- **Input**: Filter location="Mumbai"
- **Expected**: Only Mumbai races shown
- **Result**: [ ] PASS / [ ] FAIL

### TC-024 | Filter | By Distance
- **Input**: Filter distance=FULL_MARATHON
- **Expected**: Only full marathon races shown
- **Result**: [ ] PASS / [ ] FAIL

### TC-025 | Filter | By Date Range
- **Input**: Specific date range
- **Expected**: Races within range shown
- **Result**: [ ] PASS / [ ] FAIL

### TC-026 | Negative | Races Within 48 Hours Hidden
- **Input**: Race date is 24 hours from now
- **Expected**: Race NOT shown in listing
- **Result**: [ ] PASS / [ ] FAIL

### TC-027 | Negative | Already Registered Races Hidden
- **Input**: Runner already registered for Race X
- **Expected**: Race X not shown in available list
- **Result**: [ ] PASS / [ ] FAIL

### TC-028 | Boundary | Race Exactly 48 Hours Away
- **Input**: Race at exactly now+48h
- **Expected**: Race shown (edge of cutoff)
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 4: Race Registration

### TC-029 | Positive | Valid Race Registration
- **Input**: Runner with ≥5 training logs in 30 days, race ≥48h away
- **Expected**: Registration successful, confirmation shown
- **Result**: [ ] PASS / [ ] FAIL

### TC-030 | Negative | Insufficient Training Logs
- **Input**: Runner with 4 training logs in 30 days
- **Expected**: TrainingException, "Minimum 5 training logs required in last 30 days"
- **Result**: [ ] PASS / [ ] FAIL

### TC-031 | Negative | Race Full
- **Input**: Race at maximum capacity
- **Expected**: RaceFullException, "Race is fully booked"
- **Result**: [ ] PASS / [ ] FAIL

### TC-032 | Negative | Same-Day Race Conflict
- **Input**: Runner tries to register for 2 races on same day
- **Expected**: DuplicateRegistrationException, "Already registered for a race on this day"
- **Result**: [ ] PASS / [ ] FAIL

### TC-033 | Negative | Late Registration (<48h)
- **Input**: Race in 36 hours
- **Expected**: "Registration closed — race starts in less than 48 hours"
- **Result**: [ ] PASS / [ ] FAIL

### TC-034 | Boundary | Registration Exactly 48h Before Race
- **Input**: Registration at exact 48-hour mark
- **Expected**: Registration allowed
- **Result**: [ ] PASS / [ ] FAIL

### TC-035 | Negative | Late Registration Penalty (within 7 days)
- **Input**: Race in 5 days
- **Expected**: 50 marathon points deducted as penalty, warning shown
- **Result**: [ ] PASS / [ ] FAIL

### TC-036 | Positive | Duplicate Registration Prevention
- **Input**: Runner tries to register for same race twice
- **Expected**: "Already registered for this race"
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 5: Training Log Creation

### TC-037 | Positive | Valid Training Log
- **Input**: date=today, distance=15.5, duration=90
- **Expected**: Log created, pace computed, recency shown
- **Result**: [ ] PASS / [ ] FAIL

### TC-038 | Negative | Future Date
- **Input**: date=tomorrow
- **Expected**: TrainingException, "Cannot log training for a future date"
- **Result**: [ ] PASS / [ ] FAIL

### TC-039 | Boundary | Distance Exactly 100 km
- **Input**: distance=100
- **Expected**: Log created (maximum allowed)
- **Result**: [ ] PASS / [ ] FAIL

### TC-040 | Negative | Distance Over 100 km
- **Input**: distance=101
- **Expected**: TrainingException, "Distance cannot exceed 100 km"
- **Result**: [ ] PASS / [ ] FAIL

### TC-041 | Boundary | Distance Exactly 1 km
- **Input**: distance=1
- **Expected**: Log created (minimum allowed)
- **Result**: [ ] PASS / [ ] FAIL

### TC-042 | Negative | Distance Below 1 km
- **Input**: distance=0.5
- **Expected**: TrainingException, "Distance must be at least 1 km"
- **Result**: [ ] PASS / [ ] FAIL

### TC-043 | Negative | Duplicate Log (same date + distance)
- **Input**: Two logs with same date and distance
- **Expected**: TrainingException, "Duplicate training log"
- **Result**: [ ] PASS / [ ] FAIL

### TC-044 | Negative | Edit Log Older Than 6 Months
- **Input**: Try to edit a log from 7 months ago
- **Expected**: TrainingException, "Cannot edit logs older than 6 months"
- **Result**: [ ] PASS / [ ] FAIL

### TC-045 | Validation | Recency Indicator
- **Input**: Log from 3 days ago
- **Expected**: "Logged 3 days ago" shown in display
- **Result**: [ ] PASS / [ ] FAIL

### TC-046 | Validation | Edited Badge
- **Input**: Edit a training log
- **Expected**: "[EDITED]" badge shown, modifiedAt ≠ createdAt
- **Result**: [ ] PASS / [ ] FAIL

### TC-047 | Validation | Unrealistic Pace (too fast)
- **Input**: distance=10, duration=20 → pace=2 min/km
- **Expected**: TrainingException, "Pace too fast (< 3 min/km)"
- **Result**: [ ] PASS / [ ] FAIL

### TC-048 | Validation | Unrealistic Pace (too slow)
- **Input**: distance=5, duration=60 → pace=12 min/km
- **Expected**: TrainingException, "Pace too slow (> 10 min/km)"
- **Result**: [ ] PASS / [ ] FAIL

### TC-049 | Validation | Auto-flag 10× Average
- **Input**: User avg=10 km, logs distance=100 km
- **Expected**: Log flagged with warning
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 6: Training Progress Dashboard

### TC-050 | Positive | View Training Stats
- **Input**: Runner with 10 logs
- **Expected**: Average pace, total distance, best run shown correctly
- **Result**: [ ] PASS / [ ] FAIL

### TC-051 | Positive | Sort by Date
- **Input**: Sort training logs by date (ascending)
- **Expected**: Logs displayed in chronological order
- **Result**: [ ] PASS / [ ] FAIL

### TC-052 | Positive | Sort by Distance
- **Input**: Sort training logs by distance (descending)
- **Expected**: Highest distance shown first
- **Result**: [ ] PASS / [ ] FAIL

### TC-053 | Positive | CSV Export
- **Input**: Export training data
- **Expected**: CSV file created in exports/ directory with correct headers and data
- **Result**: [ ] PASS / [ ] FAIL

### TC-054 | Boundary | No Training Logs
- **Input**: Runner with zero logs
- **Expected**: "No training logs found" message, no division-by-zero error
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 7: Forum Posts

### TC-055 | Positive | Valid Forum Post
- **Input**: Title and content with no banned words or links
- **Expected**: Post created with status=PENDING
- **Result**: [ ] PASS / [ ] FAIL

### TC-056 | Negative | Banned Word Detected
- **Input**: Content contains "spam"
- **Expected**: Post auto-flagged, status=FLAGGED, runner notified
- **Result**: [ ] PASS / [ ] FAIL

### TC-057 | Negative | Post Contains Link
- **Input**: Content contains "http://"
- **Expected**: Post flagged for manual approval, requiresManualApproval=true
- **Result**: [ ] PASS / [ ] FAIL

### TC-058 | Role | Admin Approves Post
- **Input**: Admin approves PENDING post
- **Expected**: Post status changes to APPROVED, visible to other runners
- **Result**: [ ] PASS / [ ] FAIL

### TC-059 | Role | Admin Rejects Post
- **Input**: Admin rejects PENDING post
- **Expected**: Post status=REJECTED, runner's flagged count incremented
- **Result**: [ ] PASS / [ ] FAIL

### TC-060 | Negative | 3 Flagged Posts Ban
- **Input**: Runner accumulates 3 flagged posts
- **Expected**: Runner banned from forum, cannot post
- **Result**: [ ] PASS / [ ] FAIL

### TC-061 | Negative | Banned Runner Posts
- **Input**: Forum-banned runner tries to post
- **Expected**: UnauthorizedAccessException, "You are banned from the forum"
- **Result**: [ ] PASS / [ ] FAIL

### TC-062 | Positive | View Approved Posts
- **Input**: Runner views forum
- **Expected**: Only APPROVED posts visible to runners
- **Result**: [ ] PASS / [ ] FAIL

### TC-063 | Positive | Cleanup Old Posts
- **Input**: Admin triggers cleanup (posts >1 year old)
- **Expected**: Old posts removed from list
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 8: User Profile

### TC-064 | Positive | View Profile
- **Input**: Runner views own profile
- **Expected**: All fields shown including created date, points, status
- **Result**: [ ] PASS / [ ] FAIL

### TC-065 | Positive | Edit Profile (Allowed Fields)
- **Input**: Change phone number
- **Expected**: Phone updated successfully
- **Result**: [ ] PASS / [ ] FAIL

### TC-066 | Negative | Edit Email (Not Allowed)
- **Input**: Try to change email
- **Expected**: "Email cannot be changed" message
- **Result**: [ ] PASS / [ ] FAIL

### TC-067 | Negative | Edit Username (Not Allowed)
- **Input**: Try to change username
- **Expected**: "Username cannot be changed" message
- **Result**: [ ] PASS / [ ] FAIL

### TC-068 | Positive | View Past Completed Races
- **Input**: Runner with completed registrations
- **Expected**: Only completed races shown (not upcoming)
- **Result**: [ ] PASS / [ ] FAIL

### TC-069 | Positive | Personal Best Display
- **Input**: Runner with multiple races at same distance
- **Expected**: Best completion time shown per distance
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 9: Admin Race Management

### TC-070 | Positive | Add Race
- **Input**: Valid race details (date ≥7 days future, valid distance)
- **Expected**: Race added with ACTIVE status
- **Result**: [ ] PASS / [ ] FAIL

### TC-071 | Negative | Race Date in Past
- **Input**: date=yesterday
- **Expected**: ValidationException, "Race date must be in the future"
- **Result**: [ ] PASS / [ ] FAIL

### TC-072 | Negative | Race Date Within 7 Days
- **Input**: date=3 days from now
- **Expected**: ValidationException, "Race date must be at least 7 days in the future"
- **Result**: [ ] PASS / [ ] FAIL

### TC-073 | Negative | Duplicate Race Name Same Year
- **Input**: Race name "Mumbai Marathon" already exists in 2026
- **Expected**: ValidationException, "Race name must be unique per year"
- **Result**: [ ] PASS / [ ] FAIL

### TC-074 | Negative | Edit Race <24h Before Start
- **Input**: Try to edit race starting in 12 hours
- **Expected**: "Cannot modify race within 24 hours of start time"
- **Result**: [ ] PASS / [ ] FAIL

### TC-075 | Positive | Deactivate Race
- **Input**: Admin deactivates a race with no registrations
- **Expected**: Race status=INACTIVE
- **Result**: [ ] PASS / [ ] FAIL

### TC-076 | Negative | Deactivate Race With Registrations
- **Input**: Admin deactivates race with existing registrations
- **Expected**: "Cannot deactivate race with existing registrations" warning
- **Result**: [ ] PASS / [ ] FAIL

### TC-077 | Positive | Edit Race (Valid Edit)
- **Input**: Edit race location, capacity
- **Expected**: Updated with "Last Modified: [timestamp]"
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 10: Admin Analytics Dashboard

### TC-078 | Positive | View Total Users
- **Input**: Admin views analytics
- **Expected**: Correct total runner count shown
- **Result**: [ ] PASS / [ ] FAIL

### TC-079 | Positive | View Total Registrations
- **Input**: Admin views analytics
- **Expected**: Correct total registration count
- **Result**: [ ] PASS / [ ] FAIL

### TC-080 | Positive | View Forum Stats
- **Input**: Admin views analytics
- **Expected**: PENDING/APPROVED/FLAGGED post counts shown
- **Result**: [ ] PASS / [ ] FAIL

### TC-081 | Positive | Export Analytics CSV
- **Input**: Admin exports analytics
- **Expected**: CSV file created in exports/ with data
- **Result**: [ ] PASS / [ ] FAIL

### TC-082 | Role | Runner Cannot Access Analytics
- **Input**: Runner tries to access admin analytics
- **Expected**: UnauthorizedAccessException, "Admin access required"
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 11: Exception Handling

### TC-083 | Exception | InvalidAgeException
- **Input**: age=15
- **Expected**: InvalidAgeException with clear message
- **Result**: [ ] PASS / [ ] FAIL

### TC-084 | Exception | DuplicateEmailException
- **Input**: Existing email in registration
- **Expected**: DuplicateEmailException
- **Result**: [ ] PASS / [ ] FAIL

### TC-085 | Exception | WeakPasswordException
- **Input**: password="weak"
- **Expected**: WeakPasswordException with details
- **Result**: [ ] PASS / [ ] FAIL

### TC-086 | Exception | RaceFullException
- **Input**: Register for full race
- **Expected**: RaceFullException with capacity info
- **Result**: [ ] PASS / [ ] FAIL

### TC-087 | Exception | UnauthorizedAccessException
- **Input**: Runner accesses admin function
- **Expected**: UnauthorizedAccessException
- **Result**: [ ] PASS / [ ] FAIL

### TC-088 | Exception | AccountLockedException
- **Input**: Login on locked account
- **Expected**: AccountLockedException with unlock time
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 12: Integration Tests

### TC-089 | Integration | Register → Login → Register Race Flow
- **Steps**: Register user → Admin verifies → Login → View races → Register race
- **Expected**: Complete flow succeeds end to end
- **Result**: [ ] PASS / [ ] FAIL

### TC-090 | Integration | Training → Race Registration Validation
- **Steps**: Add 5 training logs → Try race registration → Should succeed
- **Expected**: Registration approved
- **Result**: [ ] PASS / [ ] FAIL

### TC-091 | Integration | Forum Post → Admin Approval → Runner Views
- **Steps**: Post created → Admin approves → Runner sees post in forum
- **Expected**: Post visible after approval
- **Result**: [ ] PASS / [ ] FAIL

### TC-092 | Integration | Race Full → Registration Rejected
- **Steps**: Fill race capacity → 1 more runner tries to register
- **Expected**: RaceFullException
- **Result**: [ ] PASS / [ ] FAIL

### TC-093 | Integration | Admin Analytics After Registrations
- **Steps**: Multiple runners register for races → View analytics
- **Expected**: Analytics reflects correct counts
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 13: System Tests

### TC-094 | System | Full Application Startup
- **Expected**: Main menu displayed correctly, no errors
- **Result**: [ ] PASS / [ ] FAIL

### TC-095 | System | Invalid Menu Input
- **Input**: Enter "abc" when number expected
- **Expected**: "Invalid input" message, menu re-displayed
- **Result**: [ ] PASS / [ ] FAIL

### TC-096 | System | Exit from All Menus
- **Input**: Select exit/logout from every menu level
- **Expected**: Graceful exit, no exceptions
- **Result**: [ ] PASS / [ ] FAIL

### TC-097 | System | Multiple Runners, Multiple Races
- **Expected**: Data isolated per runner, no cross-contamination
- **Result**: [ ] PASS / [ ] FAIL

### TC-098 | System | CSV Files Created Correctly
- **Expected**: exports/ directory contains valid CSV files after export
- **Result**: [ ] PASS / [ ] FAIL

---

## MODULE 14: Acceptance Tests

### TC-099 | Acceptance | US01 — User Registration Complete
- **Criteria**: Can register, 500 points given, timestamp shown
- **Result**: [ ] PASS / [ ] FAIL

### TC-100 | Acceptance | US02 — Role-based Login Working
- **Criteria**: Admin and Runner see different menus
- **Result**: [ ] PASS / [ ] FAIL

### TC-101 | Acceptance | US04 — Race Registration with Validation
- **Criteria**: Training check + 48h rule + capacity enforced
- **Result**: [ ] PASS / [ ] FAIL

### TC-102 | Acceptance | US07 — Forum Moderation
- **Criteria**: Posts pending by default, admin approves, banned words flagged
- **Result**: [ ] PASS / [ ] FAIL

### TC-103 | Acceptance | US09 — Admin Race Management
- **Criteria**: Add/edit/deactivate races, unique name check
- **Result**: [ ] PASS / [ ] FAIL

---

## Regression Tests

After any bug fix, re-run:
- TC-001 to TC-021 (Auth)
- TC-029 to TC-036 (Registration)
- TC-037 to TC-049 (Training)
- TC-089 to TC-093 (Integration)

---

## Test Execution Summary Template

| Module | Total TCs | Passed | Failed | Pending |
|--------|-----------|--------|--------|---------|
| Registration | 12 | - | - | 12 |
| Login/Auth | 9 | - | - | 9 |
| Race Listing | 7 | - | - | 7 |
| Race Registration | 8 | - | - | 8 |
| Training Logs | 13 | - | - | 13 |
| Training Dashboard | 5 | - | - | 5 |
| Forum | 9 | - | - | 9 |
| Profile | 6 | - | - | 6 |
| Admin Race Mgmt | 8 | - | - | 8 |
| Admin Analytics | 5 | - | - | 5 |
| Exceptions | 6 | - | - | 6 |
| Integration | 5 | - | - | 5 |
| System | 5 | - | - | 5 |
| Acceptance | 5 | - | - | 5 |
| **TOTAL** | **103** | - | - | **103** |

---
*End of Test Plan — Phase 3 Complete*
