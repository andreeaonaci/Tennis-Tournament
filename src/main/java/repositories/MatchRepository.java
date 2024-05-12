package repositories;

import model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE m.tournament.tourId = :tournamentId")
    List<Match> findByTournamentId(@Param("tournamentId") Long tournamentId);
    @Query("SELECT m FROM Match m WHERE m.referee.userId = :refereeId")
    List<Match> findRefereeMatches(@Param("refereeId") Long refereeId);

    @Query("SELECT m FROM Match m WHERE m.player1.userId = :userId OR m.player2.userId = :userId")
    List<Match> findUserMatches(@Param("userId") Long userId);
}

