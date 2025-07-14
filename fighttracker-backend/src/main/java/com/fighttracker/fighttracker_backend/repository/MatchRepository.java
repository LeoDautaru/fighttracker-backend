package com.fighttracker.fighttracker_backend.repository;

import com.fighttracker.fighttracker_backend.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByUserId(Long userId);
}
