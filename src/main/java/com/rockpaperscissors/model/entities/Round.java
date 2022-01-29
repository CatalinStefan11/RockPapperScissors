package com.rockpaperscissors.model.entities;

import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.model.actors.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Optional;

import static com.rockpaperscissors.model.gameplay.Evaluator.evaluate;

@Getter
@Entity
@Table(name = "rounds")
@NoArgsConstructor
public class Round {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long roundId;

    @Enumerated(EnumType.STRING)
    private GameState state;

    @OneToOne(
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    @JoinColumn(name = "firstMove")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Turn firstMove;

    @OneToOne(
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    @JoinColumn(name = "secondMove")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Turn secondMove;

    @OneToOne(
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    @MapsId
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Result result;

    public Round(Turn turn) {
        this.firstMove = turn;
        this.state = GameState.PLAYING;
        this.result = new Result();
    }


    public void pushMove(Turn turn) {
        Player currentPlayer = turn.getPlayer();
        if (getFirstMove().getPlayer().equals(currentPlayer)) {
            throw new InvalidOperationException(
                    "The same player cannot play again without the opponent making a move");
        }
        if (previousResultIsTie()) {
            this.result = null;
            this.secondMove = null;
            this.firstMove = turn;
        } else {
            this.secondMove = turn;
            this.result = evaluate(this.firstMove, this.secondMove);
            if (!this.result.isTie()) {
                changeStateTo(GameState.OVER);
            }
        }
    }

    private boolean previousResultIsTie() {
        return this.result != null && this.result.isTie();
    }

    private void changeStateTo(GameState state) {
        if (GameState.OVER.equals(state) && secondMove == null) {
            throw new InvalidOperationException("A Round cannot get OVER after playing only one turn");
        }
        this.state = state;
    }

    public Optional<Result> getResult() {
        return Optional.ofNullable(this.result);
    }

    public enum GameState {
        PLAYING, OVER
    }
}
