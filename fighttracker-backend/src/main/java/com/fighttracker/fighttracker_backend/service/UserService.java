package com.fighttracker.fighttracker_backend.service;

import com.fighttracker.fighttracker_backend.model.User;
import com.fighttracker.fighttracker_backend.repository.MatchRepository;
import com.fighttracker.fighttracker_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    // Constructor injection
    public UserService(UserRepository userRepository, MatchRepository matchRepository) {
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Recupera le statistiche dell'utente:
     * - Totale combattimenti
     * - Vittorie
     * - Sconfitte
     */
    public Map<String, Integer> getUserStats(User user) {
        List<Object[]> statsList = matchRepository.getStatsByUser(user);
        if (statsList.isEmpty()) {
            // Nessun dato
            return Map.of("matchesCount", 0, "wins", 0, "losses", 0);
        }
        Object[] stats = statsList.get(0);
        int total = ((Number) stats[0]).intValue();
        int wins = ((Number) stats[1]).intValue();
        int losses = ((Number) stats[2]).intValue();

        Map<String, Integer> result = new HashMap<>();
        result.put("matchesCount", total);
        result.put("wins", wins);
        result.put("losses", losses);
        return result;
    }

}
