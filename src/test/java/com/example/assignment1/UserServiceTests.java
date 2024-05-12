package com.example.assignment1;

import model.Encoder;
import model.User;
import model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositories.UserRepository;
import services.UserService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); // Initialize all mocks
    }

    @Test
    public void testRegisterUser() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User registeredUser = userService.registerUser(user);

        // Then
        assertNotNull(registeredUser);
        assertEquals(1L, registeredUser.getId());
        assertEquals("testuser", registeredUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetAllUsers() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> retrievedUsers = userService.getAllUsers();

        // Then
        assertEquals(2, retrievedUsers.size());
        assertTrue(retrievedUsers.contains(user1));
        assertTrue(retrievedUsers.contains(user2));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUserById() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        User retrievedUser = userService.getUserById(userId);

        // Then
        assertNotNull(retrievedUser);
        assertEquals(userId, retrievedUser.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserByUsername() {
        // Given
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(user);

        // When
        User retrievedUser = userService.getUserByUsername(username);

        // Then
        assertNotNull(retrievedUser);
        assertEquals(username, retrievedUser.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testValidateCredentials() {
        // Given
        String username = "testuser";
        String password = "password";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsernameAndPassword(username, Encoder.encodingPassword(password))).thenReturn(user);

        // When
        boolean isValid = userService.validateCredentials(username, password);

        // Then
        assertTrue(isValid);
        verify(userRepository, times(1)).findByUsernameAndPassword(username, Encoder.encodingPassword(password));
    }

    @Test
    public void testGetUserRole() {
        // Given
        String username = "admin";
        when(userRepository.getUserRoleByUsername(username)).thenReturn("ADMINISTRATOR");

        // When
        String userRole = userService.getUserRole(username);

        // Then
        assertEquals("ADMINISTRATOR", userRole);
        verify(userRepository, times(1)).getUserRoleByUsername(username);
    }

    @Test
    public void testDeleteUserById() {
        // Given
        Long userId = 1L;

        // When
        userService.deleteUserById(userId);

        // Then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testFilterPlayers() {
        // Given
        User player1 = new User();
        player1.setUsername("player1");
        player1.setRole(UserRole.TENNIS_PLAYER);

        User player2 = new User();
        player2.setUsername("player2");
        player2.setRole(UserRole.TENNIS_PLAYER);

        List<User> users = Arrays.asList(player1, player2);

        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> filteredPlayers = userService.filterPlayers("player1", null);

        // Then
        assertEquals(1, filteredPlayers.size());
        assertEquals("player1", filteredPlayers.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }
}
