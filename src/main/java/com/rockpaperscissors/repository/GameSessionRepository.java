package com.rockpaperscissors.repository;

import com.rockpaperscissors.model.actors.Player;
import com.rockpaperscissors.model.gameplay.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    @Override
    <S extends GameSession> S save(S entity);

    Optional<GameSession> findByInviteCode(String inviteCode);

}
