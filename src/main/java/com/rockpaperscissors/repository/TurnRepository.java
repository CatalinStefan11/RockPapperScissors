package com.rockpaperscissors.repository;

import com.rockpaperscissors.model.entities.Turn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnRepository extends JpaRepository<Turn, Long> {
}
