package com.rockpaperscissors.service;

import com.rockpaperscissors.exception.customexceptions.GameException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.dto.PlayRequest;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.model.gameplay.Move;
import com.rockpaperscissors.repository.TurnRepository;
import com.rockpaperscissors.aop.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static com.rockpaperscissors.model.actors.Player.PlayerState.READY;
import static com.rockpaperscissors.model.actors.Player.PlayerState.WAITING;
import static com.rockpaperscissors.model.entities.Round.GameState.OVER;
import static com.rockpaperscissors.utils.AppConstants.COMPUTER_PLAYER_NAME;

@Slf4j
@Service
@Profile("PlayerVsComputer")
public class PayerVsComputerService implements GameService {

    private PlayerService playerService;

    private GameSessionService sessionService;

    private TurnRepository turnRepository;

    public PayerVsComputerService(PlayerService playerService, GameSessionService sessionService,
                                  TurnRepository turnRepository) {
        this.playerService = playerService;
        this.sessionService = sessionService;
        this.turnRepository = turnRepository;
    }

    @Logger("A player has just made a move successfully")
    public void playMove(PlayRequest playRequest) {

        Player computer = playerService.getPlayer(COMPUTER_PLAYER_NAME);
        if(!computer.getCurrentState().equals(READY)){
            computer = playerService.changePlayerState(COMPUTER_PLAYER_NAME, READY);
        }
        sessionService.acceptInvite(computer, playRequest.getSessionCode());

        GameSession currentSession = sessionService.retrieveSessionAndSetStatePlaying(playRequest.getSessionCode());

        if(!playerService.getPlayer(playRequest.getPlayerName())
                .equals(currentSession.getFirstPlayer())){
            throw new GameException("The player that attempted to play did not create the session!");
        }

        isPlayerNotReady(currentSession.getFirstPlayer());

        evaluateRound(playerService.createTurnFromRequest(playRequest), currentSession);

        if (currentSession.latestRound().getState().equals(OVER)) {
            roundIsOverActions(currentSession);
        }
    }

    @Logger("Player state verified")
    private void isPlayerNotReady(Player player) {
        if (player.getCurrentState().equals(WAITING)) {
            log.warn("Player cannot play while he is in state waiting!");
            throw new GameException("Player should be ready before starting to play");
        }
    }

    @Logger("Player and computer played their turns successfully")
    private void evaluateRound(Turn playerTurn, GameSession session) {
        sessionService.createNewRound(playerTurn, session);
        sessionService.pushMoveAndUpdateRound(session.latestRound(), computerMove(session.getSecondPlayer()));
    }

    @Logger("Computer played a turn successfully")
    private Turn computerMove(Player computer) {
        final List<Move> values = List.of(Move.values());
        final int size = values.size();
        final Random random = new Random();

        Turn turn = new Turn(computer, values.get(random.nextInt(size)));
        turnRepository.saveAndFlush(turn);
        return turn;
    }

    @Logger("A round is over! Players and game session are now waiting fur further rounds!")
    private void roundIsOverActions(GameSession session) {
        session.setGameState(GameSession.State.OVER);
        sessionService.updateSessionAndLatestRound(session);

        playerService.changePlayerState(session.getFirstPlayer().getPlayerName(), WAITING);
        playerService.changePlayerState(session.getSecondPlayer().getPlayerName(), WAITING);
    }

}
