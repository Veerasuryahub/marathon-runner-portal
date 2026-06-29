import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * ============================================================================
 *   MARATHON RUNNER PORTAL — Java Console Application (Single-File Version)
 * ============================================================================
 *
 *  HOW TO RUN:
 *  1. Copy the entire contents of this file.
 *  2. Save it as "MarathonPortal.java" on your computer.
 *  3. Compile:  javac MarathonPortal.java
 *  4. Run:      java MarathonPortal
 *
 *  DEFAULT LOGINS:
 *  - Admin:  email: admin@gmail.com  | password: Admin@Pass123
 *  - Runner: email: john@gmail.com   | password: Runner@Pass123
 * ============================================================================
 */
public class MarathonPortal {

    // ==========================================
    //  SECTION 1 — DATA ENTITIES
    // ==========================================

    public static class User {
        public String id;
        public String username;
        public String email;
        public String password;
        public String phone;
        public String role;
        public String status;
        public int age;
        public int failedAttempts;
        public int marathonPoints;
        public int flaggedPosts;
        public boolean forumBanned;
        public LocalDateTime createdAt;
        public LocalDateTime lockedUntil;

        public User(String id, String username, String email, String password, int age, String phone, String role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.password = password;
            this.age = age;
            this.phone = phone;
            this.role = role;
            this.status = role.equals("ADMIN") ? "VERIFIED" : "PENDING";
            this.createdAt = LocalDateTime.now();
            this.marathonPoints = role.equals("RUNNER") ? 500 : 0;
            this.failedAttempts = 0;
            this.flaggedPosts = 0;
            this.forumBanned = false;
            this.lockedUntil = null;
        }

        public boolean isLocked() {
            if (lockedUntil == null) return false;
            if (LocalDateTime.now().isAfter(lockedUntil)) {
                lockedUntil = null;
                failedAttempts = 0;
                if (status.equals("LOCKED")) status = "VERIFIED";
                return false;
            }
            return true;
        }
    }

    public static class Race {
        public String id;
        public String name;
        public String location;
        public String distance;
        public String status;
        public String createdBy;
        public String description;
        public LocalDateTime raceDate;
        public LocalDateTime lastModified;
        public int maxCapacity;
        public int registrations;

        public Race(String id, String name, String location, LocalDateTime raceDate, String distance, int maxCapacity, String createdBy, String description) {
            this.id = id;
            this.name = name;
            this.location = location;
            this.raceDate = raceDate;
            this.distance = distance;
            this.maxCapacity = maxCapacity;
            this.createdBy = createdBy;
            this.description = description;
            this.status = "ACTIVE";
            this.registrations = 0;
            this.lastModified = null;
        }

