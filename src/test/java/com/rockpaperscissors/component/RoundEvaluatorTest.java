package com.rockpaperscissors.component;

import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.entities.Round;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.model.gameplay.Move;
import com.rockpaperscissors.utils.RoundEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RoundEvaluatorTest {

    RoundEvaluator roundEvaluator;

    @BeforeEach
    void init(){
        roundEvaluator = new RoundEvaluator();
    }

    @Test
    void testRoundEvaluator_FirstPlayerRock_SecondPaper_WinnerSecond(){

        Player player1 = new Player("Catalin");
        player1.setCurrentState(Player.PlayerState.PLAYING);
        Player player2 = new Player("Messi");
        player2.setCurrentState(Player.PlayerState.PLAYING);

        Turn turnFirstPlayer = new Turn(player1, Move.ROCK);
        Turn turnSecondPlayer = new Turn(player2, Move.PAPER);

        Round round = new Round(turnFirstPlayer);
        round.setSecondMove(turnSecondPlayer);

        roundEvaluator.evaluate(round);

        Assertions.assertEquals(player2.getPlayerName(), round.getWinner());

    }

    @Test
    void testRoundEvaluator_FirstPlayerPaper_SecondScissors_WinnerSecond(){

        Player player1 = new Player("Catalin");
        player1.setCurrentState(Player.PlayerState.PLAYING);
        Player player2 = new Player("Messi");
        player2.setCurrentState(Player.PlayerState.PLAYING);

        Turn turnFirstPlayer = new Turn(player1, Move.PAPER);
        Turn turnSecondPlayer = new Turn(player2, Move.SCISSORS);

        Round round = new Round(turnFirstPlayer);
        round.setSecondMove(turnSecondPlayer);

        roundEvaluator.evaluate(round);

        Assertions.assertEquals(player2.getPlayerName(), round.getWinner());
    }

    @Test
    void testRoundEvaluator_FirstPlayerRock_SecondScissors_WinnerFirst(){

        Player player1 = new Player("Catalin");
        player1.setCurrentState(Player.PlayerState.PLAYING);
        Player player2 = new Player("Messi");
        player2.setCurrentState(Player.PlayerState.PLAYING);

        Turn turnFirstPlayer = new Turn(player1, Move.ROCK);
        Turn turnSecondPlayer = new Turn(player2, Move.SCISSORS);

        Round round = new Round(turnFirstPlayer);
        round.setSecondMove(turnSecondPlayer);

        roundEvaluator.evaluate(round);

        Assertions.assertEquals(player1.getPlayerName(), round.getWinner());
    }

    @Test
    void testRoundEvaluator_FirstPlayerScissors_SecondRock_WinnerSecond(){

        Player player1 = new Player("Catalin");
        player1.setCurrentState(Player.PlayerState.PLAYING);
        Player player2 = new Player("Messi");
        player2.setCurrentState(Player.PlayerState.PLAYING);

        Turn turnFirstPlayer = new Turn(player1, Move.SCISSORS);
        Turn turnSecondPlayer = new Turn(player2, Move.ROCK);

        Round round = new Round(turnFirstPlayer);
        round.setSecondMove(turnSecondPlayer);

        roundEvaluator.evaluate(round);

        Assertions.assertEquals(player2.getPlayerName(), round.getWinner());
    }

    @Test
    void testRoundEvaluator_FirstPlayerPaper_SecondPaper_isTieTrue(){

        Player player1 = new Player("Catalin");
        player1.setCurrentState(Player.PlayerState.PLAYING);
        Player player2 = new Player("Messi");
        player2.setCurrentState(Player.PlayerState.PLAYING);

        Turn turnFirstPlayer = new Turn(player1, Move.PAPER);
        Turn turnSecondPlayer = new Turn(player2, Move.PAPER);

        Round round = new Round(turnFirstPlayer);
        round.setSecondMove(turnSecondPlayer);

        roundEvaluator.evaluate(round);

        Assertions.assertTrue(round.isTie());
        Assertions.assertNull(round.getWinner());
    }

}
