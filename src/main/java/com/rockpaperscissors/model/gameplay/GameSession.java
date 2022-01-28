package com.rockpaperscissors.model.gameplay;

import com.rockpaperscissors.model.actors.Player;

import javax.persistence.*;

@Entity
@Table(name = "game_session")
public class GameSession {

    @Id
    @GeneratedValue
    private long id;

    private String inviteCode;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firstPlayer")
    private Player firstPlayer;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "secondPlayer")
    private Player secondPlayer;

    @Enumerated(EnumType.STRING)
    private State gameState;

    //    private List<Round> rounds;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Player winner;

    private boolean tie;


    public GameSession(Invite invite) {
        this.inviteCode = invite.getInviteCode();
        this.firstPlayer = invite.getPlayer();
        this.gameState = State.WAITING;
//        this.rounds = new ArrayList<>(1);
    }

    public GameSession() {

    }

    public State state() {
        return this.gameState;
    }

    public String getInviteCode() {
        return this.inviteCode;
    }

    public long getSessionId() {
        return this.id;
    }

    public void changeStateTo(State state) {
        this.gameState = state;
    }

    public Player getFirstPlayer() {
        return this.firstPlayer;
    }

    public Player getSecondPlayer() {
        return this.secondPlayer;
    }

    public State getGameState() {
        return gameState;
    }

    public void addOpponent(Player player) {
        this.secondPlayer = player;
    }

//    public List<Round> rounds() {
//        return Collections.unmodifiableList(rounds);
//    }
//
//    public void addRound(Round round) {
//        rounds.add(round);
//    }
//
//    public Round latestRound()  {
//        if (rounds.isEmpty()) {
//            throw new InvalidOperationException("No rounds have been started yet");
//        }
//        return rounds.get(rounds.size() - 1);
//    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean isTie() {
        return tie;
    }

    public void setTie(boolean tie) {
        this.tie = tie;
    }


    public enum State {
        WAITING, ACCEPTED, PLAYING
    }

}
