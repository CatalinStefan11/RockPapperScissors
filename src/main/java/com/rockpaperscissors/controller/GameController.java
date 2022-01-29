package com.rockpaperscissors.controller;

import com.rockpaperscissors.model.dto.PlayRequest;
import com.rockpaperscissors.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class GameController {

    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping(value = "/play", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity play(@RequestBody PlayRequest playRequest) {
        gameService.playMove(playRequest);
        return ResponseEntity.ok().body("");
    }
}
