package com.fighttracker.fighttracker_backend.controller;

import com.fighttracker.fighttracker_backend.model.TrainingSession;
import com.fighttracker.fighttracker_backend.service.TrainingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-sessions")
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    public TrainingSessionController(TrainingSessionService trainingSessionService) {
        this.trainingSessionService = trainingSessionService;
    }

    @PostMapping
    public ResponseEntity<TrainingSession> createSession(@Valid @RequestBody TrainingSession session) {
        TrainingSession savedSession = trainingSessionService.saveSession(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSession);
    }

    @GetMapping
    public List<TrainingSession> getAllSessions() {
        return trainingSessionService.findAllSessions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingSession> getSessionById(@PathVariable Long id) {
        return trainingSessionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<TrainingSession> getSessionsByUser(@PathVariable Long userId) {
        return trainingSessionService.findByUserId(userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingSession> updateSession(@PathVariable Long id, @Valid @RequestBody TrainingSession session) {
        return trainingSessionService.findById(id)
                .map(existingSession -> {
                    session.setId(existingSession.getId());
                    TrainingSession updatedSession = trainingSessionService.saveSession(session);
                    return ResponseEntity.ok(updatedSession);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        if (trainingSessionService.findById(id).isPresent()) {
            trainingSessionService.deleteSession(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
