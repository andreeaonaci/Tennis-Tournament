package model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_enrollment")
public class UserEnrollment {
    @Id
    @Column(name = "enrollment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tournament_id")
    private Long tournamentId;

    @Transient
    private String playerUsername;

    private String status;

    @Transient
    private String tournamentLocation;
    public UserEnrollment() {
    }

    public UserEnrollment(Long userId, Long tournamentId) {
        this.userId = userId;
        this.tournamentId = tournamentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getStatus() {
        return status;
    }

    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }
    public void setTournamentLocation(String tournamentLocation) {
        this.tournamentLocation = tournamentLocation;
    }

    public String getPlayerUsername() {
        return playerUsername;
    }

    public String getTournamentLocation() {
        return tournamentLocation;
    }

    public void setRegistrationId(Long registrationId) {
    }

    public Long getRegistrationId() {
        return id;
    }
}
