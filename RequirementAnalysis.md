# Marathon Runner Portal — Requirement Analysis
**Project**: Marathon Runner Portal  
**Technology**: Java 17 Console Application  
**Author**: Senior Java Software Engineer  
**Date**: 2026-06-29  
**Version**: 1.0.0  

---

## Overview

This document provides a complete analysis of all 20 Agile User Stories (US01–US20) for the Marathon Runner Portal. Each story is analyzed for:
- **Purpose** — What the story achieves
- **Business Rules** — Constraints and logic
- **Console Feasibility** — Can this be implemented in a Java Console App?
- **Implementation Decision** — Implemented / Ignored with explanation

---

## US01: User Registration

**Purpose**: Allow new users to register with email, password, and personal details to access the portal.

**Business Rules**:
1. Email must be unique (no duplicate accounts)
2. Password must be at least 12 characters with 1 special character
3. Users cannot log in until email is verified. Allowed domains: gmail and yahoo
4. Users must be aged between 18–40
5. Show "Account Created at: [timestamp]" in User Profile
6. Provide account status (Pending / Verified)
7. If runner, 500 marathon points credited to account

**Console Feasibility**: ✅ FULLY FEASIBLE  
All validations, collections storage, and timestamp logic work perfectly in console.

**Implementation Decision**: ✅ **IMPLEMENTED**  
- `DuplicateEmailException` for duplicate check  
- `WeakPasswordException` for password validation  
- `InvalidAgeException` for age check  
- Domain validation (gmail.com, yahoo.com)  
- `LocalDateTime` for timestamps  
- `AccountStatus` enum (PENDING, VERIFIED)  
- Email verification simulated (auto-verify after admin approval simulation)

---

## US02: Login & Role-based Dashboard

**Purpose**: Allow users to log in and view a role-specific dashboard (Admin/Runner).

**Business Rules**:
1. After 5 failed attempts, account locks for 30 minutes
2. Admins see analytics; runners see upcoming races and training stats
3. Session expires after 1 hour of inactivity

**Console Feasibility**: ⚠️ PARTIALLY FEASIBLE  
- Failed attempts → ✅ Implementable  
- Role-based menus → ✅ Implementable  
- Session expiry/1-hour inactivity → ❌ Requires background scheduler (not feasible without threads; simplified)

**Implementation Decision**: ✅ **IMPLEMENTED** (partial)  
- Login with failed-attempt counter → account lock logic implemented  
- Role-based console menus for Admin and Runner  
- Session expiry simulated via login timestamp check (manual activity tracking)  
- True 1-hour background timer: mentioned in README as "Not Implemented"

---

## US03: Race Listing and Filtering

**Purpose**: Allow runners to see available races filtered by date, location, and distance.

**Business Rules**:
1. Only show races where registration is open
2. Hide races that occur in <48 hours (registration closed)
3. Runners cannot see races they've already registered for

**Console Feasibility**: ✅ FULLY FEASIBLE

**Implementation Decision**: ✅ **IMPLEMENTED**  
- Filter by date, location, distance using Java Streams/loops  
- 48-hour cutoff check with `LocalDateTime`  
- Exclude already-registered races per runner

---

## US04: Race Registration

**Purpose**: Allow runners to register for a race only if they meet training requirements.

**Business Rules**:
1. Must have ≥5 training logs in the last 30 days
2. Cannot register for two races on the same day
3. Registration must be ≥48 hours before race start time
4. Show "Last Modified: [timestamp]" if admin changes race details

**Console Feasibility**: ✅ FULLY FEASIBLE

**Implementation Decision**: ✅ **IMPLEMENTED**  
- Training log count validation  
- Same-day race conflict check  
- 48-hour registration window enforcement  
- `lastModified` field on Race with timestamp display

---

## US05: Training Log Creation

**Purpose**: Allow runners to log training sessions with distance, duration, and date.

**Business Rules**:
1. No future dates allowed
2. Max 100 km per entry (prevent typos)
3. Duplicate logs (same date & distance) are rejected
4. Logs older than 6 months cannot be edited
5. Show "Logged 3 days ago" recency indicator
6. "Edited" badge if `modified_at` differs from `created_at`

**Console Feasibility**: ✅ FULLY FEASIBLE

**Implementation Decision**: ✅ **IMPLEMENTED**  
- Date validation (no future dates)  
- Distance range check (1–100 km)  
- Duplicate detection  
- 6-month edit restriction  
- Recency display ("Logged X days ago")  
- Edited badge logic via timestamp comparison

---

## US06: Training Progress Dashboard

**Purpose**: Allow runners to view training progress in sortable/filterable format.

**Business Rules**:
1. Show average pace, distance trends, and comparisons
2. Allow export to CSV/PDF for personal records

**Console Feasibility**: ⚠️ PARTIALLY FEASIBLE  
- Average pace, distance trends → ✅ Fully feasible  
- PDF export → ❌ Requires external library  
- CSV export → ✅ Feasible with file writing

