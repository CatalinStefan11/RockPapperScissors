package com.rockpaperscissors.model.entities;

import com.rockpaperscissors.model.actors.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "results")
@NoArgsConstructor
public class Result {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long resultId;

    @OneToOne(
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    @JoinColumn(name = "winner")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Player winner;

    private boolean tie;
}
