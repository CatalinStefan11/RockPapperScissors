package com.rockpaperscissors.repository;

import com.rockpaperscissors.model.entities.Result;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> {
}