**Implementation Decision**: ✅ **IMPLEMENTED** (partial)  
- Console dashboard with computed averages, total distance, pace  
- CSV export implemented via Java `FileWriter`  
- PDF export: mentioned in README as "Not Implemented"

---

## US07: Forum Post Submission & Moderation

**Purpose**: Allow runners to post in forum; posts must be admin-approved.

**Business Rules**:
1. Posts with banned words are auto-flagged
2. Admins must approve before visibility (max 24-hour delay)
3. Users with 3+ flagged posts are temporarily banned

**Console Feasibility**: ✅ FULLY FEASIBLE  
- 24-hour delay enforcement → simplified (admin manually reviews)

**Implementation Decision**: ✅ **IMPLEMENTED**  
- Banned word filter list  
- Auto-flag and `PostStatus` enum (PENDING, APPROVED, FLAGGED, REJECTED)  
- Admin approval menu  
- Flagged post counter per user → temporary ban logic

---

## US08: User Profile and Past Races

**Purpose**: Allow runners to view profile with past race history.

**Business Rules**:
1. Only show completed races (not upcoming)
2. Display personal best times per race distance
3. Allow profile edits except email, username, and ID

**Console Feasibility**: ✅ FULLY FEASIBLE

**Implementation Decision**: ✅ **IMPLEMENTED**  
- Profile view with editable fields (name, age, phone)  
- Past completed races display  
- Personal best per distance category computed from registrations

---

## US09: Admin Race Management

**Purpose**: Allow admins to add/edit/delete races with validation.

**Business Rules**:
1. Cannot delete a race if registrations exist
2. Race dates must be in the future
3. Distance must be 5K, 10K, Half, or Full Marathon
4. Provide "Deactivate" instead of "Delete"
5. Provide race status (Active / Inactive)

**Console Feasibility**: ✅ FULLY FEASIBLE

**Implementation Decision**: ✅ **IMPLEMENTED**  
- CRUD operations in `RaceService`  
- `RaceDistance` enum (FIVE_K, TEN_K, HALF_MARATHON, FULL_MARATHON)  
- `RaceStatus` enum (ACTIVE, INACTIVE)  
- Deactivate instead of delete with registration guard

---

## US10: Admin Analytics Dashboard

**Purpose**: Allow admin to see stats on user activity, race participation, and forum posts.

**Business Rules**:
1. Data should be exportable as CSV/Excel
2. User (admin) must be authenticated before exporting the file

**Console Feasibility**: ⚠️ PARTIALLY FEASIBLE  
- Analytics display → ✅ Feasible  
- Excel export → ❌ Requires Apache POI library  
- CSV export → ✅ Feasible

**Implementation Decision**: ✅ **IMPLEMENTED** (partial)  
- Analytics: total runners, races, registrations, forum stats  
- CSV export with authentication check  
- Excel export mentioned in README as "Not Implemented"

---

## US11: User Authentication

**Purpose**: Secure login with failed login logging for security audits.

**Business Rules**:
1. Log all failed login activities for security audits
2. Provide error message with HTTP status code to frontend if user data not found

**Console Feasibility**: ⚠️ PARTIALLY FEASIBLE  
- Failed login logging → ✅ Feasible (log to file)  
- HTTP status codes → ❌ Not applicable in console (no HTTP layer)

**Implementation Decision**: ✅ **IMPLEMENTED** (partial)  
- `SecurityLog` class with failed attempt logging to file  
- HTTP status codes: mentioned in README as "Not Implemented" (no frontend)

---

## US12: Race CRUD with Validation

**Purpose**: Enhanced race management with strict validation.

**Business Rules**:
1. Race dates must be ≥7 days in the future
2. Admins cannot modify races <24 hours before start time
3. Race names must be unique per year

**Console Feasibility**: ✅ FULLY FEASIBLE

**Implementation Decision**: ✅ **IMPLEMENTED**  
- 7-day future validation  
- 24-hour modification lock  
- Unique name per year check using HashSet logic

---

## US13: Race Registration Validation

**Purpose**: Additional validation rules for race registration.

**Business Rules**:
1. Reject if user has no training logs in last 30 days
2. Prevent registration if race is at full capacity
3. Charge 50 extra marathon points if registering within 7 days of race

**Console Feasibility**: ✅ FULLY FEASIBLE

**Implementation Decision**: ✅ **IMPLEMENTED**  
- Training log 30-day check  
- `RaceFullException` when capacity reached  
- Late registration penalty (50 marathon points deducted)

---

## US14: Training Log Validation & Duplicate Check

**Purpose**: Advanced training log validation.

**Business Rules**:
1. Logs must have distance ≥1 km and ≤100 km
2. Reject if pace is unrealistic (<3 min/km or >10 min/km)
3. Auto-flag logs that are 10× higher than user's average

**Console Feasibility**: ✅ FULLY FEASIBLE

**Implementation Decision**: ✅ **IMPLEMENTED**  
- Distance range: 1–100 km  
- Pace calculation and validation  
- Average comparison with auto-flag via `TrainingException`

