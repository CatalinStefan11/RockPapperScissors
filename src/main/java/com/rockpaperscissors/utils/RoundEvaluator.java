package com.rockpaperscissors.utils;

import com.rockpaperscissors.model.entities.Round;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.model.gameplay.Move;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.rockpaperscissors.model.gameplay.Move.*;

@Component
public class RoundEvaluator {

    public void evaluate(Round round) {
        Turn firstTurn = round.getFirstMove();
        Turn secondTurn = round.getSecondMove();

        if (!firstTurn.getMove().equals(secondTurn.getMove())) {
            evaluateOpposingMoves(firstTurn, secondTurn)
                    .ifPresent((winner) -> round.setWinner(winner.getPlayer().getPlayerName()));
        } else {
            round.setTie(true);
        }
    }

    private Optional<Turn> evaluateOpposingMoves(Turn firstTurn, Turn secondTurn) {
        Move firstMove = firstTurn.getMove();
        Move secondMove = secondTurn.getMove();

        if ((firstMove.equals(PAPER) && secondMove.equals(ROCK))
                || firstMove.equals(ROCK) && secondMove.equals(SCISSORS)
                || (firstMove.equals(SCISSORS) && secondMove.equals(PAPER))) {
            return Optional.of(firstTurn);
        } else if ((firstMove.equals(PAPER) && secondMove.equals(SCISSORS))
                || (firstMove.equals(ROCK) && secondMove.equals(PAPER))
                || (firstMove.equals(SCISSORS) && secondMove.equals(ROCK))) {
            return Optional.of(secondTurn);
        }
        return Optional.empty();
    }
}
