package com.rockpaperscissors.service;

import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.exception.customexceptions.NotFoundException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.gameplay.Invite;
import com.rockpaperscissors.repository.GameSessionRepository;
import com.rockpaperscissors.repository.RoundRepository;
import com.rockpaperscissors.utils.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class GameSessionService {

    GameSessionRepository sessionRepository;

    RoundRepository roundRepository;

    public GameSessionService(GameSessionRepository sessionRepository, RoundRepository roundRepository) {
        this.sessionRepository = sessionRepository;
        this.roundRepository = roundRepository;
    }

    @Logger("Session created successfully")
    public GameSession createSessionFromInvite(Invite invite) {
        GameSession gameSession = new GameSession(invite);
        return sessionRepository.saveAndFlush(gameSession);
    }

    @Logger("Session & latest round updated successfully")
    public GameSession updateSessionAndLatestRound(GameSession session) {
        if(!session.rounds().isEmpty()){
            roundRepository.saveAndFlush(session.latestRound());
        }
        return sessionRepository.saveAndFlush(session);
    }

    @Logger("Invitation accepted successfully")
    public GameSession acceptInvite(Player player, String inviteCode) {
        GameSession gameSession = sessionRepository
                .findByInviteCode(inviteCode)
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
                .findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException("Session with invite code " + inviteCode + " not found"));
    }


}