package com.rockpaperscissors.service;

import com.rockpaperscissors.exception.customexceptions.GameException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.dto.PlayRequest;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.entities.Round;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.model.gameplay.Move;
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

        evaluateRound(createTurnFromRequest(playRequest), currentSession);

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
        if(session.getGameState().equals(GameSession.State.WAITING)){
            log.warn("Attempt to play while invitation has not been accepted yet!");
            throw new GameException("The invite should be accepted by the second player in order to play!");
        }
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

    @Logger("Turn saved to the database successfully")
    private Turn createTurnFromRequest(PlayRequest request) {
        Move move = null;
        try {
            move = Enum.valueOf(Move.class, request.getMove().toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.warn("An illegal move was made!");
            throw new GameException("Illegal move! A move should be ROCK / PAPER / SCISSORS!");
        }
        Player player = playerService.changePlayerState(
                request.getPlayerName(), Player.PlayerState.PLAYING);

        Turn turn = new Turn(player, move);
        turnRepository.save(turn);

        return turn;
    }
}
