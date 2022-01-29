package com.rockpaperscissors.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.model.actors.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @JsonIgnore
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

    @Setter
    private String winner;

    @Setter
    private boolean isTie;

    public Round(Turn turn) {
        this.firstMove = turn;
        this.state = GameState.PLAYING;
    }


    public void pushMove(Turn turn) {
        Player currentPlayer = turn.getPlayer();
        if (getFirstMove().getPlayer().equals(currentPlayer)) {
            throw new InvalidOperationException(
                    "The same player cannot play again without the opponent making a move");
        } else {
            this.secondMove = turn;
            evaluate(this, this.firstMove, this.secondMove);
            changeStateTo(GameState.OVER);

        }
    }


    private void changeStateTo(GameState state) {
        if (GameState.OVER.equals(state) && secondMove == null) {
            throw new InvalidOperationException("A Round cannot get OVER after playing only one turn");
        }
        this.state = state;
    }

    public enum GameState {
        PLAYING, OVER
    }
}
