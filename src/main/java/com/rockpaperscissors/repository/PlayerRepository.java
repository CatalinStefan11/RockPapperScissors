package com.rockpaperscissors.repository;

import com.rockpaperscissors.model.actors.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Override
    <S extends Player> S save(S entity);

    @Override
    List<Player> findAll();

    @Override
    Player getById(Long id);

    @Override
    Optional<Player> findById(Long id);

    @Override
    void delete(Player entity);

    Optional<Player> findByPlayerName(String playerName);

    @Modifying
    @Transactional
    @Query("update Player p set p.currentState = :state where p.id = :id")
    void changePlayerState(Long id, Player.PlayerState state);


}
