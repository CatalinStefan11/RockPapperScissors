package com.rockpaperscissors.model.gameplay;

import com.rockpaperscissors.model.entities.Result;
import com.rockpaperscissors.model.entities.Turn;

import java.util.Optional;

import static com.rockpaperscissors.model.gameplay.Move.*;

public class Evaluator {

    Evaluator() {
        // package-private for tests
    }

    public static Result evaluate(Turn firstTurn, Turn secondTurn) {
        Result result = new Result();
        if (!firstTurn.getMove().equals(secondTurn.getMove())) {
            Optional<Move> move = evaluateOpposingMoves(firstTurn.getMove(), secondTurn.getMove());
            if (move.isPresent()) {
                Move winningMove = move.get();
                if (winningMove.equals(firstTurn.getMove())) {
                    result.setWinner(firstTurn.getPlayer());
                } else if (winningMove.equals(secondTurn.getMove())) {
                    result.setWinner(secondTurn.getPlayer());
                }
            }
        } else {
            result.setTie(true);
        }
        return result;
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

