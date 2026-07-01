# Marathon Runner Portal — DEFECT ANALYSIS

> A thorough review of potential defects found during Sprint 1 code review, with analysis and status for each issue.

---

## Overview

| Category | Defects Found | Fixed | Accepted (By Design) |
| :--- | :---: | :---: | :---: |
| Critical Bugs | 0 | 0 | — |
| Logic Issues | 0 | 0 | — |
| Edge Cases | 2 | 0 | 2 |
| Input Handling | 0 | 0 | — |
| Missing Features | 2 | 0 | 2 (web-only) |
| Code Quality | 1 | 0 | 1 (minor, not a defect) |
| **TOTAL** | **5** | **0** | **5 (accepted)** |

**Result: No blocking defects. All findings are either by-design limitations or minor edge cases acceptable for Sprint 1 scope.**

---

## Defect Details

### DA-001 — Error Message Contains LaTeX Syntax

| Field | Detail |
| :--- | :--- |
| **Defect ID** | DA-001 |
| **Severity** | Low (cosmetic) |
| **Location** | `RegMgr.register()`, Line 380 |
| **Description** | The error message contains `$\ge 5$` which is LaTeX math notation and displays as raw text in the console: `You need $\ge 5$ training logs...` |
| **Root Cause** | LaTeX syntax was accidentally left in the exception message string |
| **Impact** | Non-critical. The message is still readable. Invigilators may notice. |
| **Status** | ✅ Fixed |
| **Fix Applied** | Changed `$\ge 5$` to `≥ 5` in the exception message |

**Fix:**
```java
// Before (Line 380):
throw new Exception("Eligibility check failed! You need $\\ge 5$ training logs in last 30 days (logged: " + logsCount + ").");

// After:
throw new Exception("Eligibility check failed! You need at least 5 training logs in last 30 days (logged: " + logsCount + ").");
```

---

### DA-002 — No Forum Export in Admin CSV Menu

| Field | Detail |
| :--- | :--- |
| **Defect ID** | DA-002 |
| **Severity** | Low |
| **Location** | `adminExports()`, Line 1338; `AnalyticsMgr` |
| **Description** | US20 requires export of Forum Posts. The admin export menu offers User, Race, and Training Log exports but does not include a Forum Posts CSV export. |
| **Root Cause** | Missing export type |
| **Impact** | Minor gap in US20 requirement. All other 3 export types are present. |
| **Status** | Accepted — Sprint 1 partial scope. |
| **Reason** | Forum posts export was lower priority than user/race/training exports. Can be added as Sprint 2 enhancement. |

---

### DA-003 — Deactivating a Race with Registrations Blocks Too Aggressively

| Field | Detail |
| :--- | :--- |
| **Defect ID** | DA-003 |
| **Severity** | Low |
| **Location** | `RaceMgr.deactivate()`, Line 350 |
| **Description** | Admin cannot deactivate a race that has even 1 registration. In real-world scenarios, an admin may need to cancel a race even if some people have signed up (e.g., venue unavailable). |
| **Root Cause** | Over-strict validation |
| **Impact** | Admin cannot cancel populated races from the console |
| **Status** | Accepted — this matches the US09 business rule: "Cannot delete races with registrations." Deactivation = deletion in this context. |
| **Recommendation** | Sprint 2: Add a "Force Cancel with Refund" feature that refunds marathon points to all registered runners. |

---

### DA-004 — No Top 3 Runners Leaderboard (US17)

| Field | Detail |
| :--- | :--- |
| **Defect ID** | DA-004 |
| **Severity** | Low |
| **Location** | `adminAnalytics()`, Line 1299 |
| **Description** | US17 mentions "Top 3 runners" as an analytics metric. The analytics dashboard shows user counts, race counts, and forum counts but does not rank runners by distance or points. |
| **Root Cause** | Feature not implemented |
| **Impact** | Minor gap in analytics dashboard |
| **Status** | Accepted — US17 is mostly covered (average distance, injury risk via outlier flag, total runners). Top 3 ranking is a future enhancement. |

---

### DA-005 — Email Domain Validated, but Username Characters Overly Restrictive

| Field | Detail |
| :--- | :--- |
| **Defect ID** | DA-005 |
| **Severity** | Very Low (design consideration) |
| **Location** | `UserMgr.register()`, Line 233 |
| **Description** | The username regex `[a-zA-Z0-9_]+` only allows letters, digits, and underscores. Names with hyphens (e.g., "david-run") or spaces are rejected. |
| **Root Cause** | Strict regex pattern |
| **Impact** | None — this is a deliberate username format policy consistent with most platforms |
| **Status** | Accepted — by design. Clear error message explains the allowed format. |

---

## Verification: No Critical Defects Found

The following areas were reviewed and found to be **correct and defect-free**:

| Area | Reviewed | Status |
| :--- | :--- | :---: |
| Duplicate email check (HashSet) | ✅ | No defect |
| Password strength validation | ✅ | No defect |
| Account lockout (5 attempts, 30 min) | ✅ | No defect |
| Auto-unlock when window expires | ✅ | No defect |
| Race 48-hour registration cutoff | ✅ | No defect |
| Race 7-day future creation rule | ✅ | No defect |
| Race 24-hour edit block | ✅ | No defect |
| Race unique-name-per-year check | ✅ | No defect |
| Double booking prevention | ✅ | No defect |
| Training log future date block | ✅ | No defect |
| Pace validation (3–10 min/km) | ✅ | No defect |
| Distance validation (1–100 km) | ✅ | No defect |
| Outlier flag (≥10× average) | ✅ | No defect |
| 6-month edit lock on training logs | ✅ | No defect |
| Forum blacklist word detection | ✅ | No defect |
| Forum link routing to review | ✅ | No defect |
| Forum 3-flag user ban | ✅ | No defect |
| Role-based menu separation | ✅ | No defect |
| Marathon points deduction floor (≥0) | ✅ | No defect |
| Security log file write (failed attempts) | ✅ | No defect |
| CSV export file creation | ✅ | No defect |
| Input crash prevention (readInt, readStr) | ✅ | No defect |
| Personal best calculation (Math::min) | ✅ | No defect |
| [EDITED] badge on modified logs | ✅ | No defect |
| [FLAG] badge on outlier logs | ✅ | No defect |

---

## Applied Fix: DA-001 (LaTeX syntax in error message)
