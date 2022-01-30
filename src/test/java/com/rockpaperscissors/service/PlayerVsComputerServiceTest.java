package com.rockpaperscissors.service;

import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.dto.PlayRequest;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.entities.Round;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.model.gameplay.Invite;
import com.rockpaperscissors.model.gameplay.Move;
import com.rockpaperscissors.repository.TurnRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.rockpaperscissors.utils.AppConstants.COMPUTER_PLAYER_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerVsComputerServiceTest {

    @Mock
    private PlayerService playerService;

    @Mock
    private GameSessionService sessionService;

    @Mock
    private TurnRepository turnRepository;

    private PayerVsComputerService playerVsComputerService;

    private Player COMPUTER;

    @BeforeEach
    void init() {
        playerVsComputerService = new PayerVsComputerService(playerService, sessionService, turnRepository);

        COMPUTER = new Player(COMPUTER_PLAYER_NAME);

        Mockito.lenient().when(playerService.getPlayer(COMPUTER_PLAYER_NAME))
                .thenReturn(COMPUTER);

        doAnswer(invocationOnMock -> {
            COMPUTER.setCurrentState(Player.PlayerState.READY);
            return null;
        }).when(playerService).changePlayerState(COMPUTER_PLAYER_NAME, Player.PlayerState.READY);

    }


    @Test
    void testPlayMove_WithoutThrowingException() {

        Player player = new Player("Catalin");
        player.setCurrentState(Player.PlayerState.READY);

        Turn turn = new Turn(player, Move.ROCK);
        PlayRequest request = new PlayRequest(player.getPlayerName(), "1234", "rock");

        Invite invite = new Invite(player);
        GameSession gameSession = new GameSession(invite);
        gameSession.setSecondPlayer(COMPUTER);

        Mockito.lenient().when(sessionService.retrieveSessionAndSetStatePlaying(any(String.class)))
                .thenReturn(gameSession);

        Mockito.lenient().when(playerService.createTurnFromRequest(any()))
                .thenReturn(turn);

        Mockito.lenient().when(playerService.getPlayer(player.getPlayerName()))
                .thenReturn(player);

        Round round = new Round(turn);
        doAnswer(invocationOnMock -> {
            gameSession.addRound(round);
            return null;
        }).when(sessionService).createNewRound(any(Turn.class), any(GameSession.class));

        doAnswer(invocationOnMock -> {
            round.setSecondMove(new Turn(COMPUTER, Move.PAPER));
            round.changeStateTo(Round.GameState.OVER);
            return null;
        }).when(sessionService).pushMoveAndUpdateRound(any(Round.class), any(Turn.class));

        playerVsComputerService.playMove(request);

        verify(sessionService, times(1)).retrieveSessionAndSetStatePlaying(any());
        verify(sessionService, times(1)).pushMoveAndUpdateRound(any(), any());
        verify(sessionService, times(1)).createNewRound(any(), any());
        verify(turnRepository, times(1)).saveAndFlush(any());

    }


}
