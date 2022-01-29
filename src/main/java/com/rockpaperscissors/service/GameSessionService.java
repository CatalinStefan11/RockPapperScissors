package com.rockpaperscissors.service;

import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.exception.customexceptions.NotFoundException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.gameplay.Invite;
import com.rockpaperscissors.repository.GameSessionRepository;
import com.rockpaperscissors.utils.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class GameSessionService {

    GameSessionRepository sessionRepository;

    public GameSessionService(GameSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Logger("Session created successfully")
    public GameSession createSessionFromInvite(Invite invite) {
        GameSession gameSession = new GameSession(invite);
        return sessionRepository.save(gameSession);
    }

    @Logger("Session updated successfully")
    public GameSession saveSession(GameSession session) {
        return sessionRepository.save(session);
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
        gameSession.changeStateTo(GameSession.State.ACCEPTED);
        sessionRepository.save(gameSession);
        return gameSession;
    }

    @Logger("Session retrieved successfully")
    public GameSession getSession(String inviteCode) {
        return sessionRepository
                .findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException("Session with invite code " + inviteCode + " not found"));
    }


}