package com.example.assignment1;

import model.Tournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositories.TournamentRepository;
import services.TournamentService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TournamentServiceTests {

    @InjectMocks
    private TournamentService tournamentService;

    @Mock
    private TournamentRepository tournamentRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterTournament() {
        // Given
        Tournament tournament = new Tournament();
        tournament.setTourId(1L);
        tournament.setLocation("New York");

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // When
        Tournament registeredTournament = tournamentService.registerTournament(tournament);

        // Then
        assertNotNull(registeredTournament);
        assertEquals(1L, registeredTournament.getTourId());
        assertEquals("New York", registeredTournament.getLocation());
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    public void testGetAllTournaments() {
        // Given
        Tournament tournament1 = new Tournament();
        tournament1.setTourId(1L);
        tournament1.setLocation("New York");

        Tournament tournament2 = new Tournament();
        tournament2.setTourId(2L);
        tournament2.setLocation("London");

        List<Tournament> tournaments = Arrays.asList(tournament1, tournament2);

        when(tournamentRepository.findAll()).thenReturn(tournaments);

        // When
        List<Map<String, Object>> simplifiedTournaments = tournamentService.getAllTournaments();

        // Then
        assertEquals(2, simplifiedTournaments.size());
        assertTrue(simplifiedTournaments.stream().anyMatch(t -> t.get("tourId").equals(1L)));
        assertTrue(simplifiedTournaments.stream().anyMatch(t -> t.get("tourId").equals(2L)));
        assertTrue(simplifiedTournaments.stream().anyMatch(t -> t.get("location").equals("New York")));
        assertTrue(simplifiedTournaments.stream().anyMatch(t -> t.get("location").equals("London")));
        verify(tournamentRepository, times(1)).findAll();
    }

    @Test
    public void testGetTournamentById() {
        // Given
        Tournament tournament = new Tournament();
        tournament.setTourId(1L);
        tournament.setLocation("New York");

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        // When
        Tournament retrievedTournament = tournamentService.getTournamentById(1L);

        // Then
        assertNotNull(retrievedTournament);
        assertEquals(1L, retrievedTournament.getTourId());
        assertEquals("New York", retrievedTournament.getLocation());
        verify(tournamentRepository, times(1)).findById(1L);
    }
}
