package model;

import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long matchId;

    @ManyToOne
    @JoinColumn(name = "tournament_id", referencedColumnName = "tour_id")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "player1_id", referencedColumnName = "user_id", nullable = false)
    private User player1;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public User getReferee() {
        return referee;
    }

    public void setReferee(User referee) {
        this.referee = referee;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    @ManyToOne
    @JoinColumn(name = "player2_id", referencedColumnName = "user_id", nullable = false)
    private User player2;

    @ManyToOne
    @JoinColumn(name = "referee_id", referencedColumnName = "user_id", nullable = false)
    private User referee;

    public String getCourtName() {
        return courtName;
    }

    @Column(name = "match_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date matchDate;

    public String getScore() {
        return score;
    }

    @Column(name = "score", length = 50)
    private String score;

    public void setScore(String score) {
        this.score = score;
    }

    @Column(name = "court", nullable = false)
    private String courtName;

    public String getFormattedMatchDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mma");
        return sdf.format(matchDate);
    }
}
