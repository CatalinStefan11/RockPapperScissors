package com.rockpaperscissors.model.actors;

import com.rockpaperscissors.model.gameplay.GameSession;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

@Getter
@EqualsAndHashCode
@Table(name = "players")
@Entity
public class Player {

    @Id
    @GeneratedValue
    private long id;

    private String playerName;

    @Setter
    @Enumerated(EnumType.STRING)
    private PlayerState currentState;

    public Player(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name of a player cannot be null!");
        }
        this.playerName = name;
        this.currentState = PlayerState.WAITING;
    }

    public Player() {

    }

    public enum PlayerState {
        WAITING, PLAYING, READY;

        @Override
        public String toString() {
            return switch (this) {
                case READY -> "READY";
                case PLAYING -> "PLAYING";
                case WAITING -> "WAITING";
                default -> throw new IllegalArgumentException("Unrecognized player state");
            };
        }
    }

}
