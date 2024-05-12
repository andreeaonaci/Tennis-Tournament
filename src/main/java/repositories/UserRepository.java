package repositories;

import model.User;
import model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    @Query("SELECT u.role FROM User u WHERE u.username = :username")
    String getUserRoleByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.username = ?1 AND u.password = ?2")
    User findByUsernameAndPassword(String username, String password);
    User save(User user);
    @Transactional
    @Modifying
    @Query("UPDATE User u SET " +
            "u.username = CASE WHEN :username IS NOT NULL THEN :username ELSE u.username END, " +
            "u.password = CASE WHEN :password IS NOT NULL THEN :password ELSE u.password END, " +
            "u.email = CASE WHEN :email IS NOT NULL THEN :email ELSE u.email END, " +
            "u.role = CASE WHEN :role IS NOT NULL THEN :role ELSE u.role END " +
            "WHERE u.userId = :userId")
    void updateUser(@Param("userId") Long userId,
                    @Param("username") String username,
                    @Param("password") String password,
                    @Param("email") String email,
                    @Param("role") UserRole role);


    void deleteById(Long userId);
}
