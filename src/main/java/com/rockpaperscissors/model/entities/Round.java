package com.rockpaperscissors.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

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

    public void changeStateTo(GameState state) {
        if (GameState.OVER.equals(state) && secondMove == null) {
            throw new InvalidOperationException("A Round cannot get OVER after playing only one turn");
        }
        this.state = state;
    }

    public void setSecondMove(Turn turn) {
        this.secondMove = turn;
    }

    public enum GameState {
        PLAYING, OVER
    }
}
