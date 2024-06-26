package services;

import model.Tournament;
import model.User;
import model.UserEnrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.UserEnrollmentRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserEnrollmentService {

    private final UserEnrollmentRepository userEnrollmentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    public UserEnrollmentService(UserEnrollmentRepository userEnrollmentRepository) {
        this.userEnrollmentRepository = userEnrollmentRepository;
    }

    public boolean isUserEnrolled(Long tournamentId, Long userId) {
        return userEnrollmentRepository.existsByTournamentIdAndUserId(tournamentId, userId);
    }

    public void registerUserForTournament(UserEnrollment userEnrollment) {
        userEnrollmentRepository.save(userEnrollment);
    }
    public List<UserEnrollment> getAllUserEnrollmentsWithDetails() {
        List<UserEnrollment> userEnrollments = userEnrollmentRepository.findAll();
        for (UserEnrollment enrollment : userEnrollments) {
            User user = userService.getUserById(enrollment.getUserId());
            Tournament tournament = tournamentService.getTournamentById(enrollment.getTournamentId());
            enrollment.setPlayerUsername(user.getUsername());
            enrollment.setTournamentLocation(tournament.getLocation());
        }
        return userEnrollments;
    }

    public void addPendingEnrollment(UserEnrollment userEnrollment) {
        userEnrollment.setStatus("Pending");
        userEnrollmentRepository.save(userEnrollment);
    }

    public List<UserEnrollment> getPendingRegistrations() {
        return userEnrollmentRepository.findByStatus("Pending"); // Get all pending registrations
    }

    public void updateEnrollment(UserEnrollment userEnrollment) {
        userEnrollmentRepository.save(userEnrollment); // Save changes
    }

    public void deleteEnrollment(Long registrationId) {
        userEnrollmentRepository.deleteById(registrationId); // Delete registration
    }

    public UserEnrollment findById(Long registrationId) {
        return userEnrollmentRepository.findById(registrationId).orElse(null);
    }

    /**
     * Filters users to return only those enrolled in a specific tournament.
     *
     * @param tournamentId The ID of the tournament to filter by.
     * @return A list of users enrolled in the specified tournament.
     */
    public List<User> filterPlayersByTournament(Long tournamentId) {
        // Get all users
        List<User> allUsers = userService.getAllUsers();

        if (tournamentId == null) {
            return Collections.emptyList();
        }

        // Filter users by tournament enrollment using UserEnrollmentService
        return allUsers.stream()
                .filter(user -> isUserEnrolled(tournamentId, user.getId()))
                .collect(Collectors.toList());
    }

}
