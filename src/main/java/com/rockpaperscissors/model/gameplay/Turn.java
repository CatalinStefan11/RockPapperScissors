package com.rockpaperscissors.model.gameplay;

import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.model.actors.Player;
import lombok.Getter;

@Getter
public class Turn {

    private Player player;
    private Move move;

    public Turn(Player player, Move move) {
        if (player == null || move == null) {
            throw new InvalidOperationException("A turn cannot be played without a Player & a Move");
        }
        this.player = player;
        this.move = move;
    }
}