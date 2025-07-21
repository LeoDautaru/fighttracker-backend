package com.fighttracker.fighttracker_backend.controller;

import com.fighttracker.fighttracker_backend.dto.UserCreateDTO;
import com.fighttracker.fighttracker_backend.dto.UserCreateResponseDTO;
import com.fighttracker.fighttracker_backend.dto.UserProfileResponse;
import com.fighttracker.fighttracker_backend.model.User;
import com.fighttracker.fighttracker_backend.repository.UserRepository;
import com.fighttracker.fighttracker_backend.repository.MatchRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email già in uso"));
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username già in uso"));
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(
                new UserCreateResponseDTO(
                        savedUser.getId(),
                        savedUser.getUsername(),
                        savedUser.getEmail()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(
                        new UserCreateResponseDTO(user.getId(), user.getUsername(), user.getEmail())
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        User user = userOpt.get();

        List<Object[]> statsList = matchRepository.getStatsByUser(user);

        Object[] stats;
        if (statsList.isEmpty()) {
            stats = new Object[]{0L, 0L, 0L};
        } else {
            stats = statsList.get(0);
        }

        int totalMatches = ((Number) stats[0]).intValue();
        int wins = ((Number) stats[1]).intValue();
        int losses = ((Number) stats[2]).intValue();

        UserProfileResponse response = new UserProfileResponse(
                user.getUsername(),
                user.getProfilePictureUrl(),
                totalMatches,
                wins,
                losses
        );

        return ResponseEntity.ok(response);
    }


}
