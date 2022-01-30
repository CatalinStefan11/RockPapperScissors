package com.rockpaperscissors.service;

import com.rockpaperscissors.exception.customexceptions.GameException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.dto.PlayRequest;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.entities.Round;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.aop.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static com.rockpaperscissors.model.actors.Player.PlayerState.WAITING;
import static com.rockpaperscissors.model.entities.Round.GameState.OVER;

@Slf4j
@Service
@Profile("PlayerVsPlayer")
public class PlayerVsPlayerService implements GameService {

    private PlayerService playerService;

    private GameSessionService sessionService;

    public PlayerVsPlayerService(PlayerService playerService, GameSessionService sessionService) {
        this.playerService = playerService;
        this.sessionService = sessionService;
    }

    @Logger("A player has just made a move successfully")
    public void playMove(PlayRequest playRequest) {
        GameSession currentSession = sessionService
                .retrieveSessionAndSetStatePlaying(playRequest.getSessionCode());

        arePlayersNotReady(currentSession.getFirstPlayer(), currentSession.getSecondPlayer());

        evaluateRound(playerService.createTurnFromRequest(playRequest), currentSession);

        if (currentSession.latestRound().getState().equals(OVER)) {
            roundIsOverActions(currentSession);
        }
    }

    @Logger("Players state verified")
    private void arePlayersNotReady(Player playerOne, Player playerTwo) {
        if (playerOne.getCurrentState().equals(WAITING)
                || playerTwo.getCurrentState().equals(WAITING)) {
            log.warn("Players cannot play while they are in state waiting!");
            throw new GameException("Both players should be ready before starting to play");
        }
    }

    @Logger("New turn was played successfully")
    private void evaluateRound(Turn turn, GameSession session) {
        if (session.rounds().isEmpty()) {
            sessionService.createNewRound(turn, session);
        } else {
            Round latestRound = session.latestRound();
            Round.GameState gameState = latestRound.getState();

            switch (gameState) {
                case OVER -> sessionService.createNewRound(turn, session);
                case PLAYING -> sessionService.pushMoveAndUpdateRound(latestRound, turn);
            }
        }
    }

    @Logger("A round is over! Players and game session are now waiting fur further rounds!")
    public void roundIsOverActions(GameSession session) {
        session.setGameState(GameSession.State.OVER);
        sessionService.updateSessionAndLatestRound(session);

        playerService.changePlayerState(session.getFirstPlayer().getPlayerName(), WAITING);
        playerService.changePlayerState(session.getSecondPlayer().getPlayerName(), WAITING);
    }

}
