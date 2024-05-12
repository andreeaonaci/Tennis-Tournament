package controller;

import model.*;
import services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserUtility userUtility;

    @Autowired
    private UserEnrollmentService userEnrollmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private JavaSmtpGmailSenderService emailService;

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllTournaments() {
        List<Map<String, Object>> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }
    @GetMapping("/tournament_registration.html")
    public String showTournamentRegistrationDashboard(@RequestParam("username") String username, Model model) {
        if (!UserController.getUserJmeker().getRole().equals(UserRole.TENNIS_PLAYER) || !UserController.getUserJmeker().getUsername().equals(username)) {
            System.out.println(username + " " + UserController.getUserJmeker().getRole() + " " + UserController.getUserJmeker().getUsername());
            return "redirect:/api/users/login.html";
        }
        model.addAttribute("username", username);
        List<Map<String, Object>> tournaments = tournamentService.getAllTournaments();
        model.addAttribute("tournaments", tournaments);
        return "tournament_registration";
    }

    @GetMapping("/{tournamentId}/matches")
    public ResponseEntity<List<Match>> getMatchesByTournamentId(@PathVariable Long tournamentId) {
        List<Match> matches = matchService.getMatchesByTournamentId(tournamentId);
        return ResponseEntity.ok(matches);
    }

    @PostMapping("/tournament_registration")
    public ResponseEntity<String> registerForTournament(
            @RequestParam("username") String username,
            @RequestBody UserEnrollment userEnrollment) {

        Long userId = userService.getUserIdByUsername(username);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userEnrollment.setUserId(userId);
        userEnrollment.setStatus("Pending");

        boolean isEnrolled = userEnrollmentService.isUserEnrolled(
                userEnrollment.getTournamentId(), userEnrollment.getUserId());

        if (isEnrolled) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already enrolled in the tournament");
        }

        // Register the user with pending status
        userEnrollmentService.addPendingEnrollment(userEnrollment);

        emailService.sendEmail(
                "andreeamariaonaci@gmail.com",
                "New Tournament Registration",
                "A new tournament registration is pending review.\nUsername: " + username + "\nTournament ID: " + userEnrollment.getTournamentId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body("Registration is pending approval");
    }

    @GetMapping("/admin_approvals.html")
    public String showAdminApprovalsDashboard(Model model) {
        if (!UserController.getUserJmeker().getRole().equals(UserRole.ADMINISTRATOR)) {
            return "redirect:/api/users/login.html";
        }
        List<UserEnrollment> pendingRegistrations = userEnrollmentService.getPendingRegistrations();
        model.addAttribute("pendingRegistrations", pendingRegistrations);
        return "admin_approvals";
    }

    @GetMapping("/pending-registrations")
    public ResponseEntity<List<UserEnrollment>> getPendingRegistrations() {
        List<UserEnrollment> pendingRegistrations = userEnrollmentService.getPendingRegistrations();
        return ResponseEntity.ok(pendingRegistrations);
    }

    @PostMapping("/approve_registration")
    public ResponseEntity<String> approveRegistration(
            @RequestBody Map<String, Object> requestBody) {

        try {
            Long registrationId = Long.parseLong(requestBody.get("registrationId").toString());
            boolean accept = Boolean.parseBoolean(requestBody.get("accept").toString());

            UserEnrollment userEnrollment = userEnrollmentService.findById(registrationId);

            if (userEnrollment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found");
            }

            if (accept) {
                userEnrollment.setStatus("Approved");
                userEnrollmentService.updateEnrollment(userEnrollment);

//                emailService.sendEmail(
//                        userEnrollment.getEmail(),
//                        "Tournament Registration Approved",
//                        "Your registration for the tournament has been approved."
//                );

                return ResponseEntity.ok("Registration approved");
            } else {
                userEnrollmentService.deleteEnrollment(registrationId); // Delete registration
//                emailService.sendEmail(
//                        userEnrollment.getEmail(),
//                        "Tournament Registration Denied",
//                        "Your registration for the tournament has been denied."
//                );

                return ResponseEntity.ok("Registration denied");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing registration approval/denial");
        }
    }
}
