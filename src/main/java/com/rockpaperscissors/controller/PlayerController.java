package com.rockpaperscissors.controller;

import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.rockpaperscissors.model.actors.Player.PlayerState.READY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class PlayerController {

    PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping(value = "/player/{playerName}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> player(@PathVariable("playerName") String playerName) {
        return new ResponseEntity<>(playerService.getPlayer(playerName), HttpStatus.OK);
    }

    @PostMapping(value = "/player/{playerName}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> createPlayer(@PathVariable("playerName") String playerName) {
        return new ResponseEntity<>(playerService.createPlayer(playerName), HttpStatus.CREATED);
    }

    @PutMapping(value = "/readyplayer/{playername}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> ready(@PathVariable("playername") String playerName) {
        return new ResponseEntity<>(playerService.changePlayerState(playerName, READY), HttpStatus.OK);
    }

    @DeleteMapping(value = "/player/{playerName}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deletePlayer(@PathVariable("playerName") String playerName) {
        playerService.deletePlayer(playerName);
        return ResponseEntity.noContent().build();
    }


}
