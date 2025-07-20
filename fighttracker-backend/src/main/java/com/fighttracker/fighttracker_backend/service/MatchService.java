package com.fighttracker.fighttracker_backend.service;

import com.fighttracker.fighttracker_backend.model.Match;
import com.fighttracker.fighttracker_backend.model.User;
import com.fighttracker.fighttracker_backend.repository.MatchRepository;
import com.fighttracker.fighttracker_backend.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public MatchService(MatchRepository matchRepository, UserRepository userRepository) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<Match> getMatchesByUser(User user) {
        return matchRepository.findByUserIdOrderByDateDesc(user.getId());
    }

    public Optional<Match> getMatchById(Long id) {
        return matchRepository.findById(id);
    }

    public Match addMatch(Match match, User user) {
        match.setUser(user);

        if (match.getResult() != null) {
            match.setResult(match.getResult().trim().toUpperCase());
        }
        return matchRepository.save(match);
    }

    public Optional<Match> updateMatch(Long matchId, Match updatedMatch, User user) {
        Optional<Match> matchOpt = matchRepository.findById(matchId);
        if (matchOpt.isEmpty()) {
            return Optional.empty();
        }

        Match existingMatch = matchOpt.get();

        if (!existingMatch.getUser().getId().equals(user.getId())) {
            logger.warn("Utente {} (id={}) non autorizzato a modificare il match {} (proprietario id={})",
                    user.getEmail(), user.getId(), existingMatch.getId(), existingMatch.getUser().getId());
            throw new SecurityException("Non autorizzato a modificare questo match");
        }

        existingMatch.setOpponent(updatedMatch.getOpponent());
        existingMatch.setResult(
                updatedMatch.getResult() != null ? updatedMatch.getResult().trim().toUpperCase() : null
        );
        existingMatch.setLocation(updatedMatch.getLocation());
        existingMatch.setDate(updatedMatch.getDate());

        return Optional.of(matchRepository.save(existingMatch));
    }

    public boolean deleteMatch(Long matchId, User user) {
        Optional<Match> matchOpt = matchRepository.findById(matchId);
        if (matchOpt.isEmpty()) {
            return false;
        }

        Match match = matchOpt.get();

        if (!match.getUser().getId().equals(user.getId())) {
            logger.warn("Utente {} (id={}) non autorizzato a eliminare il match {} (proprietario id={})",
                    user.getEmail(), user.getId(), match.getId(), match.getUser().getId());
            throw new SecurityException("Non autorizzato a eliminare questo match");
        }

        matchRepository.delete(match);
        return true;
    }
}
