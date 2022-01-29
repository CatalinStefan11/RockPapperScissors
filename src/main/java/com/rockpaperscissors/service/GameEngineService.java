package com.rockpaperscissors.service;

import com.rockpaperscissors.exception.customexceptions.GameException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.dto.PlayRequest;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.entities.Round;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.model.gameplay.Move;
import com.rockpaperscissors.repository.RoundRepository;
import com.rockpaperscissors.repository.TurnRepository;
import com.rockpaperscissors.utils.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import static com.rockpaperscissors.model.actors.Player.PlayerState.WAITING;
import static com.rockpaperscissors.model.entities.GameSession.State.PLAYING;
import static com.rockpaperscissors.model.entities.Round.GameState.OVER;

@Service
@Slf4j
public class GameEngineService {

    private PlayerService playerService;

    private GameSessionService sessionService;

    private TurnRepository turnRepository;

    public GameEngineService(PlayerService playerService, GameSessionService sessionService,
                             TurnRepository turnRepository) {
        this.playerService = playerService;
        this.sessionService = sessionService;
        this.turnRepository = turnRepository;
    }

    @Logger("A player has just made a move successfully")
    public void playMove(PlayRequest playRequest) {
        GameSession currentSession = retrieveSessionAndSetStatePlaying(playRequest.getInviteCode());

        arePlayersNotReady(currentSession.getFirstPlayer(), currentSession.getSecondPlayer());

        Player player = playerService.changePlayerState(
                playRequest.getPlayerName(), Player.PlayerState.PLAYING);

        final Turn turn = new Turn(player, Enum.valueOf(Move.class, playRequest.getMove()));
        turnRepository.save(turn);

        evaluateRound(turn, currentSession);

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

    @Logger("Session retrieved successfully")
    private GameSession retrieveSessionAndSetStatePlaying(String invitationCode) {
        GameSession session = sessionService.getSession(invitationCode);
        if (!session.getGameState().equals(PLAYING)) {
            log.info("Session with id {} is now in state PLAYING!", session.getSessionId());
            session.setGameState(PLAYING);
        }
        return session;
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
    private void roundIsOverActions(GameSession session) {
        session.setGameState(GameSession.State.WAITING);
        sessionService.updateSessionAndLatestRound(session);

        playerService.changePlayerState(session.getFirstPlayer().getPlayerName(), WAITING);
        playerService.changePlayerState(session.getSecondPlayer().getPlayerName(), WAITING);
    }
}
