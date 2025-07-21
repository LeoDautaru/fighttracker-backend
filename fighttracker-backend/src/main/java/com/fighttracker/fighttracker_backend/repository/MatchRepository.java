package com.fighttracker.fighttracker_backend.repository;

import com.fighttracker.fighttracker_backend.model.Match;
import com.fighttracker.fighttracker_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    int countByUser(User user);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.user = :user AND UPPER(m.result) = 'WIN'")
    int countWinsByUser(@Param("user") User user);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.user = :user AND UPPER(m.result) = 'LOSS'")
    int countLossesByUser(@Param("user") User user);

    List<Match> findByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT COUNT(m), " +
            "COALESCE(SUM(CASE WHEN UPPER(m.result) = 'WIN' THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN UPPER(m.result) = 'LOSS' THEN 1 ELSE 0 END), 0) " +
            "FROM Match m WHERE m.user = :user")
    List<Object[]> getStatsByUser(@Param("user") User user);


}
