package com.rockpaperscissors.service;

import com.rockpaperscissors.exception.customexceptions.AlreadyExistsException;
import com.rockpaperscissors.exception.customexceptions.GameException;
import com.rockpaperscissors.exception.customexceptions.NotFoundException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.repository.PlayerRepository;
import com.rockpaperscissors.utils.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.rockpaperscissors.model.actors.Player.PlayerState.*;

@Service
@Slf4j
public class PlayerService {

    private PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Logger("Player created successfully!")
    public Player createPlayer(String name) {
        if (playerRepository.findByPlayerName(name).isPresent()) {
            log.warn("Player {} already exists in the database!", name);
            throw new AlreadyExistsException(name + " already exists");
        }
        return playerRepository.save(new Player(name));
    }

    @Logger("Player retrieved successfully!")
    public Player getPlayer(String name) {
        return playerRepository.findByPlayerName(name)
                .orElseThrow(() -> {
                    log.warn("Player {} not found in the database!", name);
                    return new NotFoundException("Player " + name + " not found");
                });
    }

    @Logger("Player deleted successfully!")
    public void deletePlayer(String name) {
        Player player = getPlayer(name);
        if (PLAYING.equals(player.getCurrentState())) {
            log.warn("Player {} cannot be deleted while playing!", name);
            throw new GameException("Cannot delete a player in the middle of a game");
        }
        playerRepository.delete(player);
    }

    @Logger("Player changed state successfully!")
    public void changePlayerState(String existingPlayerName, Player.PlayerState newState) {
        Player player = getPlayer(existingPlayerName);
        Player.PlayerState currentStateOfPlayer = player.getCurrentState();
        if (PLAYING.equals(newState) && WAITING.equals(currentStateOfPlayer)) {
            log.warn("Player {} cannot play while he is in state waiting!", existingPlayerName);
            throw new GameException("Both players should be READY before starting PLAY");
        }
        if (currentStateOfPlayer.equals(newState)) {
            log.warn("Player {} is already in state {}!", existingPlayerName, newState);
            throw new AlreadyExistsException("Player " + existingPlayerName + " is already in state: " + newState);
        }
        player.setCurrentState(newState);
        playerRepository.saveAndFlush(player);
    }


}