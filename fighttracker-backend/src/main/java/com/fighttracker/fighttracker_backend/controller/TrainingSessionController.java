package com.fighttracker.fighttracker_backend.controller;

import com.fighttracker.fighttracker_backend.dto.TrainingSessionCreateDTO;
import com.fighttracker.fighttracker_backend.dto.TrainingSessionDTO;
import com.fighttracker.fighttracker_backend.model.TrainingSession;
import com.fighttracker.fighttracker_backend.model.User;
import com.fighttracker.fighttracker_backend.repository.TrainingSessionRepository;
import com.fighttracker.fighttracker_backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;


@RestController
@RequestMapping("/api/training-sessions")
public class TrainingSessionController {

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<TrainingSessionDTO> getAllSessions() {
        return trainingSessionRepository.findAll().stream()
                .map(TrainingSessionDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSessionById(@PathVariable Long id) {
        return trainingSessionRepository.findById(id)
                .map(session -> ResponseEntity.ok(new TrainingSessionDTO(session)))
                .orElse(ResponseEntity.notFound().build());
    }



    @PostMapping
    public ResponseEntity<?> createSession(
            @Valid @RequestBody TrainingSessionCreateDTO dto,
            BindingResult bindingResult,
            @RequestHeader("Authorization") String authorizationHeader) {

        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors()
                    .stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(errors);
        }

        String username = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        if (username == null) {
            return ResponseEntity.status(401).body("Utente non autenticato");
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.badRequest().body("Utente non trovato con username: " + username);
        }
        User user = optionalUser.get();

        TrainingSession session = new TrainingSession();
        session.setDateTime(dto.getDateTime());
        session.setType(dto.getType());
        session.setNotes(dto.getNotes());
        session.setDuration(dto.getDuration());
        session.setIntensity(dto.getIntensity());
        session.setUser(user);

        TrainingSession saved = trainingSessionRepository.save(session);

        return ResponseEntity.ok(new TrainingSessionDTO(saved));
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> updateSession(@PathVariable Long id, @Valid @RequestBody TrainingSessionCreateDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors()
                    .stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(errors);
        }

        return trainingSessionRepository.findById(id).map(session -> {
            User user = userRepository.findById(dto.getUserId()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("Utente non trovato con ID: " + dto.getUserId());
            }

            session.setDateTime(dto.getDateTime());
            session.setType(dto.getType());
            session.setNotes(dto.getNotes());
            session.setDuration(dto.getDuration());
            session.setIntensity(dto.getIntensity());
            session.setUser(user);

            TrainingSession updated = trainingSessionRepository.save(session);
            return ResponseEntity.ok(new TrainingSessionDTO(updated));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSession(@PathVariable Long id) {
        return trainingSessionRepository.findById(id).map(session -> {
            trainingSessionRepository.delete(session);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
