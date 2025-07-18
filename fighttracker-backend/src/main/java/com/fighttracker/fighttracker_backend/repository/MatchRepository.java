package com.fighttracker.fighttracker_backend.repository;

import com.fighttracker.fighttracker_backend.model.Match;
import com.fighttracker.fighttracker_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    // Conta tutti i match dell'utente
    int countByUser(User user);

    // Conta i match vinti (assumendo che 'result' sia una stringa con "WIN")
    @Query("SELECT COUNT(m) FROM Match m WHERE m.user = :user AND m.result = 'WIN'")
    int countWinsByUser(@Param("user") User user);

    // Conta i match persi (assumendo che 'result' sia una stringa con "LOSS")
    @Query("SELECT COUNT(m) FROM Match m WHERE m.user = :user AND m.result = 'LOSS'")
    int countLossesByUser(@Param("user") User user);

    // Restituisce tutti i match dell'utente ordinati per data decrescente
    List<Match> findByUserIdOrderByDateDesc(Long userId);
}