---

## US15: Forum Moderation Workflow

**Purpose**: Enhanced forum moderation rules.

**Business Rules**:
1. Posts with links require manual approval
2. Auto-delete posts older than 1 year

**Console Feasibility**: ✅ FULLY FEASIBLE  
- Auto-delete on schedule → simplified to on-demand cleanup

**Implementation Decision**: ✅ **IMPLEMENTED**  
- URL/link detection regex  
- Manual approval flag for link-containing posts  
- Cleanup method for posts older than 365 days (triggered on admin action)

---

## US16: Email Service for Verification

**Purpose**: Send verification email after registration.

**Business Rules**:
1. Send OTP/link to registered email for verification
2. OTP expires in 10 minutes

**Console Feasibility**: ❌ NOT FEASIBLE  
Email sending requires SMTP/JavaMail, which is a Maven dependency. OTP delivery requires network infrastructure.

**Implementation Decision**: ❌ **NOT IMPLEMENTED**  
→ Simulated: Admin manually verifies users in the console. See README section "Not Implemented in Console Version."

---

## US17: Password Reset / Forgot Password

**Purpose**: Allow users to reset password via email.

**Business Rules**:
1. Send password reset link to email
2. Link expires in 15 minutes

**Console Feasibility**: ❌ NOT FEASIBLE  
Requires email service and token-based URL.

**Implementation Decision**: ❌ **NOT IMPLEMENTED**  
→ Admin can reset passwords manually in the console. See README.

---

## US18: JWT Authentication & Token Management

**Purpose**: Secure API authentication using JWT tokens.

**Business Rules**:
1. Generate JWT on login
2. Token expires in 1 hour
3. Refresh token mechanism

**Console Feasibility**: ❌ NOT FEASIBLE  
JWT is a web API concept requiring HTTP headers, token libraries (jjwt), and stateless REST endpoints.

**Implementation Decision**: ❌ **NOT IMPLEMENTED**  
→ Console uses simple in-memory session object. See README.

---

## US19: CSV/Excel Export with Download UI

**Purpose**: Allow users to download data as CSV/Excel through browser UI.

**Business Rules**:
1. Download button triggers file generation
2. File includes all relevant data with headers

**Console Feasibility**: ⚠️ PARTIALLY FEASIBLE  
- CSV generation to file → ✅ Feasible  
- Browser download UI → ❌ Not applicable in console

**Implementation Decision**: ✅ **PARTIALLY IMPLEMENTED**  
→ CSV files generated and saved locally. Download UI: mentioned in README.

---

## US20: Scheduler / Automated Tasks

**Purpose**: Automated background tasks (archive old posts, send reminders, unlock accounts).

**Business Rules**:
1. Unlock locked accounts after 30 minutes automatically
2. Archive posts older than 1 year on schedule
3. Send race reminder emails 24 hours before race

**Console Feasibility**: ❌ NOT FEASIBLE  
True schedulers (cron/Spring @Scheduled) require background thread management and are not appropriate for a simple console application.

**Implementation Decision**: ❌ **NOT IMPLEMENTED** (Web concept)  
→ Account unlock triggered manually on login attempt. Post cleanup on admin action. Email reminders skipped. See README.

---

## Summary Table

| User Story | Status | Notes |
|------------|--------|-------|
| US01: User Registration | ✅ Implemented | Full validation |
| US02: Login & Dashboard | ✅ Implemented | Session simplified |
| US03: Race Listing | ✅ Implemented | Filter by date/location/distance |
| US04: Race Registration | ✅ Implemented | Training check, 48hr rule |
| US05: Training Log | ✅ Implemented | All validations |
| US06: Training Dashboard | ✅ Implemented | CSV export added |
| US07: Forum Posts | ✅ Implemented | Banned words, moderation |
| US08: User Profile | ✅ Implemented | Past races, personal bests |
| US09: Admin Race Mgmt | ✅ Implemented | CRUD, deactivate |
| US10: Admin Analytics | ✅ Implemented | CSV export |
| US11: Authentication | ✅ Implemented | Login logs, lock |
| US12: Race CRUD | ✅ Implemented | Date/name validation |
| US13: Race Reg Validation | ✅ Implemented | Capacity, points penalty |
| US14: Training Validation | ✅ Implemented | Pace, auto-flag |
| US15: Forum Moderation | ✅ Implemented | Links, cleanup |
| US16: Email Service | ❌ Not Implemented | Requires SMTP/network |
| US17: Password Reset | ❌ Not Implemented | Requires email |
| US18: JWT Auth | ❌ Not Implemented | Web API concept |
| US19: CSV Download UI | ⚠️ Partial | CSV saved locally |
| US20: Scheduler | ❌ Not Implemented | Requires background threads |

**Implemented**: 15 (13 full + 2 partial)  
**Not Implemented**: 5 (web-only features)

---
*End of Requirement Analysis — Phase 1 Complete*
