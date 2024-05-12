package repositories;

import model.UserEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEnrollmentRepository extends JpaRepository<UserEnrollment, Long> {
    List<UserEnrollment> findByTournamentId(Long tournamentId);
    boolean existsByTournamentIdAndUserId(Long tournamentId, Long userId);
    List<UserEnrollment> findByStatus(String status);
}
