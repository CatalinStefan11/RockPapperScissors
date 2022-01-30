package com.rockpaperscissors.service;

import com.rockpaperscissors.exception.customexceptions.GameException;
import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.exception.customexceptions.NotFoundException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.entities.Round;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.model.gameplay.Invite;
import com.rockpaperscissors.model.gameplay.Move;
import com.rockpaperscissors.repository.GameSessionRepository;
import com.rockpaperscissors.repository.RoundRepository;
import com.rockpaperscissors.utils.RoundEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameSessionServiceTest {

    GameSessionService sessionService;

    @Mock
    GameSessionRepository sessionRepository;

    @Mock
    RoundRepository roundRepository;

    @Mock
    RoundEvaluator roundEvaluator;

    @BeforeEach
    void init() {
        sessionService = new GameSessionService(sessionRepository, roundRepository, roundEvaluator);
    }

    @Test
    void testRetrieveSessionAndSetStatePlaying_throwsException(){

        Invite invite = new Invite(new Player("Catalin"));
        GameSession session = new GameSession(invite);

        Mockito.lenient().when(sessionRepository.findBySessionCode(any(String.class)))
                .thenReturn(Optional.of(session));

        Assertions.assertThrows(GameException.class, () -> {
            sessionService.retrieveSessionAndSetStatePlaying(invite.getInviteCode());
        });

        verify(sessionRepository, times(1)).findBySessionCode(any());
    }

    @Test
    void testRetrieveSessionAndSetStatePlaying_RunsWithoutException(){

        Invite invite = new Invite(new Player("Catalin"));
        GameSession session = new GameSession(invite);
        session.setGameState(GameSession.State.ACCEPTED);

        Mockito.lenient().when(sessionRepository.findBySessionCode(any(String.class)))
                .thenReturn(Optional.of(session));

        GameSession returned = sessionService.retrieveSessionAndSetStatePlaying(invite.getInviteCode());

        verify(sessionRepository, times(1)).findBySessionCode(any());

        Assertions.assertEquals(GameSession.State.PLAYING, returned.getGameState());
    }

    @Test
    void testPushMoveAndUpdateRound_ThrowException() {

        Turn samePlayerTurn = new Turn(new Player("Catalin"), Move.ROCK);
        Round round = new Round(samePlayerTurn);

        Assertions.assertThrows(InvalidOperationException.class, () -> {
            sessionService.pushMoveAndUpdateRound(round, samePlayerTurn);
        });
    }

    @Test
    void testPushMoveAndUpdateRound_RunsWithoutException() {

        Round round = new Round(new Turn(new Player("Catalin"), Move.ROCK));
        Turn second = new Turn(new Player("Ronaldo"), Move.PAPER);

        sessionService.pushMoveAndUpdateRound(round, second);

        verify(roundRepository, times(1)).saveAndFlush(any());
        verify(roundEvaluator, times(1)).evaluate(any());

        Assertions.assertEquals(Round.GameState.OVER, round.getState());
    }

    @Test
    void testCreateNewRound() {

        Player player = new Player("Catalin");
        Turn turn = new Turn(player, Move.ROCK);
        Invite invite = new Invite(player);
        GameSession gameSession = new GameSession(invite);

        sessionService.createNewRound(turn, gameSession);

        verify(sessionRepository, times(1)).saveAndFlush(any());
        verify(roundRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void testAcceptInvite_throwNotFound() {

        Player player = new Player("Catalin");
        Invite invite = new Invite(player);

        Mockito.lenient().when(sessionRepository.findBySessionCode(any(String.class)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            sessionService.acceptInvite(player, invite.getInviteCode());
        });

        verify(sessionRepository, times(1)).findBySessionCode(invite.getInviteCode());

    }

    @Test
    void testAcceptInvite_throwInvalidOperation_whenPlayerTriesToAcceptOwnInvite() {

        Player player = new Player("Catalin");
        Invite invite = new Invite(player);
        GameSession gameSession = new GameSession(invite);

        Mockito.lenient().when(sessionRepository.findBySessionCode(any(String.class)))
                .thenReturn(Optional.of(gameSession));

        Assertions.assertThrows(InvalidOperationException.class, () -> {
            sessionService.acceptInvite(player, invite.getInviteCode());
        });

        verify(sessionRepository, times(1)).findBySessionCode(invite.getInviteCode());

    }

    @Test
    void testAcceptInvite_ReturnValidOutput() {

        Player player = new Player("Catalin");
        Invite invite = new Invite(new Player("Messi"));
        GameSession gameSession = new GameSession(invite);

        Mockito.lenient().when(sessionRepository.findBySessionCode(any(String.class)))
                .thenReturn(Optional.of(gameSession));


        GameSession returned = sessionService.acceptInvite(player, invite.getInviteCode());

        verify(sessionRepository, times(1)).findBySessionCode(invite.getInviteCode());
        verify(sessionRepository, times(1)).saveAndFlush(any());
        Assertions.assertEquals(GameSession.State.ACCEPTED, returned.getGameState());

    }

    @Test
    void testUpdateSessionAndLatestRound_UpdateRoundsAndSessionIdDb() {

        Invite invite = new Invite(new Player("Catalin"));
        GameSession gameSession = new GameSession(invite);
        gameSession.addRound(new Round());

        sessionService.updateSessionAndLatestRound(gameSession);

        verify(sessionRepository, times(1)).saveAndFlush(any());
        verify(roundRepository, times(1)).saveAndFlush(any());

    }

    @Test
    void testCreateSessionFromInvite() {

        Invite invite = new Invite(new Player("Catalin"));
        GameSession gameSession = new GameSession(invite);

        Mockito.lenient().when(sessionRepository.saveAndFlush(any(GameSession.class)))
                .thenReturn(gameSession);

        GameSession returned = sessionService.createSessionFromInvite(invite);

        verify(sessionRepository, times(1)).saveAndFlush(any());
        Assertions.assertEquals(gameSession, returned);
    }

    @Test
    void testGetSession_returnValidInput() {

        Mockito.lenient().when(sessionRepository.findBySessionCode(any(String.class)))
                .thenReturn(Optional.of(new GameSession(new Invite(new Player("Catalin")))));

        GameSession gameSession = sessionService.getSession("1234");

        verify(sessionRepository, times(1)).findBySessionCode(ArgumentMatchers.eq("1234"));
        Assertions.assertNotNull(gameSession.getFirstPlayer());

    }

    @Test
    void testGetSession_throwException() {

        Mockito.lenient().when(sessionRepository.findBySessionCode(any(String.class)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            sessionService.getSession("invalid");
        });

        verify(sessionRepository, times(1))
                .findBySessionCode(ArgumentMatchers.eq("invalid"));

    }

}
