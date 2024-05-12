package services;

import model.ExportRequest;
import model.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.MatchRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    public Match createMatch(Match match) {
        return matchRepository.save(match);
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public List<Match> getMatchesByTournamentId(Long tournamentId) {
        List<Match> matches = matchRepository.findByTournamentId(tournamentId);
        return matches;
    }

    public List<Match> getRefereeMatches(Long refereeId) {
        return matchRepository.findRefereeMatches(refereeId);
    }

    public Match updateMatchScore(Long matchId, String newScore) {
        Optional<Match> optionalMatch = matchRepository.findById(matchId);
        if (optionalMatch.isPresent()) {
            Match match = optionalMatch.get();
            match.setScore(newScore);
            return matchRepository.save(match);
        } else {
            return null;
        }
    }

    public List<Match> getUserMatches(Long userId) {
        return matchRepository.findUserMatches(userId);
    }

    public List<Match> getFilteredMatches(ExportRequest exportRequest) {
        List<Match> allMatches = matchRepository.findAll();
        List<Match> filteredMatches = new ArrayList<>();

        for (Match match : allMatches) {
            StringBuilder selectedValues = new StringBuilder();
            if (match.getTournament() != null && match.getPlayer1() != null) {
                assert exportRequest != null;
                List<String> selectedColumns = exportRequest.getColumnsToExport();
                if (selectedColumns != null) {
                    for (String column : selectedColumns) {
                        switch (column) {
                            case "tournament":
                                if (match.getTournament() != null) {
                                    selectedValues.append(match.getTournament().getLocation()).append(",");
                                }
                                break;
                            case "player1":
                                if (match.getPlayer1() != null) {
                                    selectedValues.append(match.getPlayer1().getUsername()).append(",");
                                }
                                break;
                            case "player2":
                                if (match.getPlayer2() != null) {
                                    selectedValues.append(match.getPlayer2().getUsername()).append(",");
                                }
                                break;
                            case "referee":
                                if (match.getReferee() != null) {
                                    selectedValues.append(match.getReferee().getUsername()).append(",");
                                }
                                break;
                            case "matchDate":
                                if (match.getMatchDate() != null) {
                                    selectedValues.append(match.getFormattedMatchDate()).append(",");
                                }
                                break;
                            case "courtName":
                                if (match.getCourtName() != null) {
                                    selectedValues.append(match.getCourtName()).append(",");
                                }
                                break;
                            case "score":
                                if (match.getScore() != null) {
                                    selectedValues.append(match.getScore()).append(",");
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            if (selectedValues.length() > 0) {
                selectedValues.deleteCharAt(selectedValues.length() - 1);
                filteredMatches.add(match);
            }
        }

        return filteredMatches;
    }
    public boolean isRefereeMatch(Long matchId, Long refereeId) {
        Optional<Match> optionalMatch = matchRepository.findById(matchId);
        System.out.println("MatchService: isRefereeMatch: " + optionalMatch.get().getReferee().getUsername());
        return optionalMatch.get().getReferee().getUserId().equals(refereeId);
    }
}