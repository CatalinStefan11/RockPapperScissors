package com.rockpaperscissors.service;

import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.dto.PlayRequest;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.entities.Round;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.model.gameplay.Invite;
import com.rockpaperscissors.model.gameplay.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerVsPlayerServiceTest {

    @Mock
    private PlayerService playerService;

    @Mock
    private GameSessionService sessionService;

    private PlayerVsPlayerService playerVsPlayerService;

    @BeforeEach
    void init() {
        playerVsPlayerService = new PlayerVsPlayerService(playerService, sessionService);
    }

    @Test
    void testPlayMove_WithoutThrowingException() {

        Player player1 = new Player("Catalin");
        player1.setCurrentState(Player.PlayerState.READY);
        Player player2 = new Player("Messi");
        player2.setCurrentState(Player.PlayerState.READY);

        Turn turn = new Turn(player1, Move.ROCK);
        PlayRequest request = new PlayRequest(player1.getPlayerName(), "1234", "rock");

        Invite invite = new Invite(player1);
        GameSession gameSession = new GameSession(invite);
        gameSession.setSecondPlayer(player2);

        Mockito.lenient().when(sessionService.retrieveSessionAndSetStatePlaying(any(String.class)))
                .thenReturn(gameSession);

        Mockito.lenient().when(playerService.createTurnFromRequest(any()))
                .thenReturn(turn);

        doAnswer(invocationOnMock -> {
            gameSession.addRound(new Round(turn));
            return null;
        }).when(sessionService).createNewRound(any(Turn.class), any(GameSession.class));

        playerVsPlayerService.playMove(request);

        verify(sessionService, times(1)).retrieveSessionAndSetStatePlaying(any());
        verify(sessionService, times(1)).createNewRound(any(), any());

    }


    @Test
    void testPlayMove_WithoutThrowingExceptionSecondPlayerTurn() {

        Player player1 = new Player("Catalin");
        player1.setCurrentState(Player.PlayerState.PLAYING);
        Player player2 = new Player("Messi");
        player2.setCurrentState(Player.PlayerState.READY);


        Turn turnFirstPlayer = new Turn(player1, Move.ROCK);

        Invite invite = new Invite(player1);
        GameSession gameSession = new GameSession(invite);
        gameSession.setSecondPlayer(player2);
        Round round = new Round(turnFirstPlayer);
        gameSession.addRound(round);


        Turn turnSecondPlayer = new Turn(player2, Move.ROCK);
        PlayRequest request = new PlayRequest(player2.getPlayerName(), "1234", "rock");

        Mockito.lenient().when(sessionService.retrieveSessionAndSetStatePlaying(any(String.class)))
                .thenReturn(gameSession);

        Mockito.lenient().when(playerService.createTurnFromRequest(any()))
                .thenReturn(turnSecondPlayer);

        doAnswer(invocationOnMock -> {
            round.setSecondMove(turnSecondPlayer);
            round.changeStateTo(Round.GameState.OVER);
            return null;
        }).when(sessionService).pushMoveAndUpdateRound(any(Round.class), any(Turn.class));

        playerVsPlayerService.playMove(request);

        verify(sessionService, times(1)).retrieveSessionAndSetStatePlaying(any());
        verify(sessionService, times(1)).pushMoveAndUpdateRound(any(), any());

    }

    @Test
    void testPlayMove_WithoutThrowingExceptionRoundOver() {

        Player player1 = new Player("Catalin");
        player1.setCurrentState(Player.PlayerState.PLAYING);
        Player player2 = new Player("Messi");
        player2.setCurrentState(Player.PlayerState.READY);

        Turn turnFirstPlayer = new Turn(player1, Move.ROCK);

        Invite invite = new Invite(player1);
        GameSession gameSession = new GameSession(invite);
        Turn turnSecondPlayer = new Turn(player2, Move.ROCK);

        gameSession.setSecondPlayer(player2);
        Round round = new Round(turnFirstPlayer);
        round.setSecondMove(turnSecondPlayer);
        round.changeStateTo(Round.GameState.OVER);
        gameSession.addRound(round);

        PlayRequest request = new PlayRequest(player1.getPlayerName(), "1234", "rock");

        Mockito.lenient().when(sessionService.retrieveSessionAndSetStatePlaying(any(String.class)))
                .thenReturn(gameSession);

        Mockito.lenient().when(playerService.createTurnFromRequest(any()))
                .thenReturn(turnSecondPlayer);

        doAnswer(invocationOnMock -> {
            gameSession.addRound(new Round(turnFirstPlayer));
            return null;
        }).when(sessionService).createNewRound(any(Turn.class), any(GameSession.class));

        playerVsPlayerService.playMove(request);

        verify(sessionService, times(1)).retrieveSessionAndSetStatePlaying(any());
        verify(sessionService, times(1)).createNewRound(any(), any());
    }


}
