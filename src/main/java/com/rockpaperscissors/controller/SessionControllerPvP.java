package com.rockpaperscissors.controller;

import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.gameplay.Invite;
import com.rockpaperscissors.service.GameSessionService;
import com.rockpaperscissors.service.PlayerService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Profile("PlayerVsPlayer")
@RestController
public class SessionControllerPvP {

    PlayerService playerService;

    GameSessionService sessionService;

    public SessionControllerPvP(PlayerService playerService, GameSessionService sessionService) {
        this.playerService = playerService;
        this.sessionService = sessionService;
    }

    @PutMapping(value = "/accept-invite/{sessionCode}/{playerName}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<GameSession> acceptInvite(@PathVariable("sessionCode") String inviteCode,
                                                    @PathVariable("playerName") String playerName) {
        Player player = playerService.getPlayer(playerName);
        return new ResponseEntity<>(sessionService.acceptInvite(player, inviteCode),
                HttpStatus.OK);
    }

}