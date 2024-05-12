package controller;

import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import services.MatchService;
import services.TournamentService;
import services.UserEnrollmentService;
import services.UserService;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private UserEnrollmentService userEnrollmentService;

    @PostMapping("/create")
    public ResponseEntity<?> createMatch(@RequestBody Map<String, Object> requestData) {
        if (!UserController.getUserJmeker().getRole().equals(UserRole.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You are not authorized to create a match.");
        }
        try {
            System.out.println("Request Data: " + requestData);
            String player1Id = requestData.get("player1Id").toString();
            String player2Id = requestData.get("player2Id").toString();
            String refereeId = requestData.get("refereeId").toString();
            String tournamentId = requestData.get("tournamentId").toString();

            Long player1IdLong = Long.parseLong(player1Id);
            Long player2IdLong = Long.parseLong(player2Id);
            Long refereeIdLong = Long.parseLong(refereeId);
            Long tournamentIdLong = Long.parseLong(tournamentId);
            String matchDateStr = (String) requestData.get("matchDate");
            String score = (String) requestData.get("score");
            String court = (String) requestData.get("court");

            if (!userEnrollmentService.isUserEnrolled(tournamentIdLong, player1IdLong) ||
                    !userEnrollmentService.isUserEnrolled(tournamentIdLong, player2IdLong)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("One or more players are not enrolled in the tournament.");
            }

            if (!userService.isUserReferee(refereeIdLong)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid referee ID.");
            }

            Date matchDate;
            try {
                matchDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(matchDateStr);
            } catch (ParseException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid match date format.");
            }

            User player1 = userService.getUserById(player1IdLong);
            User player2 = userService.getUserById(player2IdLong);
            User referee = userService.getUserById(refereeIdLong);
            Tournament tournament = tournamentService.getTournamentById(tournamentIdLong);

            Match match = new Match();
            match.setPlayer1(player1);
            match.setPlayer2(player2);
            match.setReferee(referee);
            match.setTournament(tournament);
            match.setMatchDate(matchDate);
            match.setScore(score);
            match.setCourtName(court);

            Match createdMatch = matchService.createMatch(match);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMatch);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


    @GetMapping("/schedule.html")
    public String showScheduleDashboard(Model model) {
        if (!UserController.getUserJmeker().getRole().equals(UserRole.TENNIS_PLAYER)) {
            return "redirect:/api/users/login.html";
        }
        List<Map<String, Object>> tournaments = tournamentService.getAllTournaments();
        model.addAttribute("matches", tournaments);
        return "schedule";
    }

    @GetMapping("/{tournamentId}/matches")
    public ResponseEntity<List<Match>> getMatchesByTournamentId(@PathVariable("tournamentId") Long tournamentId) {
        List<Match> matches = matchService.getMatchesByTournamentId(tournamentId);
        return ResponseEntity.ok(matches);
    }

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        List<Match> matches = matchService.getAllMatches();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/{username}/matches")
    public ResponseEntity<List<Match>> getMatchesByRefereeUsername(@PathVariable String username) {
        if (!UserController.getUserJmeker().getRole().equals(UserRole.REFEREE) || !UserController.getUserJmeker().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        else {
            User referee = userService.getUserByUsername(username);
            if (referee != null && referee.getRole().equals(UserRole.REFEREE)) {
                Long refereeId = referee.getUserId();
                List<Match> matches = matchService.getRefereeMatches(refereeId);

                System.out.println("Matches for referee " + username + ":");
                for (Match match : matches) {
                    System.out.println("Match ID: " + match.getMatchId() + ", Date: " + match.getFormattedMatchDate() + ", Score: " + match.getScore());
                }

                return ResponseEntity.ok(matches);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @GetMapping("/{username}/scores")
    public ResponseEntity<List<Match>> getMatchesByUserUsername(@PathVariable String username) {
        if (!UserController.getUserJmeker().getRole().equals(UserRole.TENNIS_PLAYER) || !UserController.getUserJmeker().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        else {
            User user = userService.getUserByUsername(username);
            if (user != null && user.getRole().equals(UserRole.TENNIS_PLAYER)) {
                Long userId = user.getUserId();
                List<Match> matches = matchService.getUserMatches(userId);
                return ResponseEntity.ok(matches);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @PostMapping("/update_scores") // Add this endpoint
    public ResponseEntity<String> updateMatchScores(@RequestBody List<Match> matches) {
        if (!UserController.getUserJmeker().getRole().equals(UserRole.REFEREE) || !UserController.getUserJmeker().getUsername().equals(matches.get(0).getReferee().getUsername())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You are not authorized to update match scores.");
        }
        try {
            for (Match match : matches) {
                matchService.updateMatchScore(match.getMatchId(), match.getScore());
            }
            return ResponseEntity.ok("Scores updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating scores: " + e.getMessage());
        }
    }

    @GetMapping("/{username}/matches_scores")
    public ResponseEntity<List<Match>> getMatchesScoresByUsername(@PathVariable String username) {
        if (!UserController.getUserJmeker().getRole().equals(UserRole.REFEREE) || !UserController.getUserJmeker().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        User referee = userService.getUserByUsername(username);
        if (referee != null && referee.getRole().equals(UserRole.REFEREE)) {
            Long refereeId = referee.getUserId();
            List<Match> matches = matchService.getRefereeMatches(refereeId);
            return ResponseEntity.ok(matches);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/info/export")
    @ResponseBody
    public ResponseEntity<String> exportMatchInformation(@RequestBody ExportRequest exportRequest) {
        if (!UserController.getUserJmeker().getRole().equals(UserRole.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You are not authorized to export match information.");
        }
        try {
            if (exportRequest != null && (exportRequest.isCsvExport() || exportRequest.isTxtExport())) {
                List<Match> matches = matchService.getFilteredMatches(exportRequest);

                if (exportRequest.isCsvExport()) {
                    exportToCsv(matches, exportRequest.getColumnsToExport());
                }

                if (exportRequest.isTxtExport()) {
                    exportToTxt(matches, exportRequest.getColumnsToExport());
                }

                return ResponseEntity.ok("Match information exported successfully");
            } else {
                return ResponseEntity.badRequest().body("No valid export options selected");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error exporting match information: " + e.getMessage());
        }
    }


    private void exportToCsv(List<Match> matches, List<String> selectedColumns) throws IOException {
        FileWriter csvWriter = new FileWriter("matches.csv");
        StringBuilder header = new StringBuilder();
        for (String column : selectedColumns) {
            header.append(column).append(",");
        }
        header.deleteCharAt(header.length() - 1);
        header.append("\n");
        csvWriter.append(header.toString());
        for (Match match : matches) {
            StringBuilder selectedValues = new StringBuilder();
            if (match != null) {
                for (String column : selectedColumns) {
                    switch (column) {
                        case "matchId":
                            selectedValues.append(match.getMatchId());
                            break;
                        case "tournament":
                            if (match.getTournament() != null) {
                                selectedValues.append(match.getTournament().getLocation());
                            }
                            break;
                        case "player1":
                            if (match.getPlayer1() != null) {
                                selectedValues.append(match.getPlayer1().getUsername());
                            }
                            break;
                        case "player2":
                            if (match.getPlayer2() != null) {
                                selectedValues.append(match.getPlayer2().getUsername());
                            }
                            break;
                        case "referee":
                            if (match.getReferee() != null) {
                                selectedValues.append(match.getReferee().getUsername());
                            }
                            break;
                        case "matchDate":
                            selectedValues.append(match.getFormattedMatchDate());
                            break;
                        case "courtName":
                            selectedValues.append(match.getCourtName());
                            break;
                        case "score":
                            selectedValues.append(match.getScore());
                            break;
                        default:
                            break;
                    }
                    selectedValues.append(",");
                }
                selectedValues.deleteCharAt(selectedValues.length() - 1);
                selectedValues.append("\n");
                csvWriter.append(selectedValues.toString());
            }
        }
        csvWriter.flush();
        csvWriter.close();
    }


    private void exportToTxt(List<Match> matches, List<String> selectedColumns) throws IOException {
        FileWriter txtWriter = new FileWriter("matches.txt");
        StringBuilder header = new StringBuilder();
        for (String column : selectedColumns) {
            header.append(column).append(": ");
        }
        header.append("\n");
        txtWriter.append(header.toString());
        for (Match match : matches) {
            StringBuilder selectedValues = new StringBuilder();
            if (match != null) {
                for (String column : selectedColumns) {
                    switch (column) {
                        case "matchId":
                            selectedValues.append("Match ID: ").append(match.getMatchId()).append("\n");
                            break;
                        case "tournament":
                            if (match.getTournament() != null) {
                                selectedValues.append("Tournament: ").append(match.getTournament().getLocation()).append("\n");
                            }
                            break;
                        case "player1":
                            if (match.getPlayer1() != null) {
                                selectedValues.append("Player 1: ").append(match.getPlayer1().getUsername()).append("\n");
                            }
                            break;
                        case "player2":
                            if (match.getPlayer2() != null) {
                                selectedValues.append("Player 2: ").append(match.getPlayer2().getUsername()).append("\n");
                            }
                            break;
                        case "referee":
                            if (match.getReferee() != null) {
                                selectedValues.append("Referee: ").append(match.getReferee().getUsername()).append("\n");
                            }
                            break;
                        case "matchDate":
                            selectedValues.append("Match Date: ").append(match.getFormattedMatchDate()).append("\n");
                            break;
                        case "courtName":
                            selectedValues.append("Court: ").append(match.getCourtName()).append("\n");
                            break;
                        case "score":
                            selectedValues.append("Score: ").append(match.getScore()).append("\n");
                            break;
                        default:
                            break;
                    }
                }
            }
            txtWriter.append(selectedValues.toString()).append("\n");
        }
        txtWriter.flush();
        txtWriter.close();
    }
}
