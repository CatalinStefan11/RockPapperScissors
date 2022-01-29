package com.rockpaperscissors.model.entities;

import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.gameplay.Invite;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "game_sessions")
@Getter
@NoArgsConstructor
public class GameSession {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long sessionId;

    private String inviteCode;

    @OneToOne(
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "firstPlayer")
    private Player firstPlayer;

    @OneToOne(
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    @JoinColumn(name = "secondPlayer")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Player secondPlayer;

    @Enumerated(EnumType.STRING)
    private State gameState;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "gameSession")
    private List<Round> rounds;

    @OneToOne(
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    @JoinColumn(name = "winner")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Player winner;

    private boolean tie;


    public GameSession(Invite invite) {
        this.inviteCode = invite.getInviteCode();
        this.firstPlayer = invite.getPlayer();
        this.gameState = State.WAITING;
        this.rounds = new ArrayList<>(1);
    }


    public void changeStateTo(State state) {
        this.gameState = state;
    }

    public void addOpponent(Player player) {
        this.secondPlayer = player;
    }

    public List<Round> rounds() {
        return Collections.unmodifiableList(rounds);
    }

    public void addRound(Round round) {
        rounds.add(round);
    }


    public Round latestRound() {
        if (rounds.isEmpty()) {
            throw new InvalidOperationException("No rounds have been started yet");
        }
        return rounds.get(rounds.size() - 1);
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public void setTie(boolean tie) {
        this.tie = tie;
    }


    public enum State {
        WAITING, ACCEPTED, PLAYING
    }

}
