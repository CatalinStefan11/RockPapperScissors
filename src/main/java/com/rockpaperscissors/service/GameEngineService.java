package com.rockpaperscissors.service;

import com.rockpaperscissors.exception.customexceptions.GameException;
import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.dto.PlayRequest;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.entities.Result;
import com.rockpaperscissors.model.entities.Round;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.model.gameplay.Move;
import com.rockpaperscissors.repository.ResultRepository;
import com.rockpaperscissors.repository.RoundRepository;
import com.rockpaperscissors.repository.TurnRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.rockpaperscissors.model.entities.Round.GameState.OVER;
import static com.rockpaperscissors.model.entities.Round.GameState.PLAYING;

@Service
public class GameEngineService {

    private PlayerService playerService;

    private GameSessionService gameSessionService;

    private TurnRepository turnRepository;

    private ResultRepository resultRepository;

    private RoundRepository roundRepository;

    public GameEngineService(PlayerService playerService, GameSessionService gameSessionService,
                             TurnRepository turnRepository, ResultRepository resultRepository,
                             RoundRepository roundRepository) {
        this.playerService = playerService;
        this.gameSessionService = gameSessionService;
        this.turnRepository = turnRepository;
        this.resultRepository = resultRepository;
        this.roundRepository = roundRepository;
    }

    public void play(PlayRequest playRequest) {
        GameSession currentSession = gameSessionService.getSession((playRequest.getInviteCode()));

        //status playing is changed 2 times
        currentSession.changeStateTo(GameSession.State.PLAYING);

        playerService.changePlayerState(playRequest.getPlayerName(), Player.PlayerState.PLAYING);
        Player player = playerService.getPlayer(playRequest.getPlayerName());

        final Turn turn = new Turn(player, Enum.valueOf(Move.class, playRequest.getMove()));
        turnRepository.save(turn);



        if (currentSession.rounds().isEmpty()) {
            createNewRound(turn, currentSession);
        } else {
            Round latestRound = currentSession.latestRound();
            Round.GameState gameState = latestRound.getState();

            switch (gameState){
                case OVER -> createNewRound(turn, currentSession);
                case PLAYING -> pushMoveAndUpdateRound(latestRound, turn);
            }

        }

        Round latestRound = currentSession.latestRound();
        if (latestRound.getState().equals(OVER)) {
            Optional<Result> resultOptional = latestRound.getResult();
            if (resultOptional.isPresent()) {
                currentSession.changeStateTo(GameSession.State.WAITING);
                Result result = resultOptional.get();
                if (result.isTie()) {
                    currentSession.setTie(true);
                } else {
                    currentSession.setWinner(result.getWinner());
                }
                resultRepository.save(result);
                gameSessionService.saveSession(currentSession);


                currentSession.getFirstPlayer().setCurrentState(Player.PlayerState.WAITING);
                playerService.changePlayerState(currentSession.getFirstPlayer().getPlayerName(),
                        currentSession.getFirstPlayer().getCurrentState());
                currentSession.getSecondPlayer().setCurrentState(Player.PlayerState.WAITING);
                playerService.changePlayerState(currentSession.getSecondPlayer().getPlayerName(),
                        currentSession.getSecondPlayer().getCurrentState());
            }
        }


    }


    private void pushMoveAndUpdateRound(Round actualRound, Turn move){
        actualRound.pushMove(move);
        roundRepository.saveAndFlush(actualRound);
    }


    private void createNewRound(Turn turn, GameSession gameSession) {
        Round newRound = new Round(turn);
        roundRepository.save(newRound);
        gameSession.addRound(new Round(turn));
        gameSessionService.saveSession(gameSession);
    }
}
