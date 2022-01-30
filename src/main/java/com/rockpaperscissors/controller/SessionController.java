package com.rockpaperscissors.controller;

import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.gameplay.Invite;
import com.rockpaperscissors.service.GameSessionService;
import com.rockpaperscissors.service.PlayerService;
import org.springframework.context.annotation.Profile;
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
    public ResponseEntity<GameSession> createSession(@PathVariable("playerName") String invite) {
        Player player = playerService.getPlayer(invite);
        return new ResponseEntity<>(sessionService.createSessionFromInvite(new Invite(player)),
                HttpStatus.CREATED);
    }

    @GetMapping(value = "/result/{sessionCode}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<GameSession> session(@PathVariable("sessionCode") String inviteCode) {
        return new ResponseEntity<>(sessionService.getSession(inviteCode),
                HttpStatus.OK);
    }


}
