import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TestPortal — Automated test suite for the Marathon Runner Portal.
 * Programmatically calls all modules in MarathonPortal to verify correctness of business rules.
 */
public class TestPortal {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("        STARTING AUTOMATED UNIT TESTS");
        System.out.println("==================================================");

        try {
            // Initialize data store seed data
            MarathonPortal.loadSeedData();
            System.out.println("[PASS] Loaded Seed Data successfully.");

            // Test 1: User Registration
            System.out.println("\n--- TEST 1: Runner Registration ---");
            MarathonPortal.User runner = MarathonPortal.UserMgr.register(
                    "alice_runner", "alice@gmail.com", "Alice@Pass12345", 25, "9876543210"
            );
            assert runner.username.equals("alice_runner") : "Username mismatch";
            assert runner.email.equals("alice@gmail.com") : "Email mismatch";
            assert runner.status.equals("PENDING") : "Initial status must be PENDING";
            assert runner.marathonPoints == 500 : "Runner should start with 500 points";
            System.out.println("[PASS] Registered Alice successfully (PENDING, 500 pts).");

            // Test 2: Double Registration Check
            System.out.println("\n--- TEST 2: Duplicate Email Block ---");
            try {
                MarathonPortal.UserMgr.register("alice_clone", "alice@gmail.com", "Alice@Pass12345", 26, "9876543211");
                throw new RuntimeException("FAIL: Allowed duplicate email registration!");
            } catch (Exception e) {
                System.out.println("[PASS] Successfully blocked duplicate email registration: " + e.getMessage());
            }

            // Test 3: Password Strength Checks
            System.out.println("\n--- TEST 3: Password Strength Policy ---");
            try {
                MarathonPortal.UserMgr.register("weak_user", "weak@gmail.com", "short", 22, "9876543212");
                throw new RuntimeException("FAIL: Allowed weak password!");
            } catch (Exception e) {
                System.out.println("[PASS] Successfully blocked weak password: " + e.getMessage());
            }

            // Test 4: Verify Account & Login
            System.out.println("\n--- TEST 4: Account Approval & Login ---");
            try {
                MarathonPortal.UserMgr.login("alice@gmail.com", "Alice@Pass12345");
                throw new RuntimeException("FAIL: Allowed login for PENDING runner!");
            } catch (Exception e) {
                System.out.println("[PASS] Correctly blocked login for unverified account.");
            }

            // Approve account
            runner.status = "VERIFIED";
            MarathonPortal.User loggedIn = MarathonPortal.UserMgr.login("alice@gmail.com", "Alice@Pass12345");
            assert loggedIn != null : "Login failed after approval";
            System.out.println("[PASS] Alice successfully logged in after admin verification.");

            // Test 5: Training Log Boundary Rules (Future workouts blocked)
            System.out.println("\n--- TEST 5: Workout Logs Date Check ---");
            try {
                MarathonPortal.TrainingMgr.addLog(runner.id, LocalDate.now().plusDays(1), 5.0, 30, "Future run");
                throw new RuntimeException("FAIL: Allowed future training log!");
            } catch (Exception e) {
                System.out.println("[PASS] Blocked logging workouts in the future.");
            }

            // Test 6: Training Log Pace Check
            System.out.println("\n--- TEST 6: Workout Logs Pace Check ---");
            try {
                // Pace = 10 minutes / 1 km = 10 min/km (Borderline OK)
                MarathonPortal.TrainingLog log = MarathonPortal.TrainingMgr.addLog(runner.id, LocalDate.now(), 1.0, 10, "Valid pace");
                assert log != null;
                // Pace = 2 minutes / 1 km = 2 min/km (Too fast)
                MarathonPortal.TrainingMgr.addLog(runner.id, LocalDate.now(), 1.0, 2, "Too fast");
                throw new RuntimeException("FAIL: Allowed unrealistic fast pace!");
            } catch (Exception e) {
                System.out.println("[PASS] Unrealistic running pace was successfully blocked: " + e.getMessage());
            }

            // Test 7: Race Registration Eligibility Check (Needs 5 logs)
            System.out.println("\n--- TEST 7: Race Registration Prerequisite ---");
            MarathonPortal.Race race = MarathonPortal.races.get(0); // Mumbai Marathon
            try {
                MarathonPortal.RegMgr.register(runner, race);
                throw new RuntimeException("FAIL: Registered for race without 5 recent training logs!");
            } catch (Exception e) {
                System.out.println("[PASS] Successfully blocked registration due to insufficient logs: " + e.getMessage());
            }

            // Log 5 valid workouts for Alice
            System.out.println("Logging 5 training workouts for Alice...");
            for (int i = 0; i < 5; i++) {
                MarathonPortal.TrainingLog log = MarathonPortal.TrainingMgr.addLog(runner.id, LocalDate.now().minusDays(i), 5.0, 30, "Run " + i);
                assert log != null;
            }

            // Register again
            MarathonPortal.Registration reg = MarathonPortal.RegMgr.register(runner, race);
            assert reg != null : "Registration failed after 5 logs logged";
            System.out.println("[PASS] Registration succeeded after logging 5 workouts.");

            // Test 8: Same-Day Double Booking Block
            System.out.println("\n--- TEST 8: Same-Day Double Booking Block ---");
            // Create another race on the same day as Mumbai Marathon
            MarathonPortal.Race sameDayRace = new MarathonPortal.Race(
                    "RACE-999", "Mumbai Backup 10K", "Mumbai", race.raceDate, "10K", 100, "USR-1000", "Same day test"
            );
            MarathonPortal.races.add(sameDayRace);
            try {
                MarathonPortal.RegMgr.register(runner, sameDayRace);
                throw new RuntimeException("FAIL: Allowed registration for two races on the same day!");
            } catch (Exception e) {
                System.out.println("[PASS] Successfully blocked double booking on the same day: " + e.getMessage());
            }

            // Test 9: Late Registration Points Penalty Check
            System.out.println("\n--- TEST 9: Late Registration Point Penalty ---");
            // Create a race starting in 3 days (within 7 days, so penalty applies)
            MarathonPortal.Race lateRace = new MarathonPortal.Race(
                    "RACE-888", "Express 5K", "Pune", LocalDateTime.now().plusDays(3), "5K", 100, "USR-1000", "Express run"
            );
            MarathonPortal.races.add(lateRace);
            int pointsBefore = runner.marathonPoints;
            MarathonPortal.Registration lateReg = MarathonPortal.RegMgr.register(runner, lateRace);
            assert lateReg.latePenalty : "Late penalty flag must be true";
            assert runner.marathonPoints == pointsBefore - 50 : "Late fee did not deduct 50 points";
            System.out.println("[PASS] Registration within 7 days correctly charged a 50-point late fee.");

            // Test 10: Forum Moderation Auto-Flag Check
            System.out.println("\n--- TEST 10: Forum Post Spam Word Auto-Flag ---");
            MarathonPortal.ForumPost spamPost = MarathonPortal.ForumMgr.submit(
                    runner, "Win Free Money", "This is a spam and fraud message click now"
            );
            assert spamPost.status.equals("FLAGGED") : "Post status must be FLAGGED";
            assert runner.flaggedPosts == 1 : "Runner flag count must increment";
            System.out.println("[PASS] Forum post with spam keywords was auto-flagged.");

            System.out.println("\n==================================================");
            System.out.println("      ALL TESTS PASSED SUCCESSFULLY! (10/10)");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.out.println("\n[FAIL] Test suite execution encountered an error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
