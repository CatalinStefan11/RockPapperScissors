package com.rockpaperscissors.service;

import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.exception.customexceptions.NotFoundException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.gameplay.GameSession;
import com.rockpaperscissors.model.gameplay.Invite;
import com.rockpaperscissors.repository.GameSessionRepository;
import com.rockpaperscissors.utils.logging.Logger;
import org.springframework.stereotype.Service;


@Service
public class GameSessionService {

    GameSessionRepository sessionRepository;

    public GameSessionService(GameSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Logger(value = "Session created", showData = true)
    public GameSession createSessionFromInvite(Invite invite) {
        GameSession gameSession = new GameSession(invite);
        return sessionRepository.save(gameSession);
    }

    @Logger(value = "Invitation accepted", showData = true)
    public GameSession acceptInvite(Player player, String inviteCode) {
        GameSession gameSession = sessionRepository
                .findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException("Session with invite code " + inviteCode + " not found"));

        if (player.equals(gameSession.getFirstPlayer())) {
            throw new InvalidOperationException("A player cannot accept their own invite");
        }
        gameSession.addOpponent(player);
        gameSession.changeStateTo(GameSession.State.ACCEPTED);
        sessionRepository.save(gameSession);
        return gameSession;
    }

    @Logger(value = "Session retrieved", showData = true)
    public GameSession getSession(String inviteCode) {
        return sessionRepository
                .findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException("Session with invite code " + inviteCode + " not found"));
    }



}