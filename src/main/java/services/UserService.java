package services;

import model.Encoder;
import model.User;
import model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User registerUser(User user) {
        try {
            System.out.println("Registering user: " + user.getUsername());
            return userRepository.save(user);
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            throw e;
        }
    }

    public boolean validateCredentials(String username, String password) {
        String pass = Encoder.encodingPassword(password);
        User user = userRepository.findByUsernameAndPassword(username, pass);
        return user != null;
    }
    public String loginUser(User user) {
        return "JWT_TOKEN";
    }

    public String getUserRole(String username) {
        String userRole = userRepository.getUserRoleByUsername(username);
        if (("ADMINISTRATOR".equals(userRole) || "REFEREE".equals(userRole) || "TENNIS_PLAYER".equals(userRole))) {
            return userRole;
        }
        else {
            return "UNKNOWN";
        }
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public User updateUser(Long userId, String username, String password, String email, UserRole role) {
        userRepository.updateUser(userId, username, password, email, role);
        return userRepository.findById(userId).orElse(null);
    }

    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.getId();
        } else {
            return null;
        }
    }

    public boolean isUserReferee(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.getRole() == UserRole.REFEREE;
    }

    private List<User> users;

    /**
     * Filters users to return only those with the TENNIS_PLAYER role,
     * and applies additional filtering criteria such as username and email.
     *
     * @param username The username to filter by, or null to ignore this filter.
     * @param email The email to filter by, or null to ignore this filter.
     * @return A list of filtered tennis players.
     */
    public List<User> filterPlayers(String username, String email) {
        // Get all tennis players
        List<User> tennisPlayers = getAllUsers().stream()
                .filter(user -> user.getRole() == UserRole.TENNIS_PLAYER)
                .collect(Collectors.toList());

        List<User> filteredPlayers = tennisPlayers;

        // Filter by username if it's not null
        if (username != null && !username.trim().isEmpty()) {
            filteredPlayers = filteredPlayers.stream()
                    .filter(user -> user.getUsername().equalsIgnoreCase(username))
                    .collect(Collectors.toList());
        }

        // Further filter by email if it's not null
        if (email != null && !email.trim().isEmpty()) {
            filteredPlayers = filteredPlayers.stream()
                    .filter(user -> user.getEmail().equalsIgnoreCase(email))
                    .collect(Collectors.toList());
        }

        return filteredPlayers;
    }

}