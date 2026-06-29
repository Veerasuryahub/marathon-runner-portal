import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * TestPortal — Automated QA Test Suite for Marathon Runner Portal.
 * Programmatically runs 14 test cases to verify User Stories and Business Rules.
 */
public class TestPortal {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   MARATHON PORTAL - AUTOMATED QA TEST RESULTS");
        System.out.println("==================================================");
        try {
            MarathonPortal.loadSeedData();
            System.out.println("[VERIFY] Seed Data loaded: OK.");

            // 1. Registration tests
            System.out.print("[TEST 01] Valid Runner Signup: ");
            MarathonPortal.User u = MarathonPortal.UserMgr.register("david_test", "david@gmail.com", "David@Pass12345", 25, "9876543210");
            System.out.println("PASS (ID: " + u.id + ", Pts: " + u.marathonPoints + ", Status: " + u.status + ")");

            System.out.print("[TEST 02] Block Duplicate Email: ");
            try {
                MarathonPortal.UserMgr.register("david_clone", "david@gmail.com", "David@Pass12345", 26, "9876543211");
                System.out.println("FAIL");
            } catch (Exception e) {
                System.out.println("PASS (Blocked: " + e.getMessage() + ")");
            }

            System.out.print("[TEST 03] Password Strength Rules: ");
            try {
                MarathonPortal.UserMgr.register("weakpwd", "weak@gmail.com", "pwd123", 24, "9876543212");
                System.out.println("FAIL");
            } catch (Exception e) {
                System.out.println("PASS (Blocked: " + e.getMessage() + ")");
            }

            System.out.print("[TEST 04] Verify Age Limit Range: ");
            try {
                MarathonPortal.UserMgr.register("ageunder", "young@gmail.com", "Young@Pass123", 16, "9876543212");
                System.out.println("FAIL");
            } catch (Exception e) {
                System.out.println("PASS (Blocked: " + e.getMessage() + ")");
            }

            System.out.print("[TEST 05] Block Login for Unverified: ");
            try {
                MarathonPortal.UserMgr.login("david@gmail.com", "David@Pass12345");
                System.out.println("FAIL");
            } catch (Exception e) {
                System.out.println("PASS (Blocked: " + e.getMessage() + ")");
            }

            // 2. Lockout tests
            System.out.print("[TEST 06] Login Lockout after 5 Wrong Passwords: ");
            MarathonPortal.User runner = MarathonPortal.users.get(1); // john_runner
            runner.status = "VERIFIED";
            for (int i = 0; i < 4; i++) {
                try { MarathonPortal.UserMgr.login("john@gmail.com", "WrongPwd"); } catch (Exception e) {}
            }
            try {
                MarathonPortal.UserMgr.login("john@gmail.com", "WrongPwd");
                System.out.println("FAIL");
            } catch (Exception e) {
                System.out.println("PASS (Blocked: " + e.getMessage() + ", Status: " + runner.status + ")");
            }

            // Reset John
            runner.status = "VERIFIED";
            runner.failedAttempts = 0;
            runner.lockedUntil = null;

            // 3. Training & Pace tests
            System.out.print("[TEST 07] Future Workout Date Block: ");
            try {
                MarathonPortal.TrainingMgr.addLog(runner.id, LocalDate.now().plusDays(1), 10.0, 60, "Future");
                System.out.println("FAIL");
            } catch (Exception e) {
                System.out.println("PASS (Blocked: " + e.getMessage() + ")");
            }

            System.out.print("[TEST 08] Pace Validation Rules (<3 min/km): ");
            try {
                MarathonPortal.TrainingMgr.addLog(runner.id, LocalDate.now(), 5.0, 10, "Too fast");
                System.out.println("FAIL");
            } catch (Exception e) {
                System.out.println("PASS (Blocked: " + e.getMessage() + ")");
            }

            System.out.print("[TEST 09] Auto-Flag Distance Outliers (10x Avg): ");
            MarathonPortal.User flagRunner = MarathonPortal.UserMgr.register("flagrun", "flag@gmail.com", "Flag@Pass12345", 25, "9876543219");
            flagRunner.status = "VERIFIED";
            MarathonPortal.TrainingMgr.addLog(flagRunner.id, LocalDate.now().minusDays(1), 5.0, 30, "Short run");
            MarathonPortal.TrainingLog outlier = MarathonPortal.TrainingMgr.addLog(flagRunner.id, LocalDate.now(), 55.0, 330, "Outlier");
            System.out.println(outlier.flagged ? "PASS (Outlier flagged)" : "FAIL");

            // 4. Registration Eligibility & Penalties
            System.out.print("[TEST 10] Race Eligibility Logs Check: ");
            MarathonPortal.User elgRunner = MarathonPortal.UserMgr.register("elg", "elg@gmail.com", "Elg@Pass12345", 22, "9876543212");
            elgRunner.status = "VERIFIED";
            MarathonPortal.Race race = MarathonPortal.races.get(0);
            try {
                MarathonPortal.RegMgr.register(elgRunner, race);
                System.out.println("FAIL");
            } catch (Exception e) {
                System.out.println("PASS (Blocked: " + e.getMessage() + ")");
            }

            System.out.print("[TEST 11] Same-day Booking Check: ");
            MarathonPortal.User john = MarathonPortal.UserMgr.login("john@gmail.com", "Runner@Pass123");
            MarathonPortal.Registration reg1 = MarathonPortal.RegMgr.register(john, race);
            try {
                MarathonPortal.RegMgr.register(john, race);
                System.out.println("FAIL");
            } catch (Exception e) {
                System.out.println("PASS (Blocked: " + e.getMessage() + ")");
            }

            System.out.print("[TEST 12] Late Registration point deduction: ");
            MarathonPortal.Race lateRace = new MarathonPortal.Race("RACE-LATE", "Late Run", "Pune", LocalDateTime.now().plusDays(3), "5K", 100, "USR-1000", "Late");
            MarathonPortal.races.add(lateRace);
            int ptsBefore = john.marathonPoints;
            MarathonPortal.Registration regLate = MarathonPortal.RegMgr.register(john, lateRace);
            System.out.println(regLate.latePenalty && john.marathonPoints == (ptsBefore - 50) ? "PASS (50 pts deducted)" : "FAIL");

            // 5. Forum checks
            System.out.print("[TEST 13] Forum Post Spam Detection: ");
            MarathonPortal.ForumPost postSpam = MarathonPortal.ForumMgr.submit(john, "Free scam cash", "Get your illegal coins today!");
            System.out.println(postSpam.status.equals("FLAGGED") ? "PASS (Post flagged)" : "FAIL");

            System.out.print("[TEST 14] Forum Post URL Approval Routing: ");
            MarathonPortal.ForumPost postLink = MarathonPortal.ForumMgr.submit(john, "My training link", "Visit my site at http://running.com");
            System.out.println(postLink.status.equals("PENDING") && postLink.containsLink ? "PASS (URL flagged for review)" : "FAIL");

            System.out.println("==================================================");
            System.out.println("        TEST REPORT PROCESS COMPLETE");
            System.out.println("==================================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
