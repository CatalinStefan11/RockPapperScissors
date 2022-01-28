package com.rockpaperscissors.model.gameplay;

import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.model.actors.Player;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class Round {

    private GameState state;
    @Getter(AccessLevel.NONE)
    private List<Turn> turns = new ArrayList<>(2);
    private Turn firstPlayerMove;
    private Turn secondPlayerMove;
    private Result result;

    public Round(Turn turn) {
        this.turns.add(turn);
        this.firstPlayerMove = turn;
        this.state = GameState.PLAYING;
    }

    public GameState getState() {
        return state;
    }

    public void pushLatestTurn(Turn turn) {
        Player currentPlayer = turn.getPlayer();
        if (latestTurn().getPlayer().equals(currentPlayer)) {
            throw new InvalidOperationException(
                    "The same player cannot play again without the opponent making a move");
        }
        if (previousResultIsTie()) {
            this.result = null;
            this.secondPlayerMove = null;
            this.firstPlayerMove = turn;
            this.turns.add(turn);
        } else {
            this.secondPlayerMove = turns.get(turns.size() - 1);
            this.firstPlayerMove = turn;
            this.turns.add(turn);
//            this.result = evaluate(this.secondPlayerMove, this.firstPlayerMove);
            if (!this.result.isTie()) {
                changeStateTo(GameState.OVER);
            }
        }
    }

    private boolean previousResultIsTie() {
        return this.result != null && this.result.isTie();
    }

    private void changeStateTo(GameState state) {
        if (GameState.OVER.equals(state) && turns.size() < 2) {
            throw new InvalidOperationException("A Round cannot get OVER after playing only one turn");
        }
        this.state = state;
    }

    public Turn latestTurn() {
        return this.firstPlayerMove;
    }

    public Turn previousTurn() {
        return this.secondPlayerMove;
    }

    public Optional<Result> getResult() {
        return Optional.ofNullable(this.result);
    }

    public enum GameState {
        PLAYING, OVER
    }
}
