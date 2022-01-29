package com.rockpaperscissors.service;

import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.dto.PlayRequest;
import com.rockpaperscissors.model.entities.GameSession;
import com.rockpaperscissors.model.entities.Round;
import com.rockpaperscissors.model.entities.Turn;
import com.rockpaperscissors.model.gameplay.Move;
import com.rockpaperscissors.repository.RoundRepository;
import com.rockpaperscissors.repository.TurnRepository;
import org.springframework.stereotype.Service;


import static com.rockpaperscissors.model.entities.Round.GameState.OVER;

@Service
public class GameEngineService {

    private PlayerService playerService;

    private GameSessionService gameSessionService;

    private TurnRepository turnRepository;

    private RoundRepository roundRepository;

    public GameEngineService(PlayerService playerService, GameSessionService gameSessionService,
                             TurnRepository turnRepository, RoundRepository roundRepository) {
        this.playerService = playerService;
        this.gameSessionService = gameSessionService;
        this.turnRepository = turnRepository;
        this.roundRepository = roundRepository;
    }

    public void play(PlayRequest playRequest) {
        GameSession currentSession = gameSessionService.getSession((playRequest.getInviteCode()));
        currentSession.setGameState(GameSession.State.PLAYING);


        playerService.changePlayerState(playRequest.getPlayerName(), Player.PlayerState.PLAYING);
        Player player = playerService.getPlayer(playRequest.getPlayerName());


        final Turn turn = new Turn(player, Enum.valueOf(Move.class, playRequest.getMove()));
        turnRepository.save(turn);

        if (currentSession.rounds().isEmpty()) {
            createNewRound(turn, currentSession);
        } else {
            Round latestRound = currentSession.latestRound();
            Round.GameState gameState = latestRound.getState();

            switch (gameState) {
                case OVER -> createNewRound(turn, currentSession);
                case PLAYING -> pushMoveAndUpdateRound(latestRound, turn);
            }

        }

        Round latestRound = currentSession.latestRound();
        if (latestRound.getState().equals(OVER)) {
            currentSession.setGameState(GameSession.State.WAITING);
            gameSessionService.updateSessionAndLatestRound(currentSession);

            playerService.changePlayerState(currentSession.getFirstPlayer().getPlayerName(), Player.PlayerState.WAITING);
            playerService.changePlayerState(currentSession.getSecondPlayer().getPlayerName(), Player.PlayerState.WAITING);
        }
    }


    private void pushMoveAndUpdateRound(Round actualRound, Turn move) {
        actualRound.pushMove(move);
        roundRepository.saveAndFlush(actualRound);
    }


    private void createNewRound(Turn turn, GameSession gameSession) {
        Round newRound = new Round(turn);
        roundRepository.save(newRound);
        gameSession.addRound(new Round(turn));
        gameSessionService.updateSessionAndLatestRound(gameSession);
    }
}
