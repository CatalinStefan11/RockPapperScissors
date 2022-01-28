package com.rockpaperscissors.model.gameplay;

import com.rockpaperscissors.model.actors.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {

    private Player winner;
    private boolean tie;
}
