package com.rockpaperscissors.repository;

import com.rockpaperscissors.model.entities.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    @Override
    <S extends GameSession> S save(S entity);

    Optional<GameSession> findByInviteCode(String inviteCode);

}
