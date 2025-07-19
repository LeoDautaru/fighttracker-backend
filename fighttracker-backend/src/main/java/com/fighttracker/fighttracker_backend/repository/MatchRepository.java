package com.fighttracker.fighttracker_backend.repository;

import com.fighttracker.fighttracker_backend.model.Match;
import com.fighttracker.fighttracker_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    // Conta tutti i match di un utente
    int countByUser(User user);

    // Conta tutte le vittorie (case-insensitive su 'WIN')
    @Query("SELECT COUNT(m) FROM Match m WHERE m.user = :user AND UPPER(m.result) = 'WIN'")
    int countWinsByUser(@Param("user") User user);

    // Conta tutte le sconfitte (case-insensitive su 'LOSS')
    @Query("SELECT COUNT(m) FROM Match m WHERE m.user = :user AND UPPER(m.result) = 'LOSS'")
    int countLossesByUser(@Param("user") User user);

    // Recupera tutti i match dell'utente ordinati per data DESC
    List<Match> findByUserIdOrderByDateDesc(Long userId);

    // Query unica per statistiche (totali, vittorie, sconfitte)
    @Query("SELECT COUNT(m), " +
            "SUM(CASE WHEN UPPER(m.result) = 'WIN' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN UPPER(m.result) = 'LOSS' THEN 1 ELSE 0 END) " +
            "FROM Match m WHERE m.user = :user")
    Object[] getStatsByUser(@Param("user") User user);
}