        public boolean isRegistrationOpen() {
            long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), raceDate);
            return hours >= 48;
        }

        public boolean isFull() {
            return registrations >= maxCapacity;
        }

        public boolean isWithin7Days() {
            long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), raceDate);
            return hours <= 168 && hours >= 48;
        }
    }

    public static class TrainingLog {
        public String id;
        public String runnerId;
        public LocalDate date;
        public double distanceKm;
        public int durationMinutes;
        public double paceMinPerKm;
        public String notes;
        public boolean flagged;
        public LocalDateTime createdAt;
        public LocalDateTime modifiedAt;

        public TrainingLog(String id, String runnerId, LocalDate date, double distanceKm, int durationMinutes, String notes) {
            this.id = id;
            this.runnerId = runnerId;
            this.date = date;
            this.distanceKm = distanceKm;
            this.durationMinutes = durationMinutes;
            this.notes = notes;
            this.createdAt = LocalDateTime.now();
            this.modifiedAt = null;
            this.flagged = false;
            this.paceMinPerKm = (distanceKm > 0) ? (double) durationMinutes / distanceKm : 0;
        }

        public String getPace() {
            int minutes = (int) paceMinPerKm;
            int seconds = (int) Math.round((paceMinPerKm - minutes) * 60);
            return minutes + ":" + String.format("%02d", seconds) + " min/km";
        }
    }

    public static class ForumPost {
        public String id;
        public String authorId;
        public String authorName;
        public String title;
        public String content;
        public String status;
        public String flagReason;
        public boolean containsLink;
        public LocalDateTime createdAt;

        public ForumPost(String id, String authorId, String authorName, String title, String content, boolean containsLink, boolean flagged, String flagReason) {
            this.id = id;
            this.authorId = authorId;
            this.authorName = authorName;
            this.title = title;
            this.content = content;
            this.containsLink = containsLink;
            this.flagReason = flagReason;
            this.createdAt = LocalDateTime.now();
            this.status = flagged ? "FLAGGED" : "PENDING";
        }
    }

    public static class Registration {
        public String id;
        public String runnerId;
        public String raceId;
        public String status;
        public boolean latePenalty;
        public int completionMinutes;
        public LocalDateTime registeredAt;

        public Registration(String id, String runnerId, String raceId, boolean latePenalty) {
            this.id = id;
            this.runnerId = runnerId;
            this.raceId = raceId;
            this.latePenalty = latePenalty;
            this.status = "REGISTERED";
            this.completionMinutes = 0;
            this.registeredAt = LocalDateTime.now();
        }
    }

    // ==========================================
    //  SECTION 2 — DATABASE (In-Memory Arrays)
    // ==========================================

    public static List<User>         users         = new ArrayList<>();
    public static List<Race>         races         = new ArrayList<>();
    public static List<TrainingLog>  trainingLogs  = new ArrayList<>();
    public static List<ForumPost>    forumPosts    = new ArrayList<>();
    public static List<Registration> registrations = new ArrayList<>();

    public static Map<String, User> userByEmail = new HashMap<>();
    public static Set<String> usedEmails = new HashSet<>();

    public static int userIdCount = 1002;
    public static int raceIdCount = 105;
    public static int logIdCount  = 5010;
    public static int postIdCount = 3002;
    public static int regIdCount  = 8001;

    // Shared scanner for keyboard inputs
    private static final Scanner sc = new Scanner(System.in);

    // Global date/time formatting rules
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter DT_FMT   = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    // Word filter lists for flagging spam posts
    private static final String[] BLACKLIST = {"spam", "scam", "fake", "fraud", "hack", "abuse", "illegal"};

    // Current session tracker
    private static User currentUser = null;

    // ==========================================
    //  SECTION 3 — BUSINESS LOGIC MODULES
    // ==========================================

    // --- User & Login Management ---
    public static class UserMgr {
        public static User register(String username, String email, String password, int age, String phone) throws Exception {
            if (username.length() < 3) throw new Exception("Username must be at least 3 characters.");
            if (!username.matches("[a-zA-Z0-9_]+")) throw new Exception("Username can only contain letters, digits, and underscores.");

            String lowerEmail = email.toLowerCase().trim();
            if (!lowerEmail.endsWith("@gmail.com") && !lowerEmail.endsWith("@yahoo.com")) {
                throw new Exception("Only gmail.com and yahoo.com email addresses are accepted.");
            }
            if (usedEmails.contains(lowerEmail)) throw new Exception("This email is already registered.");

            String pwdErr = checkPassword(password);
            if (pwdErr != null) throw new Exception("Weak password: " + pwdErr);

            if (age < 18 || age > 40) throw new Exception("Age must be between 18 and 40.");
            if (!phone.matches("\\d{10}")) throw new Exception("Phone number must be exactly 10 digits.");

            String newId = "USR-" + userIdCount++;
            User newUser = new User(newId, username, lowerEmail, password, age, phone, "RUNNER");

            users.add(newUser);
            userByEmail.put(lowerEmail, newUser);
            usedEmails.add(lowerEmail);
            return newUser;
        }

        public static User login(String email, String password) throws Exception {
            String lowerEmail = email.toLowerCase().trim();
            User user = userByEmail.get(lowerEmail);

            if (user == null) {
                logFailedAttempt(lowerEmail, "Email not registered");
                throw new Exception("No account found with email: " + email);
            }

            if (user.isLocked()) {
                throw new Exception("Your account is locked until " + user.lockedUntil.format(DT_FMT));
            }

            if (user.status.equals("PENDING")) {
                throw new Exception("Your account is pending verification. Contact an admin to approve.");
            }

            if (!user.password.equals(password)) {
                user.failedAttempts++;
                logFailedAttempt(lowerEmail, "Wrong password (attempt #" + user.failedAttempts + ")");

                if (user.failedAttempts >= 5) {
                    user.lockedUntil = LocalDateTime.now().plusMinutes(30);
                    user.status = "LOCKED";
                    throw new Exception("ACCOUNT LOCKED! Wrong password entered 5 times. Locked for 30 minutes.");
                } else {
                    int remaining = 5 - user.failedAttempts;
                    throw new Exception("Wrong password. " + remaining + " attempts remaining.");
                }
            }

            user.failedAttempts = 0;
            return user;
        }

        private static String checkPassword(String pwd) {
            if (pwd == null || pwd.length() < 12) return "Must be at least 12 characters long.";
            boolean hasUpper = false, hasDigit = false, hasSpecial = false;
            String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
            for (char c : pwd.toCharArray()) {
                if (Character.isUpperCase(c)) hasUpper = true;
                if (Character.isDigit(c)) hasDigit = true;
                if (specialChars.indexOf(c) >= 0) hasSpecial = true;
            }
            if (!hasUpper) return "Must contain at least one uppercase letter.";
            if (!hasDigit) return "Must contain at least one numeric digit.";
            if (!hasSpecial) return "Must contain at least one special character.";
            return null;
        }

        private static void logFailedAttempt(String email, String reason) {
            try (PrintWriter writer = new PrintWriter(new FileWriter("logs/security.log", true))) {
                writer.println(LocalDateTime.now().format(DT_FMT) + " | " + email + " | " + reason);
            } catch (IOException e) {}
        }
    }

    // --- Race & Event Management ---
    public static class RaceMgr {
        public static Race create(String name, String location, LocalDate date, String distance, int maxCapacity, String adminId, String description) throws Exception {
            if (name.length() < 5) throw new Exception("Race name must be at least 5 characters.");

            int targetYear = date.getYear();
            for (Race r : races) {
                if (r.name.equalsIgnoreCase(name.trim()) && r.raceDate.getYear() == targetYear) {
                    throw new Exception("A race named '" + name + "' already exists in " + targetYear + ".");
                }
            }

            long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), date);
            if (daysUntil < 7) throw new Exception("Race date must be at least 7 days in future.");
            if (maxCapacity < 10) throw new Exception("Race capacity must be at least 10.");

            String raceId = "RACE-" + raceIdCount++;
            LocalDateTime startDateTime = date.atTime(7, 0);
            Race race = new Race(raceId, name.trim(), location.trim(), startDateTime, distance, maxCapacity, adminId, description);
            races.add(race);
            return race;
        }

        public static void edit(Race race, String newLoc, String newDesc, String capacityStr) throws Exception {
            long hoursLeft = ChronoUnit.HOURS.between(LocalDateTime.now(), race.raceDate);
            if (hoursLeft < 24) throw new Exception("Cannot edit race. Starts in less than 24 hours.");

            if (newLoc != null && !newLoc.isEmpty()) race.location = newLoc.trim();
            if (newDesc != null && !newDesc.isEmpty()) race.description = newDesc.trim();
            if (capacityStr != null && !capacityStr.isEmpty()) {
                int newCap = Integer.parseInt(capacityStr);
                if (newCap < race.registrations) throw new Exception("New capacity cannot be less than signups.");
                race.maxCapacity = newCap;
            }
            race.lastModified = LocalDateTime.now();
        }

        public static void deactivate(Race race) throws Exception {
            if (race.registrations > 0) throw new Exception("Cannot deactivate race with active registrations.");
            race.status = "INACTIVE";
        }
    }

    // --- Registration Prereqs & Penalty checks ---
    public static class RegMgr {
        public static Registration register(User runner, Race race) throws Exception {
            if (!race.status.equals("ACTIVE")) throw new Exception("This race is currently INACTIVE.");
            if (!race.isRegistrationOpen()) throw new Exception("Registration closed! Starts in less than 48 hours.");
            if (race.isFull()) throw new Exception("This race is at full capacity.");

            LocalDate targetDate = race.raceDate.toLocalDate();
            for (Registration reg : registrations) {
                if (reg.runnerId.equals(runner.id) && reg.status.equals("REGISTERED")) {
                    Race r = findRaceById(reg.raceId);
                    if (r != null && r.raceDate.toLocalDate().equals(targetDate)) {
                        throw new Exception("Double booking! You have another race on " + targetDate.format(DATE_FMT));
                    }
                }
            }

            int logsCount = 0;
            LocalDate cutoff = LocalDate.now().minusDays(30);
            for (TrainingLog log : trainingLogs) {
                if (log.runnerId.equals(runner.id) && !log.date.isBefore(cutoff)) {
                    logsCount++;
                }
            }
            if (logsCount < 5) throw new Exception("Eligibility check failed! You need $\\ge 5$ training logs in last 30 days (logged: " + logsCount + ").");

            boolean latePenalty = race.isWithin7Days();
            if (latePenalty) {
                runner.marathonPoints = Math.max(0, runner.marathonPoints - 50);
            }

            String regId = "REG-" + regIdCount++;
            Registration newReg = new Registration(regId, runner.id, race.id, latePenalty);
            registrations.add(newReg);
            race.registrations++;
            return newReg;
        }
    }

    // --- Training Workouts & Metric Logs ---
    public static class TrainingMgr {
        public static TrainingLog addLog(String runnerId, LocalDate date, double distanceKm, int durationMinutes, String notes) throws Exception {
            if (date.isAfter(LocalDate.now())) throw new Exception("Cannot log training for a future date.");
            if (distanceKm < 1.0 || distanceKm > 100.0) throw new Exception("Distance must be between 1.0 and 100.0 km.");

            double pace = (double) durationMinutes / distanceKm;
            if (pace < 3.0 || pace > 10.0) throw new Exception(String.format("Unrealistic pace (%.1f min/km). Must be between 3.0 and 10.0 min/km.", pace));

            for (TrainingLog log : trainingLogs) {
                if (log.runnerId.equals(runnerId) && log.date.equals(date) && log.distanceKm == distanceKm) {
                    throw new Exception("Duplicate run detected! A log for this date and distance already exists.");
                }
            }

            boolean flagged = false;
            double sum = 0;
            int count = 0;
            for (TrainingLog log : trainingLogs) {
                if (log.runnerId.equals(runnerId)) {
                    sum += log.distanceKm;
                    count++;
                }
            }
            if (count > 0 && distanceKm >= (sum / count) * 10) {
                flagged = true;
            }

            String logId = "TRN-" + logIdCount++;
            TrainingLog newLog = new TrainingLog(logId, runnerId, date, distanceKm, durationMinutes, notes);
            newLog.flagged = flagged;
            trainingLogs.add(newLog);
            return newLog;
        }

        public static void editLog(TrainingLog log, String distStr, String durStr, String notes) throws Exception {
            long monthsDiff = ChronoUnit.MONTHS.between(log.date, LocalDate.now());
            if (monthsDiff >= 6) throw new Exception("Cannot edit training logs older than 6 months.");

            double distance = log.distanceKm;
            int duration = log.durationMinutes;

            if (distStr != null && !distStr.isEmpty()) {
                distance = Double.parseDouble(distStr);
                if (distance < 1.0 || distance > 100.0) throw new Exception("Distance must be between 1.0 and 100.0 km.");
            }
            if (durStr != null && !durStr.isEmpty()) {
                duration = Integer.parseInt(durStr);
                if (duration < 1 || duration > 1440) throw new Exception("Duration must be between 1 and 1440 minutes.");
            }

            double pace = (double) duration / distance;
            if (pace < 3.0 || pace > 10.0) throw new Exception(String.format("Unrealistic pace (%.1f min/km). Must be between 3.0 and 10.0 min/km.", pace));

            log.distanceKm = distance;
            log.durationMinutes = duration;
            log.paceMinPerKm = pace;
            if (notes != null && !notes.isEmpty()) log.notes = notes;
            log.modifiedAt = LocalDateTime.now();
        }
    }

    // --- Forum Board Submissions ---
    public static class ForumMgr {
        public static ForumPost submit(User runner, String title, String content) throws Exception {
            if (runner.forumBanned) throw new Exception("You are currently banned from posting in the forum.");
            if (title.length() < 5) throw new Exception("Title must be at least 5 characters.");
            if (content.length() < 10) throw new Exception("Content must be at least 10 characters.");

            String foundBannedWord = null;
            String lowerContent = content.toLowerCase();
            for (String word : BLACKLIST) {
                if (lowerContent.contains(word)) {
                    foundBannedWord = word;
                    break;
                }
            }

            boolean hasLink = lowerContent.contains("http://") || lowerContent.contains("https://") || lowerContent.contains("www.");
            boolean flagged = (foundBannedWord != null);
            String reason = null;

            if (flagged) {
                reason = "Contains blacklisted word: '" + foundBannedWord + "'";
                runner.flaggedPosts++;
                if (runner.flaggedPosts >= 3) runner.forumBanned = true;
            } else if (hasLink) {
                reason = "URL link detected — routed to review queue";
            }

            String postId = "POST-" + postIdCount++;
            ForumPost post = new ForumPost(postId, runner.id, runner.username, title, content, hasLink, flagged, reason);
            forumPosts.add(post);
            return post;
        }

        public static int cleanup() {
            int before = forumPosts.size();
            forumPosts.removeIf(post -> ChronoUnit.DAYS.between(post.createdAt.toLocalDate(), LocalDate.now()) > 365);
            return before - forumPosts.size();
        }
    }

    // --- Statistics and CSV Exports ---
    public static class AnalyticsMgr {
        public static String exportTraining(User runner) throws IOException {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String file = "exports/training_" + runner.username + "_" + timestamp + ".csv";
            try (PrintWriter w = new PrintWriter(new FileWriter(file))) {
                w.println("LogID,Date,Distance(km),Duration(min),Pace,Flagged,Edited,Notes");
                for (TrainingLog log : trainingLogs) {
                    if (log.runnerId.equals(runner.id)) {
                        w.printf("%s,%s,%.2f,%d,%s,%s,%s,%s%n",
                                log.id, log.date.format(DATE_FMT), log.distanceKm, log.durationMinutes, log.getPace(),
                                log.flagged ? "YES" : "NO", log.modifiedAt != null ? "YES" : "NO",
                                log.notes != null ? log.notes.replace(",", ";") : "");
                    }
                }
            }
            return file;
        }

        public static String exportUsers() throws IOException {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String file = "exports/users_" + timestamp + ".csv";
            try (PrintWriter w = new PrintWriter(new FileWriter(file))) {
                w.println("UserID,Username,Email,Role,Status,Age,Phone,Points,JoinedDate");
                for (User u : users) {
                    w.printf("%s,%s,%s,%s,%s,%d,%s,%d,%s%n",
                            u.id, u.username, u.email, u.role, u.status, u.age, u.phone, u.marathonPoints, u.createdAt.format(DATE_FMT));
                }
            }
            return file;
        }

        public static String exportRaces() throws IOException {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String file = "exports/races_" + timestamp + ".csv";
            try (PrintWriter w = new PrintWriter(new FileWriter(file))) {
                w.println("RaceID,Name,Location,Date,Distance,SpotsRegistered,MaxCapacity,Status");
                for (Race r : races) {
                    w.printf("%s,%s,%s,%s,%s,%d,%d,%s%n",
                            r.id, r.name, r.location, r.raceDate.format(DT_FMT), r.distance, r.registrations, r.maxCapacity, r.status);
                }
            }
            return file;
        }

        public static String exportAllLogs() throws IOException {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String file = "exports/training_all_" + timestamp + ".csv";
            try (PrintWriter w = new PrintWriter(new FileWriter(file))) {
                w.println("LogID,RunnerID,Date,Distance(km),Duration(min),Pace,Flagged,Edited");
                for (TrainingLog l : trainingLogs) {
                    w.printf("%s,%s,%s,%.2f,%d,%s,%s,%s%n",
                            l.id, l.runnerId, l.date.format(DATE_FMT), l.distanceKm, l.durationMinutes, l.getPace(),
                            l.flagged ? "YES" : "NO", l.modifiedAt != null ? "YES" : "NO");
                }
            }
            return file;
        }
    }

    public static void main(String[] args) {
        new File("exports").mkdirs();
        new File("logs").mkdirs();
        loadSeedData();
        printBanner();
        while (true) {
            showMainMenu();
        }
    }

    public static void loadSeedData() {
        users.clear();
        races.clear();
        trainingLogs.clear();
        forumPosts.clear();
        registrations.clear();
        userByEmail.clear();
        usedEmails.clear();

        User admin = new User("USR-1000", "admin", "admin@gmail.com", "Admin@Pass123", 30, "9999900000", "ADMIN");
        users.add(admin);
        userByEmail.put("admin@gmail.com", admin);
        usedEmails.add("admin@gmail.com");

        User runner = new User("USR-1001", "john_runner", "john@gmail.com", "Runner@Pass123", 28, "9988776655", "RUNNER");
        runner.status = "VERIFIED";
        users.add(runner);
        userByEmail.put("john@gmail.com", runner);
        usedEmails.add("john@gmail.com");

        races.add(new Race("RACE-101", "Mumbai Marathon 2026", "Mumbai", LocalDateTime.now().plusDays(30), "Full Marathon", 500, "USR-1000", "Annual city marathon."));
        races.add(new Race("RACE-102", "Delhi 10K Challenge", "Delhi", LocalDateTime.now().plusDays(45), "10K", 300, "USR-1000", "Historic Delhi run."));
        races.add(new Race("RACE-103", "Bangalore Half Marathon", "Bangalore", LocalDateTime.now().plusDays(60), "Half Marathon", 400, "USR-1000", "Tech city half marathon."));
        races.add(new Race("RACE-104", "Pune Fun Run 5K", "Pune", LocalDateTime.now().plusDays(20), "5K", 200, "USR-1000", "Beginner-friendly 5K."));

        LocalDate today = LocalDate.now();
        String[] logNotes = {"Easy jog", "Long run", "Speed work", "Recovery run", "Tempo run", "Hill training", "Fartlek session"};
        for (int i = 1; i <= 7; i++) {
            trainingLogs.add(new TrainingLog("TRN-500" + i, "USR-1001", today.minusDays(i * 3L), 8 + i, 50 + (i * 5), logNotes[i - 1]));
        }

        ForumPost post = new ForumPost("POST-3001", "USR-1001", "john_runner", "My First Marathon Training Tips",
                "Hello everyone! After 3 months of training, I have learned that consistency is the most important thing. Log every run, stay hydrated, and trust the process. Good luck to all runners!",
                false, false, null);
        post.status = "APPROVED";
        forumPosts.add(post);
    }

    // ==========================================
    //  SECTION 4 — USER INTERFACE MENUS
    // ==========================================

    private static void showMainMenu() {
        line();
        println("  MAIN MENU");
        line();
        println("  [1] Register as New Runner");
        println("  [2] Login");
        println("  [0] Exit");
        line();

        int choice = readInt("Enter choice", 0, 2);

        if (choice == 1) {
            handleRegister();
        } else if (choice == 2) {
            handleLogin();
        } else if (choice == 0) {
            println("\n  Thank you for using Marathon Runner Portal. Goodbye!\n");
            sc.close();
            System.exit(0);
        }
    }

    private static void handleRegister() {
        line();
        println("  REGISTER AS NEW RUNNER");
        line();
        try {
            String username = readStr("Username (letters, digits, underscore only)");
            String email    = readStr("Email Address");
            String password = readStr("Password");
            String confirm  = readStr("Confirm Password");

            if (!password.equals(confirm)) {
                err("Passwords do not match.");
                pause();
                return;
            }

            int age = readInt("Age", 1, 120);
            String phone = readStr("Phone Number (10 digits)");

            User u = UserMgr.register(username, email, password, age, phone);

            println();
            ok("Registration successful!");
            println("  User ID         : " + u.id);
            println("  Marathon Points : 500 points credited!");
            println("  Status          : PENDING — Admin must verify your account.");
            pause();
        } catch (Exception e) {
            err(e.getMessage());
            pause();
        }
    }

    private static void handleLogin() {
        line();
        println("  LOGIN");
        line();
        try {
            String email    = readStr("Email Address");
            String password = readStr("Password");

            User u = UserMgr.login(email, password);
            currentUser = u;
            ok("Welcome back, " + u.username + "!");

            if (u.role.equals("ADMIN")) {
                showAdminMenu();
            } else {
                showRunnerMenu();
            }
            currentUser = null;
        } catch (Exception e) {
            err(e.getMessage());
            pause();
        }
    }

    private static void showRunnerMenu() {
        while (true) {
            line();
            println("  RUNNER MENU — Welcome, " + currentUser.username + "!");
            println("  Points: " + currentUser.marathonPoints + " pts  |  Status: " + currentUser.status);
            line();
            println("  [1]  View Profile & Completed Races");
            println("  [2]  View Available Races");
            println("  [3]  Register for a Race");
            println("  [4]  View My Registrations");
            println("  [5]  Add Training Log");
            println("  [6]  View Training Dashboard");
            println("  [7]  Edit a Training Log");
            println("  [8]  View Forum Posts");
            println("  [9]  Post in Forum");
            println("  [10] Export Training Logs to CSV");
            println("  [0]  Logout");
            line();

            int choice = readInt("Enter choice", 0, 10);

            switch (choice) {
                case 1  -> viewProfile();
                case 2  -> viewRaces();
                case 3  -> registerRace();
                case 4  -> viewRegistrations();
                case 5  -> addTraining();
                case 6  -> viewTrainingDashboard();
                case 7  -> editTraining();
                case 8  -> viewForum();
                case 9  -> postForum();
                case 10 -> exportTraining();
                case 0  -> { println("  Logged out."); return; }
            }
        }
    }

    private static void showAdminMenu() {
        while (true) {
            line();
            println("  ADMIN MENU — Welcome, " + currentUser.username + "!");
            line();
            println("  [1] Manage Users (Verify Accounts)");
            println("  [2] Manage Races (Add / Edit / Deactivate)");
            println("  [3] Moderate Forum (Approve / Reject)");
            println("  [4] Analytics Dashboard");
            println("  [5] Export Reports to CSV");
            println("  [6] View Security Log");
            println("  [7] Mark Race Completion");
            println("  [0] Logout");
            line();

            int choice = readInt("Enter choice", 0, 7);

            switch (choice) {
                case 1 -> adminUsers();
                case 2 -> adminRaces();
                case 3 -> adminForum();
                case 4 -> adminAnalytics();
                case 5 -> adminExports();
                case 6 -> adminSecurityLog();
                case 7 -> adminMarkCompleted();
                case 0 -> { println("  Logged out."); return; }
            }
        }
    }

    // ==========================================
    //  SECTION 5 — RUNNER SUB-FEATURES
    // ==========================================

    private static void viewProfile() {
        line();
        println("  MY PROFILE");
        line();
        println("  User ID         : " + currentUser.id);
        println("  Username        : " + currentUser.username);
        println("  Email           : " + currentUser.email);
        println("  Age             : " + currentUser.age);
        println("  Phone           : " + currentUser.phone);
        println("  Points          : " + currentUser.marathonPoints + " pts");
        println("  Forum Status    : " + (currentUser.forumBanned ? "BANNED" : "Active"));
        println("  Joined On       : " + currentUser.createdAt.format(DT_FMT));
        line();

        println("  COMPLETED RACES:");
        boolean hasCompleted = false;
        Map<String, Integer> pbs = new HashMap<>();
        for (Registration r : registrations) {
            if (r.runnerId.equals(currentUser.id) && r.status.equals("COMPLETED")) {
                Race race = findRaceById(r.raceId);
                if (race != null) {
                    println("    " + race.name + " (" + race.distance + ") | Date: "
                            + race.raceDate.format(DATE_FMT) + " | Time: "
                            + r.completionMinutes/60 + "h " + r.completionMinutes%60 + "min");
                    pbs.merge(race.distance, r.completionMinutes, Math::min);
                    hasCompleted = true;
                }
            }
        }
        if (!hasCompleted) println("    No completed races yet.");

        if (!pbs.isEmpty()) {
            println("  PERSONAL BESTS:");
            for (Map.Entry<String, Integer> pb : pbs.entrySet()) {
                println("    " + pb.getKey() + " : " + pb.getValue()/60 + "h " + pb.getValue()%60 + "min");
            }
        }

        line();
        println("  Update Settings:");
        println("  [1] Change Age    [2] Change Phone    [0] Back");
        int c = readInt("Choice", 0, 2);
        if (c == 1) {
            int age = readInt("New Age", 18, 40);
            currentUser.age = age;
            ok("Age updated.");
        } else if (c == 2) {
            String ph = readStr("New Phone (10 digits)");
            if (ph.matches("\\d{10}")) {
                currentUser.phone = ph;
                ok("Phone updated.");
            } else {
                err("Phone must be exactly 10 digits.");
            }
        }
        pause();
    }

    private static void viewRaces() {
        line();
        println("  AVAILABLE RACES");
        line();
        println("  Filter by: [1] Show All  [2] Location  [3] Distance  [0] Back");
        int f = readInt("Choose filter", 0, 3);
        if (f == 0) return;

        String loc = null;
        String dist = null;
        if (f == 2) loc = readStr("Enter city keyword").toLowerCase();
        if (f == 3) {
            println("  [1] 5K  [2] 10K  [3] Half Marathon  [4] Full Marathon");
            int d = readInt("Choose", 1, 4);
            dist = new String[]{"5K", "10K", "Half Marathon", "Full Marathon"}[d - 1];
        }

        Set<String> signedUp = new HashSet<>();
        for (Registration reg : registrations) {
            if (reg.runnerId.equals(currentUser.id) && reg.status.equals("REGISTERED")) {
                signedUp.add(reg.raceId);
            }
        }

        line();
        println(String.format("  %-8s %-26s %-12s %-12s %-15s %-7s %s", "ID", "Name", "Location", "Date", "Distance", "Spots", "Status"));
        line();
        for (Race r : races) {
            if (!r.status.equals("ACTIVE")) continue;
            if (!r.isRegistrationOpen()) continue;
            if (signedUp.contains(r.id)) continue;

            if (loc != null && !r.location.toLowerCase().contains(loc)) continue;
            if (dist != null && !r.distance.equals(dist)) continue;

            int spotsLeft = r.maxCapacity - r.registrations;
            String note = r.isFull() ? "FULL" : (r.isWithin7Days() ? "Late Penalty" : "Open");

            println(String.format("  %-8s %-26s %-12s %-12s %-15s %-7d %s",
                    r.id, r.name, r.location, r.raceDate.format(DATE_FMT), r.distance, spotsLeft, note));

            if (r.lastModified != null) {
                println("           ↳ Last Modified: " + r.lastModified.format(DT_FMT));
            }
        }
        pause();
    }

    private static void registerRace() {
        line();
        println("  REGISTER FOR A RACE");
        line();
        String raceId = readStr("Enter Race ID to register (e.g. RACE-101)");
        Race race = findRaceById(raceId);
        if (race == null) {
            err("Race not found.");
            pause();
            return;
        }

        try {
            Registration reg = RegMgr.register(currentUser, race);
            ok("Successfully registered!");
            println("  Registration ID: " + reg.id);
            if (reg.latePenalty) {
                warn("Applied late registration point deduction (-50 points). New balance: " + currentUser.marathonPoints + " pts");
            }
        } catch (Exception e) {
            err(e.getMessage());
        }
        pause();
    }

    private static void viewRegistrations() {
        line();
        println("  MY REGISTRATIONS");
        line();
        boolean any = false;
        for (Registration reg : registrations) {
            if (!reg.runnerId.equals(currentUser.id)) continue;
            Race race = findRaceById(reg.raceId);
            if (race != null) {
                println("  Reg ID   : " + reg.id);
                println("  Race     : " + race.name + " (" + race.distance + ")");
                println("  Date     : " + race.raceDate.format(DT_FMT));
                println("  Status   : " + reg.status);
                if (reg.latePenalty) println("  Penalties: 50 point late fee applied");
                line();
                any = true;
            }
        }
        if (!any) println("  You have no active or past registrations.");
        pause();
    }

    private static void addTraining() {
        line();
        println("  ADD TRAINING LOG");
        line();
        try {
            String dateStr = readStr("Workout Date (dd-MM-yyyy)");
            LocalDate date = LocalDate.parse(dateStr, DATE_FMT);

            double dist = readDouble("Distance in km (1.0 to 100.0)", 0.1, 200.0);
            int duration = readInt("Duration in minutes", 1, 1440);
            String notes = readStrOptional("Notes (optional)");

            TrainingLog log = TrainingMgr.addLog(currentUser.id, date, dist, duration, notes);

            ok("Training workout saved!");
            println("  Log ID  : " + log.id);
            println("  Pace    : " + log.getPace());
            if (log.flagged) {
                warn("AUTO-FLAGGED: This workout is 10x your average training distance!");
            }
        } catch (Exception e) {
            err(e.getMessage());
        }
        pause();
    }

    private static void viewTrainingDashboard() {
        line();
        println("  TRAINING DASHBOARD");
        line();
        List<TrainingLog> logs = new ArrayList<>();
        for (TrainingLog log : trainingLogs) {
            if (log.runnerId.equals(currentUser.id)) logs.add(log);
        }

        if (logs.isEmpty()) {
            println("  No training records logged yet.");
            pause();
            return;
        }

        double totalDist = 0;
        double totalPace = 0;
        double bestDist  = 0;
        double bestPace  = Double.MAX_VALUE;

        for (TrainingLog l : logs) {
            totalDist += l.distanceKm;
            totalPace += l.paceMinPerKm;
            if (l.distanceKm > bestDist) bestDist = l.distanceKm;
            if (l.paceMinPerKm < bestPace) bestPace = l.paceMinPerKm;
        }

        double avgDist = totalDist / logs.size();
        double avgPace = totalPace / logs.size();

        println(String.format("  Total Workouts    : %d", logs.size()));
        println(String.format("  Total Distance    : %.2f km", totalDist));
        println(String.format("  Average Distance  : %.2f km", avgDist));
        println(String.format("  Best Distance Run : %.2f km", bestDist));

        int apMin = (int) avgPace;
        int apSec = (int) Math.round((avgPace - apMin) * 60);
        println(String.format("  Average Pace      : %d:%02d min/km", apMin, apSec));

        int bpMin = (int) bestPace;
        int bpSec = (int) Math.round((bestPace - bpMin) * 60);
        println(String.format("  Fastest Pace Run  : %d:%02d min/km", bpMin, bpSec));
        line();

        println("  Sort Workouts:");
        println("  [1] Date Descending  [2] Distance Descending  [3] Pace Ascending  [0] Back");
        int sort = readInt("Choice", 0, 3);
        if (sort == 0) return;

        List<TrainingLog> sorted = new ArrayList<>(logs);
        if (sort == 1) sorted.sort((a, b) -> b.date.compareTo(a.date));
        if (sort == 2) sorted.sort((a, b) -> Double.compare(b.distanceKm, a.distanceKm));
        if (sort == 3) sorted.sort((a, b) -> Double.compare(a.paceMinPerKm, b.paceMinPerKm));

        line();
        println(String.format("  %-10s %-12s %-10s %-10s %-12s %s", "ID", "Date", "Dist(km)", "Time(min)", "Pace", "Badge"));
        line();
        for (TrainingLog l : sorted) {
            String badge = (l.modifiedAt != null ? "[EDITED] " : "") + (l.flagged ? "[FLAG]" : "");
            println(String.format("  %-10s %-12s %-10.1f %-10d %-12s %s",
                    l.id, l.date.format(DATE_FMT), l.distanceKm, l.durationMinutes, l.getPace(), badge));
        }
        pause();
    }

    private static void editTraining() {
        line();
        println("  EDIT TRAINING LOG");
        line();
        List<TrainingLog> logs = new ArrayList<>();
        for (TrainingLog log : trainingLogs) {
            if (log.runnerId.equals(currentUser.id)) logs.add(log);
        }

        if (logs.isEmpty()) {
            println("  No training logs to edit.");
            pause();
            return;
        }

        for (TrainingLog l : logs) {
            println("  " + l.id + " | Date: " + l.date.format(DATE_FMT) + " | Dist: " + l.distanceKm + " km | Pace: " + l.getPace());
        }
        line();

        String id = readStr("Enter Log ID to edit");
        TrainingLog log = null;
        for (TrainingLog l : logs) {
            if (l.id.equals(id)) { log = l; break; }
        }

        if (log == null) {
            err("Log not found.");
            pause();
            return;
        }

        println("  (Leave empty to keep unchanged)");
        String dist = readStrOptional("New Distance in km [current: " + log.distanceKm + "]");
        String dur  = readStrOptional("New Duration in min [current: " + log.durationMinutes + "]");
        String note = readStrOptional("New Notes [current: " + (log.notes != null ? log.notes : "") + "]");

        try {
            TrainingMgr.editLog(log, dist, dur, note);
            ok("Workout log updated!");
        } catch (Exception e) {
            err(e.getMessage());
        }
        pause();
    }

    private static void viewForum() {
        line();
        println("  COMMUNITY FORUM BOARD");
        line();
        boolean found = false;
        for (ForumPost post : forumPosts) {
            if (post.status.equals("APPROVED")) {
                println("  ID     : " + post.id);
                println("  Author : " + post.authorName + "  |  " + post.createdAt.format(DT_FMT));
                println("  Title  : " + post.title);
                println("  Content: " + post.content);
                line();
                found = true;
            }
        }
        if (!found) println("  No approved forum posts available yet.");
        pause();
    }

    private static void postForum() {
        line();
        println("  SUBMIT NEW FORUM POST");
        line();
        try {
            String title   = readStr("Title (min 5 chars)");
            String content = readStr("Content (min 10 chars)");

            ForumPost p = ForumMgr.submit(currentUser, title, content);

            if (p.status.equals("FLAGGED")) {
                warn("POST FLAGGED: " + p.flagReason);
                warn("A flag was added to your account (" + currentUser.flaggedPosts + "/3 flags).");
            } else {
                ok("Post submitted for review! ID: " + p.id);
                if (p.containsLink) warn("Your post contains a link and is queued for manual admin approval.");
            }
        } catch (Exception e) {
            err(e.getMessage());
        }
        pause();
    }

    private static void exportTraining() {
        line();
        println("  EXPORT RUNNING WORKOUTS TO CSV");
        line();
        try {
            String path = AnalyticsMgr.exportTraining(currentUser);
            ok("Logs exported successfully!");
            println("  Saved CSV File to: " + path);
        } catch (Exception e) {
            err("Could not export: " + e.getMessage());
        }
        pause();
    }

    // ==========================================
    //  SECTION 6 — ADMIN SUB-FEATURES
    // ==========================================

    private static void adminUsers() {
        line();
        println("  ADMIN — ACCOUNT MANAGEMENT");
        line();
        println("  [1] List All Registered Users");
        println("  [2] Verify Pending Runner Account");
        println("  [0] Back");
        int choice = readInt("Choice", 0, 2);
        if (choice == 0) return;

        if (choice == 1) {
            println();
            println(String.format("  %-10s %-15s %-28s %-7s %-10s %s", "ID", "Username", "Email", "Role", "Status", "Points"));
            line();
            for (User u : users) {
                println(String.format("  %-10s %-15s %-28s %-7s %-10s %s",
                        u.id, u.username, u.email, u.role, u.status,
                        u.role.equals("RUNNER") ? u.marathonPoints + " pts" : "-"));
            }
        } else if (choice == 2) {
            println("  PENDING ACCOUNTS:");
            boolean any = false;
            for (User u : users) {
                if (u.status.equals("PENDING")) {
                    println("  " + u.id + " | " + u.username + " | " + u.email + " | Age: " + u.age);
                    any = true;
                }
            }
            if (!any) {
                println("  No accounts pending approval.");
                pause();
                return;
            }

            String id = readStr("Enter User ID to verify");
            boolean verified = false;
            for (User u : users) {
                if (u.id.equals(id) && u.status.equals("PENDING")) {
                    u.status = "VERIFIED";
                    verified = true;
                    ok("Runner account approved!");
                    break;
                }
            }
            if (!verified) err("User not found or already verified.");
        }
        pause();
    }

    private static void adminRaces() {
        line();
        println("  ADMIN — RACE MANAGEMENT");
        line();
        println("  [1] List All Scheduled Races");
        println("  [2] Add New Race Event");
        println("  [3] Edit Existing Race Event");
        println("  [4] Deactivate Race Event");
        println("  [0] Back");
        int choice = readInt("Choice", 0, 4);
        if (choice == 0) return;

        if (choice == 1) {
            println();
            println(String.format("  %-8s %-26s %-12s %-15s %-15s %-7s/%-7s %s", "ID", "Name", "Location", "Date", "Distance", "Regs", "Cap", "Status"));
            line();
            for (Race r : races) {
                println(String.format("  %-8s %-26s %-12s %-15s %-15s %-7d/%-7d %s",
                        r.id, r.name, r.location, r.raceDate.format(DATE_FMT), r.distance, r.registrations, r.maxCapacity, r.status));
            }
        } else if (choice == 2) {
            try {
                String name = readStr("Race Name (min 5 chars)");
                String location = readStr("Location (City)");
                String dateStr = readStr("Race Date (dd-MM-yyyy)");
                LocalDate date = LocalDate.parse(dateStr, DATE_FMT);

                println("  Distance: [1] 5K  [2] 10K  [3] Half Marathon  [4] Full Marathon");
                int d = readInt("Distance", 1, 4);
                String dist = new String[]{"5K", "10K", "Half Marathon", "Full Marathon"}[d - 1];

                int cap = readInt("Max Participant Capacity", 10, 100000);
                String desc = readStrOptional("Description (optional)");

                Race r = RaceMgr.create(name, location, date, dist, cap, currentUser.id, desc);
                ok("Race added! ID: " + r.id);
            } catch (Exception e) {
                err(e.getMessage());
            }
        } else if (choice == 3) {
            String id = readStr("Enter Race ID to edit");
            Race race = findRaceById(id);
            if (race == null) {
                err("Race not found.");
                pause();
                return;
            }
            println("  Editing: " + race.name);
            println("  (Leave empty to skip edits)");
            String loc = readStrOptional("New Location [current: " + race.location + "]");
            String desc = readStrOptional("New Description [current: " + race.description + "]");
            String cap = readStrOptional("New Capacity [current: " + race.maxCapacity + "]");

            try {
                RaceMgr.edit(race, loc, desc, cap);
                ok("Race details updated.");
            } catch (Exception e) {
                err(e.getMessage());
            }
        } else if (choice == 4) {
            String id = readStr("Enter Race ID to deactivate");
            Race race = findRaceById(id);
            if (race == null) {
                err("Race not found.");
                pause();
                return;
            }
            try {
                RaceMgr.deactivate(race);
                ok("Race deactivated.");
            } catch (Exception e) {
                err(e.getMessage());
            }
        }
        pause();
    }

    private static void adminForum() {
        line();
        println("  ADMIN — FORUM MODERATION QUEUE");
        line();
        boolean any = false;
        for (ForumPost post : forumPosts) {
            if (post.status.equals("PENDING") || post.status.equals("FLAGGED")) {
                println("  Post ID : " + post.id);
                println("  Title   : " + post.title);
                println("  Author  : " + post.authorName);
                println("  Status  : " + post.status);
                if (post.flagReason != null) println("  Reason  : " + post.flagReason);
                println("  Content : " + post.content);
                line();
                any = true;
            }
        }

        if (!any) {
            println("  All quiet! No posts awaiting moderation.");
            pause();
            return;
        }

        println("  [1] Approve Post  [2] Reject Post  [3] Delete Posts Older than 1 Year  [0] Back");
        int choice = readInt("Choice", 0, 3);
        if (choice == 0) return;

        if (choice == 3) {
            int count = ForumMgr.cleanup();
            ok("Removed " + count + " post(s) older than 365 days.");
            pause();
            return;
        }

        String id = readStr("Enter Post ID to moderate");
        ForumPost post = null;
        for (ForumPost p : forumPosts) {
            if (p.id.equals(id)) { post = p; break; }
        }

        if (post == null) {
            err("Post not found.");
            pause();
            return;
        }

        if (choice == 1) {
            post.status = "APPROVED";
            post.flagReason = null;
            ok("Post approved.");
        } else if (choice == 2) {
            post.status = "REJECTED";
            User author = findUserById(post.authorId);
            if (author != null) {
                author.flaggedPosts++;
                if (author.flaggedPosts >= 3) author.forumBanned = true;
            }
            ok("Post rejected.");
        }
        pause();
    }

    private static void adminAnalytics() {
        line();
        println("  ADMIN — SYSTEM ANALYTICS");
        line();

        long runners = users.stream().filter(u -> u.role.equals("RUNNER")).count();
        long verified = users.stream().filter(u -> u.role.equals("RUNNER") && u.status.equals("VERIFIED")).count();
        long pending = users.stream().filter(u -> u.role.equals("RUNNER") && u.status.equals("PENDING")).count();
        long admins = users.stream().filter(u -> u.role.equals("ADMIN")).count();

        println("  ── USERS ──────────────────────────────────");
        println("  Total Runner Accounts       : " + runners);
        println("  Verified Runner Accounts    : " + verified);
        println("  Pending Runner Accounts     : " + pending);
        println("  Admin Accounts              : " + admins);

        long activeRaces = races.stream().filter(r -> r.status.equals("ACTIVE")).count();
        long regs = registrations.size();

        println("\n  ── RACES & REGISTRATIONS ──────────────────");
        println("  Total Races Scheduled       : " + races.size());
        println("  Active Races Scheduled      : " + activeRaces);
        println("  Total Signups (All Races)   : " + regs);

        println("\n  ── FORUM BOARD ────────────────────────────");
        long approved = forumPosts.stream().filter(p -> p.status.equals("APPROVED")).count();
        long pReview  = forumPosts.stream().filter(p -> p.status.equals("PENDING") || p.status.equals("FLAGGED")).count();
        println("  Approved Community Posts   : " + approved);
        println("  Moderation Review Queue     : " + pReview);

        println("\n  ── SIGNUPS BY RACE ────────────────────────");
        for (Race r : races) {
            println(String.format("  %-30s : %d / %d slots filled", r.name, r.registrations, r.maxCapacity));
        }

        line();
        pause();
    }

    private static void adminExports() {
        line();
        println("  ADMIN — EXPORT SUMMARIES TO CSV");
        line();
        println("  [1] Export User Database");
        println("  [2] Export Race Events");
        println("  [3] Export All Runner Training Logs");
        println("  [0] Back");
        int choice = readInt("Choice", 0, 3);
        if (choice == 0) return;

        try {
            String path = "";
            if (choice == 1) path = AnalyticsMgr.exportUsers();
            if (choice == 2) path = AnalyticsMgr.exportRaces();
            if (choice == 3) path = AnalyticsMgr.exportAllLogs();

            ok("Export saved successfully!");
            println("  File Location: " + path);
        } catch (Exception e) {
            err("Export failed: " + e.getMessage());
        }
        pause();
    }

    private static void adminSecurityLog() {
        line();
        println("  ADMIN — SECURITY ATTEMPTS AUDIT LOG");
        line();
        try (BufferedReader reader = new BufferedReader(new FileReader("logs/security.log"))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                println("  " + line);
                count++;
            }
            if (count == 0) println("  No failed login attempts in log.");
        } catch (IOException e) {
            println("  No security audit log file exists.");
        }
        pause();
    }

    private static void adminMarkCompleted() {
        line();
        println("  ADMIN — MARK RACE COMPLETED");
        line();
        println("  Active Registrations:");
        boolean any = false;
        for (Registration reg : registrations) {
            if (!reg.status.equals("REGISTERED")) continue;
            Race race = findRaceById(reg.raceId);
            User user = findUserById(reg.runnerId);
            if (race != null && user != null) {
                println("  " + reg.id + " | Runner: " + user.username + " | Race: " + race.name);
                any = true;
            }
        }

        if (!any) {
            println("  No active registrations found.");
            pause();
            return;
        }

        String id = readStr("Enter Registration ID to mark completed");
        Registration reg = null;
        for (Registration r : registrations) {
            if (r.id.equals(id) && r.status.equals("REGISTERED")) {
                reg = r;
                break;
            }
        }

        if (reg == null) {
            err("Registration not found or already completed.");
            pause();
            return;
        }

        int mins = readInt("Completion Time in minutes (e.g. 210)", 1, 1440);
        reg.status = "COMPLETED";
        reg.completionMinutes = mins;

        ok("Race completion time successfully recorded!");
        pause();
    }

    // ==========================================
    //  SECTION 7 — HELPER IO FUNCTIONS
    // ==========================================

    private static Race findRaceById(String id) {
        for (Race r : races) if (r.id.equals(id)) return r;
        return null;
    }

    private static User findUserById(String id) {
        for (User u : users) if (u.id.equals(id)) return u;
        return null;
    }

    private static String readStr(String prompt) {
        while (true) {
            System.out.print("  > " + prompt + ": ");
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) return input;
            println("  Input cannot be empty. Please try again.");
        }
    }

    private static String readStrOptional(String prompt) {
        System.out.print("  > " + prompt + ": ");
        return sc.nextLine().trim();
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print("  > " + prompt + " [" + min + "-" + max + "]: ");
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                if (val >= min && val <= max) return val;
                println("  Enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                println("  Invalid format. Enter a whole number.");
            }
        }
    }

    private static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print("  > " + prompt + ": ");
            try {
                double val = Double.parseDouble(sc.nextLine().trim());
                if (val >= min && val <= max) return val;
                println("  Enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                println("  Invalid format. Enter a decimal number.");
            }
        }
    }

    private static void println(String s) { System.out.println(s); }
    private static void println()         { System.out.println(); }
    private static void line()            { System.out.println("  " + "─".repeat(60)); }
    private static void ok(String msg)    { System.out.println("\n  ✓ SUCCESS: " + msg + "\n"); }
    private static void err(String msg)   { System.out.println("  ✗ ERROR: " + msg); }
    private static void warn(String msg)  { System.out.println("  ⚠ WARNING: " + msg); }

    private static void pause() {
        System.out.print("\n  Press Enter to continue...");
        sc.nextLine();
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║        MARATHON RUNNER PORTAL  —  v1.0.0                ║");
        System.out.println("  ║        Java Console Application                         ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════╣");
        System.out.println("  ║  Default Accounts (ready to use):                       ║");
        System.out.println("  ║  Admin  → admin@gmail.com    password: Admin@Pass123     ║");
        System.out.println("  ║  Runner → john@gmail.com     password: Runner@Pass123    ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }
}
