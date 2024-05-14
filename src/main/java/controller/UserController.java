package controller;

import jakarta.servlet.http.HttpSession;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import services.MatchService;
import services.TournamentService;
import services.UserEnrollmentService;
import services.UserService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static model.UserRole.REFEREE;

@Controller
@RequestMapping("/api/users")
public class UserController {

    private Encoder encoder = Encoder.getInstance();
    @Autowired
    private UserEnrollmentService userEnrollmentService;

    @Autowired
    private TournamentService tournamentService;

    private static User userJmeker;
    @GetMapping("/getUserJmeker")
    public static User getUserJmeker() {
        return userJmeker;
    }

    @GetMapping("/register.html")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User newUser) {
        try {
            String pass = Encoder.encodingPassword(newUser.getPassword());
            newUser.setPassword(pass);
            userService.registerUser(newUser);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user: " + e.getMessage());
        }
    }

    @GetMapping("/create_user_account.html")
    public String showCreateUserPage() {
        return "create_user_account";
    }

    @PostMapping("/create_user_account")
    public ResponseEntity<String> createUser(@RequestBody User newUser) {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        else {
            try {
                String pass = Encoder.encodingPassword(newUser.getPassword());
                newUser.setPassword(pass);
                userService.registerUser(newUser);
                return ResponseEntity.ok("User created successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user: " + e.getMessage());
            }
        }
    }

    @Autowired
    UserService userService;

    @Autowired
    MatchService matchService;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/login.html")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/api/users/dashboard")
    public ResponseEntity<?> accessDashboard(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");

        if (username != null && role != null) {
            return ResponseEntity.status(HttpStatus.OK).body("/api/users/" + role.toLowerCase() + "_dashboard.html?username=" + username);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        boolean isValidUser = userService.validateCredentials(user.getUsername(), user.getPassword());
        if (isValidUser) {
            String role = userService.getUserRole(user.getUsername());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), null);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authenticationToken);
            if ("ADMINISTRATOR".equals(role)) {
                userJmeker= userService.getUserByUsername(user.getUsername());
                return ResponseEntity.status(HttpStatus.OK).body("/api/users/admin_dashboard.html?username=" + user.getUsername());
            } else if ("REFEREE".equals(role)) {
                userJmeker= userService.getUserByUsername(user.getUsername());
                System.out.println("userJmeker: " + userJmeker.getUsername());
                return ResponseEntity.status(HttpStatus.OK).body("/api/users/referee_dashboard.html?username=" + user.getUsername());
            } else {
                userJmeker= userService.getUserByUsername(user.getUsername());
                return ResponseEntity.status(HttpStatus.OK).body("/api/users/player_dashboard.html?username=" + user.getUsername());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping("/player_dashboard.html")
    public String showPlayerDashboard(@RequestParam("username") String username, Model model) {
        if(Objects.equals(username, userJmeker.getUsername())) {
            model.addAttribute("username", username);
            return "player_dashboard";
        }
        else {
            return "redirect:/api/users/login.html";
        }
    }

    @GetMapping("/referee_dashboard.html")
    public String showRefereeDashboard(@RequestParam("username") String username, Model model) {
        if(Objects.equals(username, userJmeker.getUsername())) {
            model.addAttribute("username", username);
            return "referee_dashboard";
        }
        else {
            return "redirect:/api/users/login.html";
        }
    }

    @GetMapping("/admin_dashboard.html")
    public String showAdminDashboard(@RequestParam("username") String username, Model model) {
        if(Objects.equals(username, userJmeker.getUsername())) {
            model.addAttribute("username", username);
            return "admin_dashboard";
        }
        else {
            return "redirect:/api/users/login.html";
        }
    }

    @GetMapping("/user_account_update.html")
    public String showUpdateAccountInformation(Model model, @RequestParam String username) {
        if (!username.equals(userJmeker.getUsername())) {
            return "redirect:/api/users/login.html";
        }
        else {
            User user = userService.getUserByUsername(username);
            user.setPassword(Encoder.decodingPassword(user.getPassword()));
            model.addAttribute("user", user);
            return "user_account_update";
        }
    }

    @GetMapping("/delete_account.html")
    public String showDeleteAccountDashboard() {
        if (userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return "delete_account";
        }
        else
            return "redirect:/api/users/login.html";
    }

    @GetMapping("/admin_view_users.html")
    public String showAdminViewUsersDashboard(Model model) {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return "redirect:/api/users/login.html";
        }
        else {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "admin_view_users";
        }
    }

    @GetMapping("/admin_user_management.html")
    public String showAdminUserManagementDashboard(Model model) {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return "redirect:/api/users/login.html";
        }
        else
            return "admin_user_management";
    }

    @GetMapping("/update_account.html")
    public String showUpdateAccountDashboard(Model model) {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return "redirect:/api/users/login.html";
        }
        else {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "update_account";
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        else {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }
    }

    @GetMapping("/tournament_registration.html")
    public String showTournamentRegistrationDashboard(@RequestParam("username") String username) {
        if(Objects.equals(username, userJmeker.getUsername())) {
            return "redirect:/api/tournaments/tournament_registration.html?username=" + username;
        }
        else
            return "redirect:/api/users/login.html";
    }

    @GetMapping("/schedule.html")
    public String showScheduleDashboard(@RequestParam("username") String username) {
        if(Objects.equals(username, userJmeker.getUsername())) {
            return "redirect:/api/matches/schedule.html" + username;
        }
        else
            return "redirect:/api/users/login.html";
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        else {
            userService.deleteUserById(userId);
            return ResponseEntity.ok("User deleted successfully");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestParam Long userId,
                                           @RequestParam(required = false) String username,
                                           @RequestParam(required = false) String password,
                                           @RequestParam(required = false) String email,
                                           @RequestParam(required = false) UserRole role) {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        else {
            String pass = Encoder.encodingPassword(password);
            User updatedUser = userService.updateUser(userId, username, pass, email, role);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @GetMapping("/{username}/matches")
    public ResponseEntity<List<Match>> getMatchesByUserId(@PathVariable("username") String usernameId) {
        if (!userJmeker.getRole().equals(UserRole.REFEREE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        else {
            Long refereeId = Long.valueOf(usernameId);
            if (refereeId != null) {
                List<Match> matches = matchService.getRefereeMatches(refereeId);
                return ResponseEntity.ok(matches);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @GetMapping("/{username}/matches_scores")
    public ResponseEntity<List<Match>> getMatchesScoresByUserId(@PathVariable("username") String usernameId) {
        if (!userJmeker.getRole().equals(UserRole.REFEREE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        else {
            System.out.println("usernameId: " + usernameId);
            Long refereeId = userService.getUserIdByUsername(usernameId);
            if (refereeId != null) {
                List<Match> matches = matchService.getRefereeMatches(refereeId);
                return ResponseEntity.ok(matches);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @GetMapping("/referee_own_program.html")
    public String showRefereeOwnProgram(@RequestParam("username") String username, Model model) {
        if (!userJmeker.getRole().equals(UserRole.REFEREE) || !userJmeker.getUsername().equals(username)) {
            return "redirect:/api/users/login.html";
        }
        else {
            model.addAttribute("username", username);
            return "referee_own_program";
        }
    }

    @GetMapping("/create_tournament.html")
    public String showCreateTournament() {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return "redirect:/api/users/login.html";
        }
        else
            return "create_tournament";
    }

    @PostMapping("/create_tournament")
    public ResponseEntity<Tournament> createTournament(@RequestBody Map<String, String> formData) throws ParseException {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        else {
            String name = formData.get("name");
            String startDate = formData.get("startDate");
            String endDate = formData.get("endDate");
            String location = formData.get("location");

            // Parse dates if needed

            Tournament tournament = new Tournament();
            tournament.setName(name);
            tournament.setStartDate(convertStringToDate(startDate, "yyyy-MM-dd"));
            tournament.setEndDate(convertStringToDate(endDate, "yyyy-MM-dd"));
            tournament.setLocation(location);

            Tournament createdTournament = tournamentService.registerTournament(tournament);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTournament);
        }
    }

    public static Date convertStringToDate(String dateString, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(dateString);
    }

    @PostMapping("/update_score")
    public ResponseEntity<String> updateMatchScore(@RequestParam Long matchId, @RequestParam String newScore) {
        if (!userJmeker.getRole().equals(UserRole.REFEREE) || !matchService.isRefereeMatch(matchId, userJmeker.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        else {
            try {
                Match updatedMatch = matchService.updateMatchScore(matchId, newScore);
                if (updatedMatch != null) {
                    return ResponseEntity.ok("Score updated successfully");
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating score: " + e.getMessage());
            }
        }
    }

    @GetMapping("/{username}/scores")
    public ResponseEntity<List<Match>> getMatchesScoreByUserId(@PathVariable("username") String usernameId) {
        if (!userJmeker.getRole().equals(UserRole.TENNIS_PLAYER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        else {
            Long userId = Long.valueOf(usernameId);
            if (userId != null) {
                List<Match> matches = matchService.getUserMatches(userId);
                return ResponseEntity.ok(matches);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @GetMapping("/manage_match_score.html")
    public String showMatchesScore(@RequestParam("username") String username, Model model) {
        if (!userJmeker.getRole().equals(UserRole.REFEREE) || !userJmeker.getUsername().equals(username)) {
            return "redirect:/api/users/login.html";
        }
        else {
            model.addAttribute("username", username);
            return "manage_match_score";
        }
    }

    @GetMapping("/admin_export_match_info.html")
    public String showExportInfo(Model model) {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return "redirect:/api/users/login.html";
        }
        else
            return "admin_export_match_info";
    }

    @GetMapping("/match_score.html")
    public String showManageMatchesScore(@RequestParam("username") String username, Model model) {
        if (!userJmeker.getRole().equals(UserRole.TENNIS_PLAYER) || !userJmeker.getUsername().equals(username)) {
            return "redirect:/api/users/login.html";
        }
        model.addAttribute("username", username);
        return "match_score";
    }

    @GetMapping("/create_matches.html")
    public String showCreateMatches(Model model) {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return "redirect:/api/users/login.html";
        }
        else
            return "create_matches";
    }

    @GetMapping("/user-enrollments/all")
    public ResponseEntity<List<Map<String, Object>>> getApprovedUserEnrollmentsWithDetails() {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<UserEnrollment> userEnrollments = userEnrollmentService.getAllUserEnrollmentsWithDetails();
        List<Map<String, Object>> approvedEnrollmentDetails = new ArrayList<>();

        Iterator<UserEnrollment> iterator = userEnrollments.iterator();
        while (iterator.hasNext()) {
            UserEnrollment enrollment = iterator.next();
            if ("Approved".equals(enrollment.getStatus())) { // Only add "Approved" enrollments
                Map<String, Object> details = new HashMap<>();
                details.put("userId", enrollment.getUserId());
                details.put("username", userService.getUserById(enrollment.getUserId()).getUsername());
                details.put("tournamentId", enrollment.getTournamentId());
                details.put("location", tournamentService.getTournamentById(enrollment.getTournamentId()).getLocation());
                approvedEnrollmentDetails.add(details);
            }
        }

        return ResponseEntity.ok(approvedEnrollmentDetails); // Return only approved enrollments
    }

    //Filter pattern
    @GetMapping("/referees-all")
    public ResponseEntity<List<User>> getAllReferees() {
        if (!userJmeker.getRole().equals(UserRole.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        else {
            List<User> allUsers = userService.getAllUsers();

            List<User> referees = allUsers.stream()
                    .filter(user -> user.getRole().equals(REFEREE))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(referees);
        }
    }

    @GetMapping("/filterPlayers.html")
    public String showFilterPlayers() {
        return "filterPlayers"; // This should correspond to a view/template
    }

    @GetMapping("/filter-players")
    public ResponseEntity<List<User>> filterPlayers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String tournamentName) {

        List<User> filteredPlayers;

        System.out.println("tournamentName: " + tournamentName);

        if(!tournamentName.equals("")) {
            Long tournamentId = (long) tournamentService.getTournamentByTournamentName(tournamentName);
            filteredPlayers = userEnrollmentService.filterPlayersByTournament(tournamentId);
        } else {
            // If tournamentName is null, do not filter by tournament
            filteredPlayers = userService.filterPlayers(username, email);
        }

        return ResponseEntity.ok(filteredPlayers);
    }

}