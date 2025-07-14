package com.fighttracker.fighttracker_backend.controller;

import com.fighttracker.fighttracker_backend.model.Match;
import com.fighttracker.fighttracker_backend.model.User;
import com.fighttracker.fighttracker_backend.repository.MatchRepository;
import com.fighttracker.fighttracker_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getMatches(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("Utente non trovato");

        return ResponseEntity.ok(matchRepository.findByUserId(user.getId()));
    }

    @PostMapping
    public ResponseEntity<?> addMatch(@RequestBody Match match, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("Utente non trovato");

        match.setUser(user);
        return ResponseEntity.ok(matchRepository.save(match));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatch(@PathVariable Long id, @RequestBody Match updatedMatch, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("Utente non trovato");

        Optional<Match> matchOpt = matchRepository.findById(id);
        if (matchOpt.isEmpty()) return ResponseEntity.status(404).body("Match non trovato");

        Match existingMatch = matchOpt.get();
        if (!existingMatch.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Non autorizzato a modificare questo match");
        }

        existingMatch.setOpponent(updatedMatch.getOpponent());
        existingMatch.setResult(updatedMatch.getResult());
        existingMatch.setDate(updatedMatch.getDate());

        return ResponseEntity.ok(matchRepository.save(existingMatch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("Utente non trovato");

        Optional<Match> matchOpt = matchRepository.findById(id);
        if (matchOpt.isEmpty()) return ResponseEntity.status(404).body("Match non trovato");

        Match match = matchOpt.get();
        if (!match.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Non autorizzato a eliminare questo match");
        }

        matchRepository.delete(match);
        return ResponseEntity.ok("Match eliminato");
    }
}
