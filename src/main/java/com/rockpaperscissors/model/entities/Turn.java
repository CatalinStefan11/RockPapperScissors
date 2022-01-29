package com.rockpaperscissors.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.gameplay.Move;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "turns")
@NoArgsConstructor
public class Turn {

    @Id
    @GeneratedValue
    @Column(name = "id")
    @JsonIgnore
    private Long turnId;

    @OneToOne(
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    @JoinColumn(name = "player")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Player player;

    @Enumerated(EnumType.STRING)
    private Move move;

    public Turn(Player player, Move move) {
        if (player == null || move == null) {
            throw new InvalidOperationException("A turn cannot be played without a Player & a Move");
        }
        this.player = player;
        this.move = move;
    }

}