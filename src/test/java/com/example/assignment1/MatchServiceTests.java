package com.example.assignment1;

import model.Match;
import model.Tournament;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositories.MatchRepository;
import services.MatchService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MatchServiceTests {

    @InjectMocks
    private MatchService matchService;

    @Mock
    private MatchRepository matchRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateMatch() {
        // Given
        User player1 = new User();
        player1.setId(1L);

        User player2 = new User();
        player2.setId(2L);

        User referee = new User();
        referee.setId(3L);

        Tournament tournament = new Tournament();
        tournament.setId(1L);

        Match match = new Match();
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setReferee(referee);
        match.setTournament(tournament);

        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // When
        Match createdMatch = matchService.createMatch(match);

        // Then
        assertNotNull(createdMatch);
        assertEquals(player1, createdMatch.getPlayer1());
        assertEquals(player2, createdMatch.getPlayer2());
        assertEquals(referee, createdMatch.getReferee());
        assertEquals(tournament, createdMatch.getTournament());
        verify(matchRepository, times(1)).save(match);
    }

    @Test
    public void testGetAllMatches() {
        // Given
        User player1 = new User();
        player1.setId(1L);

        User player2 = new User();
        player2.setId(2L);

        User referee = new User();
        referee.setId(3L);

        Tournament tournament = new Tournament();
        tournament.setId(1L);

        Match match1 = new Match();
        match1.setPlayer1(player1);
        match1.setPlayer2(player2);
        match1.setReferee(referee);
        match1.setTournament(tournament);

        Match match2 = new Match();
        match2.setPlayer1(player1);
        match2.setPlayer2(player2);
        match2.setReferee(referee);
        match2.setTournament(tournament);

        List<Match> matches = Arrays.asList(match1, match2);

        when(matchRepository.findAll()).thenReturn(matches);

        // When
        List<Match> retrievedMatches = matchService.getAllMatches();

        // Then
        assertEquals(2, retrievedMatches.size());
        assertTrue(retrievedMatches.contains(match1));
        assertTrue(retrievedMatches.contains(match2));
        verify(matchRepository, times(1)).findAll();
    }

    @Test
    public void testGetMatchesByTournamentId() {
        // Given
        Long tournamentId = 1L;

        User player1 = new User();
        player1.setId(1L);

        User player2 = new User();
        player2.setId(2L);

        User referee = new User();
        referee.setId(3L);

        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);

        Match match1 = new Match();
        match1.setPlayer1(player1);
        match1.setPlayer2(player2);
        match1.setReferee(referee);
        match1.setTournament(tournament);

        Match match2 = new Match();
        match2.setPlayer1(player1);
        match2.setPlayer2(player2);
        match2.setReferee(referee);
        match2.setTournament(tournament);

        List<Match> matches = Arrays.asList(match1, match2);

        when(matchRepository.findByTournamentId(tournamentId)).thenReturn(matches);

        // When
        List<Match> retrievedMatches = matchService.getMatchesByTournamentId(tournamentId);

        // Then
        assertNotNull(retrievedMatches);
        assertEquals(2, retrievedMatches.size());
        assertEquals(matches, retrievedMatches);
        verify(matchRepository, times(1)).findByTournamentId(tournamentId);
    }
}
