package com.rockpaperscissors.controller;

import com.rockpaperscissors.model.dto.PlayRequest;
import com.rockpaperscissors.service.GameEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class GameController {

    private GameEngineService gameEngineService;

    public GameController(GameEngineService gameEngineService) {
        this.gameEngineService = gameEngineService;
    }

    @PostMapping(value = "/play", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity play(@RequestBody PlayRequest playRequest) {
        gameEngineService.playMove(playRequest);
        return ResponseEntity.ok().body("");
    }
}
