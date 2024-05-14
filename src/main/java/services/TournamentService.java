package services;

import model.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.TournamentRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TournamentService {

    @Autowired
    TournamentRepository tournamentRepository;

    private static final Logger logger = LoggerFactory.getLogger(TournamentService.class);

    public Tournament registerTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }
    public List<Map<String, Object>> getAllTournaments() {
        List<Tournament> tournaments = tournamentRepository.findAll();
        List<Map<String, Object>> simplifiedTournaments = new ArrayList<>();

        for (Tournament tournament : tournaments) {
            Map<String, Object> simplifiedTournament = new HashMap<>();
            simplifiedTournament.put("tourId", tournament.getTourId());
            simplifiedTournament.put("location", tournament.getLocation());
            simplifiedTournaments.add(simplifiedTournament);
        }

        return simplifiedTournaments;
    }

    public Tournament getTournamentById(Long tournamentId) {
        return tournamentRepository.findById(tournamentId).orElse(null);
    }

    public int getTournamentByTournamentName(String tournamentName) {
        return Math.toIntExact(tournamentRepository.findByLocation(tournamentName).getTourId());
    }

}
