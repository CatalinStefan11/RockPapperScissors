package com.rockpaperscissors.repository;

import com.rockpaperscissors.model.entities.Round;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Long> {
}
