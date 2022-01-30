package com.rockpaperscissors.service;

import com.rockpaperscissors.exception.customexceptions.GameException;
import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.exception.customexceptions.NotFoundException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.entities.Round;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.model.gameplay.Invite;
import com.rockpaperscissors.repository.GameSessionRepository;
import com.rockpaperscissors.repository.RoundRepository;
import com.rockpaperscissors.utils.RoundEvaluator;
import com.rockpaperscissors.aop.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.rockpaperscissors.model.entities.GameSession.State.PLAYING;

@Service
@Slf4j
public class GameSessionService {

    GameSessionRepository sessionRepository;

    RoundRepository roundRepository;

    RoundEvaluator roundEvaluator;

    public GameSessionService(GameSessionRepository sessionRepository, RoundRepository roundRepository,
                              RoundEvaluator roundEvaluator) {
        this.sessionRepository = sessionRepository;
        this.roundRepository = roundRepository;
        this.roundEvaluator = roundEvaluator;
    }

    @Logger("Session created successfully")
    public GameSession createSessionFromInvite(Invite invite) {
        GameSession gameSession = new GameSession(invite);
        return sessionRepository.saveAndFlush(gameSession);
    }

    @Logger("Session & latest round updated successfully")
    public void updateSessionAndLatestRound(GameSession session) {
        if (!session.rounds().isEmpty()) {
            roundRepository.saveAndFlush(session.latestRound());
        }
        sessionRepository.saveAndFlush(session);
    }

    @Logger("Invitation accepted successfully")
    public GameSession acceptInvite(Player player, String inviteCode) {
        GameSession gameSession = sessionRepository
                .findBySessionCode(inviteCode)
                .orElseThrow(() -> {
                    log.warn("Session with invite code {} not found in the database!", inviteCode);
                    return new NotFoundException("Session with invite code " + inviteCode + " not found");
                });

        if (player.equals(gameSession.getFirstPlayer())) {
            log.warn("Player {} cannot accept his own invite!", player.getPlayerName());
            throw new InvalidOperationException("A player cannot accept their own invite");
        }
        gameSession.addOpponent(player);
        gameSession.setGameState(GameSession.State.ACCEPTED);
        sessionRepository.saveAndFlush(gameSession);
        return gameSession;
    }

    @Logger("Session retrieved successfully")
    public GameSession getSession(String inviteCode) {
        return sessionRepository
                .findBySessionCode(inviteCode)
                .orElseThrow(() -> {
                    log.warn("Session with inviteCode {} not found!", inviteCode);
                    return new NotFoundException("Session with invite code " + inviteCode + " not found");
                });
    }

    @Logger("New round created successfully")
    public void createNewRound(Turn turn, GameSession gameSession) {
        gameSession.addRound(new Round(turn));
        updateSessionAndLatestRound(gameSession);
    }

    @Logger("Second turn was pushed successfully")
    public void pushMoveAndUpdateRound(Round actualRound, Turn turn) {

        Player currentPlayer = turn.getPlayer();
        if (actualRound.getFirstMove().getPlayer().equals(currentPlayer)) {
            throw new InvalidOperationException(
                    "The same player cannot play again without the opponent making a move");
        } else {
            actualRound.setSecondMove(turn);
            roundEvaluator.evaluate(actualRound);
            actualRound.changeStateTo(Round.GameState.OVER);

        }
        roundRepository.saveAndFlush(actualRound);
    }

    @Logger("Session retrieved successfully")
    public GameSession retrieveSessionAndSetStatePlaying(String invitationCode) {
        GameSession session = getSession(invitationCode);
        if (session.getGameState().equals(GameSession.State.WAITING)) {
            log.warn("Attempt to play while invitation has not been accepted yet!");
            throw new GameException("The invite should be accepted by the second player in order to play!");
        }
        if (!session.getGameState().equals(PLAYING)) {
            log.info("Session with id {} is now in state PLAYING!", session.getSessionId());
            session.setGameState(PLAYING);
        }
        return session;
    }

}