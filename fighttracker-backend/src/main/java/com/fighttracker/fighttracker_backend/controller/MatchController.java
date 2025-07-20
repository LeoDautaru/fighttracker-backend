package com.fighttracker.fighttracker_backend.controller;

import com.fighttracker.fighttracker_backend.model.Match;
import com.fighttracker.fighttracker_backend.model.User;
import com.fighttracker.fighttracker_backend.service.MatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private static final Logger logger = LoggerFactory.getLogger(MatchController.class);

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<?> getMatches(Authentication authentication) {
        String email = authentication.getName();
        logger.info("Richiesta GET /api/matches per utente: {}", email);

        User user = matchService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        List<Match> matches = matchService.getMatchesByUser(user);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMatchById(@PathVariable Long id) {
        Optional<Match> match = matchService.getMatchById(id);
        return match.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> addMatch(@RequestBody Match match, Authentication authentication) {
        String email = authentication.getName();
        logger.info("Richiesta POST /api/matches da utente: {}", email);

        User user = matchService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        if (match.getOpponent() == null || match.getLocation() == null || match.getDate() == null) {
            return ResponseEntity.badRequest().body("Dati del match incompleti");
        }

        Match savedMatch = matchService.addMatch(match, user);
        return ResponseEntity.ok(savedMatch);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatch(
            @PathVariable Long id,
            @RequestBody Match updatedMatch,
            Authentication authentication
    ) {
        String email = authentication.getName();
        logger.info("Richiesta PUT /api/matches/{} da utente: {}", id, email);

        User user = matchService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        try {
            Optional<Match> updated = matchService.updateMatch(id, updatedMatch, user);
            if (updated.isEmpty()) {
                return ResponseEntity.status(404).body("Match non trovato");
            }
            return ResponseEntity.ok(updated.get());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        logger.info("Richiesta DELETE /api/matches/{} da utente: {}", id, email);

        User user = matchService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        try {
            boolean deleted = matchService.deleteMatch(id, user);
            if (!deleted) {
                return ResponseEntity.status(404).body("Match non trovato");
            }
            return ResponseEntity.ok("Match eliminato");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
