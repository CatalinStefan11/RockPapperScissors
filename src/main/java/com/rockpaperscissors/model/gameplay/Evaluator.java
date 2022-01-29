package com.rockpaperscissors.model.gameplay;

import com.rockpaperscissors.model.entities.Round;
import com.rockpaperscissors.model.entities.Turn;

import java.util.Optional;

import static com.rockpaperscissors.model.gameplay.Move.*;

public class Evaluator {

    Evaluator() {
        // package-private for tests
    }

    public static void evaluate(Round round, Turn firstTurn, Turn secondTurn) {
        if (!firstTurn.getMove().equals(secondTurn.getMove())) {
            Optional<Move> move = evaluateOpposingMoves(firstTurn.getMove(), secondTurn.getMove());
            if (move.isPresent()) {
                Move winningMove = move.get();
                if (winningMove.equals(firstTurn.getMove())) {
                    round.setWinner(firstTurn.getPlayer().getPlayerName());
                } else if (winningMove.equals(secondTurn.getMove())) {
                    round.setWinner(secondTurn.getPlayer().getPlayerName());
                }
            }
        }else{
            round.setTie(true);
        }
    }

    private static Optional<Move> evaluateOpposingMoves(Move move1, Move move2) {
        if ((move1.equals(PAPER) && move2.equals(ROCK))
                || move1.equals(ROCK) && move2.equals(SCISSORS)
                || (move1.equals(SCISSORS) && move2.equals(PAPER))) {
            return Optional.of(move1);
        } else if ((move1.equals(PAPER) && move2.equals(SCISSORS))
                || (move1.equals(ROCK) && move2.equals(PAPER))
                || (move1.equals(SCISSORS) && move2.equals(ROCK))) {
            return Optional.of(move2);
        }
        return Optional.empty();
    }
}

