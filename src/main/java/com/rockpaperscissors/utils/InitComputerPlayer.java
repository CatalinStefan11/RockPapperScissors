package com.rockpaperscissors.utils;

import com.rockpaperscissors.service.PlayerService;
import com.rockpaperscissors.aop.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.rockpaperscissors.utils.AppConstants.COMPUTER_PLAYER_NAME;

@Slf4j
@Component
@Profile("PlayerVsComputer")
public class InitComputerPlayer {

    PlayerService playerService;

    public InitComputerPlayer(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Logger("Computer player was initialized")
    @EventListener(ApplicationReadyEvent.class)
    public void createComputerPlayer() {
        playerService.createPlayer(COMPUTER_PLAYER_NAME);
    }
}
