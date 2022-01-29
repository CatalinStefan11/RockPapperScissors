package com.rockpaperscissors.controller;

import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.gameplay.Invite;
import com.rockpaperscissors.service.GameSessionService;
import com.rockpaperscissors.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class SessionController {

    PlayerService playerService;

    GameSessionService sessionService;

    public SessionController(PlayerService playerService, GameSessionService sessionService) {
        this.playerService = playerService;
        this.sessionService = sessionService;
    }

    @PostMapping(value = "/create-game/{playerName}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<GameSession> createSession(@PathVariable("playerName") String inviter) {
        Player player = playerService.getPlayer(inviter);
        return new ResponseEntity<>(sessionService.createSessionFromInvite(new Invite(player)),
                HttpStatus.CREATED);
    }

    @PutMapping(value = "/accept-invite/{sessionCode}/{playerName}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<GameSession> acceptInvite(@PathVariable("inviteCode") String inviteCode,
                                                    @PathVariable("playerName") String playerName) {
        Player player = playerService.getPlayer(playerName);
        return new ResponseEntity<>(sessionService.acceptInvite(player, inviteCode),
                HttpStatus.OK);
    }

    @GetMapping(value = "/result/{sessionCode}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<GameSession> session(@PathVariable("sessionCode") String inviteCode) {
        return new ResponseEntity<>(sessionService.getSession(inviteCode),
                HttpStatus.OK);
    }


}
