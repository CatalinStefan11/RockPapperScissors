package com.rockpaperscissors.service;

import com.rockpaperscissors.exception.customexceptions.AlreadyExistsException;
import com.rockpaperscissors.exception.customexceptions.GameException;
import com.rockpaperscissors.exception.customexceptions.NotFoundException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.dto.PlayRequest;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.model.gameplay.Move;
import com.rockpaperscissors.repository.PlayerRepository;
import com.rockpaperscissors.repository.TurnRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TurnRepository turnRepository;

    PlayerService playerService;

    @BeforeEach
    void init() {
        playerService = new PlayerService(playerRepository, turnRepository);
    }

    @Test
    void testCreatePlayer_RunsWithoutException() {

        String playerName = "player";

        Mockito.lenient().when(playerRepository.findByPlayerName(any(String.class)))
                .thenReturn(Optional.empty());

        Mockito.lenient().when(playerRepository.saveAndFlush(any()))
                .thenReturn(new Player(playerName));

        Player returned = playerService.createPlayer(playerName);

        verify(playerRepository, times(1)).saveAndFlush(any());

        Assertions.assertEquals(playerName, returned.getPlayerName());
    }

    @Test
    void testCreatePlayer_throwsExceptionWhenPlayerExists() {

        String playerName = "player";

        Mockito.lenient().when(playerRepository.findByPlayerName(any(String.class)))
                .thenReturn(Optional.of(new Player(playerName)));

        Assertions.assertThrows(AlreadyExistsException.class, () -> {
            playerService.createPlayer(playerName);
        });
    }

    @Test
    void testGetPlayer_runsWithoutException() {

        String playerName = "player";

        Mockito.lenient().when(playerRepository.findByPlayerName(any(String.class)))
                .thenReturn(Optional.of(new Player(playerName)));

        Player returned = playerService.getPlayer(playerName);

        verify(playerRepository, times(1)).findByPlayerName(any());

        Assertions.assertEquals(playerName, returned.getPlayerName());
    }

    @Test
    void testGetPlayer_throwsException() {

        String playerName = "player";

        Mockito.lenient().when(playerRepository.findByPlayerName(any(String.class)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            playerService.getPlayer(playerName);
        });

        verify(playerRepository, times(1)).findByPlayerName(any());
    }

    @Test
    void testDeletePlayer_runsWithoutException() {

        Player player = new Player("player");

        Mockito.lenient().when(playerRepository.findByPlayerName(any(String.class)))
                .thenReturn(Optional.of(player));

        playerService.deletePlayer(player.getPlayerName());

        verify(playerRepository, times(1)).findByPlayerName(any());
        verify(playerRepository, times(1)).delete(any());
    }

    @Test
    void testDeletePlayer_playerNotFound() {

        Player player = new Player("player");

        Mockito.lenient().when(playerRepository.findByPlayerName(any(String.class)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            playerService.deletePlayer(player.getPlayerName());
        });

        verify(playerRepository, times(1)).findByPlayerName(any());
    }

    @Test
    void testDeletePlayer_ExceptionWhenPlayerInStatePlaying() {

        Player player = new Player("player");
        player.setCurrentState(Player.PlayerState.PLAYING);

        Mockito.lenient().when(playerRepository.findByPlayerName(any(String.class)))
                .thenReturn(Optional.of(player));

        Assertions.assertThrows(GameException.class, () -> {
            playerService.deletePlayer(player.getPlayerName());
        });

        verify(playerRepository, times(1)).findByPlayerName(any());
    }

    @Test
    void testChangePlayerState_RunsWithoutException() {

        Player player = new Player("player");
        player.setCurrentState(Player.PlayerState.READY);

        Mockito.lenient().when(playerRepository.findByPlayerName(any(String.class)))
                .thenReturn(Optional.of(new Player("player")));

        Mockito.lenient().when(playerRepository.saveAndFlush(any()))
                .thenReturn(player);

        Player returned = playerService
                .changePlayerState(player.getPlayerName(), Player.PlayerState.READY);

        verify(playerRepository, times(1)).findByPlayerName(any());
        verify(playerRepository, times(1)).saveAndFlush(any());

        Assertions.assertEquals(Player.PlayerState.READY, returned.getCurrentState());

    }

    @Test
    void testChangePlayerState_PlayerNotFound() {

        Player player = new Player("player");

        Mockito.lenient().when(playerRepository.findByPlayerName(any(String.class)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            playerService.changePlayerState(player.getPlayerName(), Player.PlayerState.READY);
        });

        verify(playerRepository, times(1)).findByPlayerName(any());
    }

    @Test
    void testChangePlayerState_ThrowsExWhenPlayerInStateWaitingWantsToChangeInPlaying() {

        Player player = new Player("player");

        Mockito.lenient().when(playerRepository.findByPlayerName(any(String.class)))
                .thenReturn(Optional.of(new Player("player")));

        Assertions.assertThrows(GameException.class, () -> {
            playerService.changePlayerState(player.getPlayerName(), Player.PlayerState.PLAYING);
        });

        verify(playerRepository, times(1)).findByPlayerName(any());
    }

    @Test
    void testChangePlayerState_ThrowsExWhenPlayerAlreadyInState() {

        Player player = new Player("player");
        player.setCurrentState(Player.PlayerState.PLAYING);

        Mockito.lenient().when(playerRepository.findByPlayerName(any(String.class)))
                .thenReturn(Optional.of(player));

        Assertions.assertThrows(AlreadyExistsException.class, () -> {
            playerService.changePlayerState(player.getPlayerName(), Player.PlayerState.PLAYING);
        });

        verify(playerRepository, times(1)).findByPlayerName(any());
    }

    @Test
    void testCreateTurnFromRequest_RunsWithoutException() {

        Player player = new Player("player");
        player.setCurrentState(Player.PlayerState.READY);

        PlayRequest playRequest = new PlayRequest(player.getPlayerName(), "1234", "rock");

        Mockito.lenient().when(turnRepository.saveAndFlush(any()))
                .thenReturn(new Turn(player, Move.ROCK));

        Mockito.lenient().when(playerRepository.findByPlayerName(any(String.class)))
                .thenReturn(Optional.of(player));

        Mockito.lenient().when(playerRepository.saveAndFlush(any()))
                .thenReturn(player);

        Turn turn = playerService.createTurnFromRequest(playRequest);

        verify(playerRepository, times(1)).findByPlayerName(any());
        verify(playerRepository, times(1)).saveAndFlush(any());
        verify(turnRepository, times(1)).saveAndFlush(any());

        Assertions.assertEquals(Move.ROCK, turn.getMove());
    }


    @Test
    void testCreateTurnFromRequest_throwsExceptionWhenMoveIsIllegal() {

        Player player = new Player("player");
        player.setCurrentState(Player.PlayerState.READY);

        PlayRequest playRequest = new PlayRequest(player.getPlayerName(), "1234", "illegalMove");

        Assertions.assertThrows(GameException.class, () -> {
            playerService.createTurnFromRequest(playRequest);
        });

    }
}
