package com.fighttracker.fighttracker_backend.controller;

import com.fighttracker.fighttracker_backend.model.Match;
import com.fighttracker.fighttracker_backend.model.User;
import com.fighttracker.fighttracker_backend.repository.MatchRepository;
import com.fighttracker.fighttracker_backend.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private static final Logger logger = LoggerFactory.getLogger(MatchController.class);

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    // Recupera tutti i match dell'utente loggato
    @GetMapping
    public ResponseEntity<?> getMatches(Authentication authentication) {
        String email = authentication.getName();
        logger.info("Richiesta GET /api/matches per utente: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            logger.error("Utente con email {} non trovato", email);
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        List<Match> matches = matchRepository.findByUserIdOrderByDateDesc(user.getId());
        logger.info("Trovati {} match per utente {}", matches.size(), email);
        return ResponseEntity.ok(matches);
    }




    // Aggiunge un nuovo match
    @PostMapping
    public ResponseEntity<?> addMatch(@RequestBody Match match, Authentication authentication) {
        String email = authentication.getName();
        logger.info("Richiesta POST /api/matches da utente: {}", email);
        logger.debug("Dati match ricevuti: {}", match);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            logger.error("Utente con email {} non trovato", email);
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        if (match.getOpponent() == null || match.getLocation() == null || match.getDate() == null) {
            logger.error("Dati del match incompleti: {}", match);
            return ResponseEntity.badRequest().body("Dati del match incompleti");
        }

        // Normalizzazione del result
        if (match.getResult() != null) {
            match.setResult(match.getResult().trim().toUpperCase());
        }

        match.setUser(user);

        Match savedMatch = matchRepository.save(match);
        logger.info("Match salvato correttamente con ID: {}", savedMatch.getId());

        return ResponseEntity.ok(savedMatch);
    }

    // Aggiorna un match esistente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatch(
            @PathVariable Long id,
            @RequestBody Match updatedMatch,
            Authentication authentication
    ) {
        String email = authentication.getName();
        logger.info("Richiesta PUT /api/matches/{} da utente: {}", id, email);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        Optional<Match> matchOpt = matchRepository.findById(id);
        if (matchOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Match non trovato");
        }

        Match existingMatch = matchOpt.get();
        if (!existingMatch.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Non autorizzato a modificare questo match");
        }

        existingMatch.setOpponent(updatedMatch.getOpponent());
        existingMatch.setResult(
                updatedMatch.getResult() != null ? updatedMatch.getResult().trim().toUpperCase() : null
        );
        existingMatch.setLocation(updatedMatch.getLocation());
        existingMatch.setDate(updatedMatch.getDate());

        Match savedMatch = matchRepository.save(existingMatch);
        logger.info("Match con ID {} aggiornato correttamente", savedMatch.getId());
        return ResponseEntity.ok(savedMatch);
    }

    // Elimina un match
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        logger.info("Richiesta DELETE /api/matches/{} da utente: {}", id, email);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        Optional<Match> matchOpt = matchRepository.findById(id);
        if (matchOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Match non trovato");
        }

        Match match = matchOpt.get();
        if (!match.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Non autorizzato a eliminare questo match");
        }

        matchRepository.delete(match);
        logger.info("Match con ID {} eliminato correttamente", id);
        return ResponseEntity.ok("Match eliminato");
    }
}
