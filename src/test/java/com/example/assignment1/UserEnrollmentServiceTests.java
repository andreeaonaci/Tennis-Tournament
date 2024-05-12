package com.example.assignment1;

import model.Tournament;
import model.User;
import model.UserEnrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositories.TournamentRepository;
import repositories.UserEnrollmentRepository;
import repositories.UserRepository;
import services.TournamentService;
import services.UserEnrollmentService;
import services.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserEnrollmentServiceTests {

    @InjectMocks
    private UserEnrollmentService userEnrollmentService;

    @Mock
    private UserEnrollmentRepository userEnrollmentRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); // Initialize all mocks
    }

    @Test
    public void testIsUserEnrolled() {
        // Given
        Long tournamentId = 1L;
        Long userId = 2L;

        when(userEnrollmentRepository.existsByTournamentIdAndUserId(tournamentId, userId)).thenReturn(true);

        // When
        boolean isEnrolled = userEnrollmentService.isUserEnrolled(tournamentId, userId);

        // Then
        assertTrue(isEnrolled);
        verify(userEnrollmentRepository, times(1)).existsByTournamentIdAndUserId(tournamentId, userId);
    }

    @Test
    public void testRegisterUserForTournament() {
        // Given
        UserEnrollment userEnrollment = new UserEnrollment();
        userEnrollment.setUserId(2L);
        userEnrollment.setTournamentId(1L);

        when(userEnrollmentRepository.save(any(UserEnrollment.class))).thenReturn(userEnrollment);

        // When
        userEnrollmentService.registerUserForTournament(userEnrollment);

        // Then
        verify(userEnrollmentRepository, times(1)).save(userEnrollment);
    }

    @Test
    public void testAddPendingEnrollment() {
        // Given
        UserEnrollment userEnrollment = new UserEnrollment();
        userEnrollment.setStatus("Pending");

        when(userEnrollmentRepository.save(any(UserEnrollment.class))).thenReturn(userEnrollment);

        // When
        userEnrollmentService.addPendingEnrollment(userEnrollment);

        // Then
        verify(userEnrollmentRepository, times(1)).save(userEnrollment);
    }

    @Test
    public void testGetPendingRegistrations() {
        // Given
        UserEnrollment userEnrollment = new UserEnrollment();
        userEnrollment.setStatus("Pending");

        List<UserEnrollment> pendingEnrollments = Arrays.asList(userEnrollment);

        when(userEnrollmentRepository.findByStatus("Pending")).thenReturn(pendingEnrollments);

        // When
        List<UserEnrollment> retrievedPending = userEnrollmentService.getPendingRegistrations();

        // Then
        assertEquals(1, retrievedPending.size());
        assertEquals("Pending", retrievedPending.get(0).getStatus());
        verify(userEnrollmentRepository, times(1)).findByStatus("Pending");
    }

    @Test
    public void testUpdateEnrollment() {
        // Given
        UserEnrollment userEnrollment = new UserEnrollment();
        userEnrollment.setStatus("Pending");

        when(userEnrollmentRepository.save(any(UserEnrollment.class))).thenReturn(userEnrollment);

        // When
        userEnrollmentService.updateEnrollment(userEnrollment);

        // Then
        verify(userEnrollmentRepository, times(1)).save(userEnrollment);
    }

    @Test
    public void testDeleteEnrollment() {
        // Given
        Long registrationId = 1L;

        // When
        userEnrollmentService.deleteEnrollment(registrationId);

        // Then
        verify(userEnrollmentRepository, times(1)).deleteById(registrationId);
    }

    @Test
    public void testFindById() {
        // Given
        Long registrationId = 1L;
        UserEnrollment userEnrollment = new UserEnrollment();
        userEnrollment.setId(registrationId);

        when(userEnrollmentRepository.findById(registrationId)).thenReturn(Optional.of(userEnrollment));

        // When
        UserEnrollment retrievedEnrollment = userEnrollmentService.findById(registrationId);

        // Then
        assertNotNull(retrievedEnrollment);
        assertEquals(registrationId, retrievedEnrollment.getId());
        verify(userEnrollmentRepository, times(1)).findById(registrationId);
    }
}
