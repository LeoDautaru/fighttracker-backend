package com.fighttracker.fighttracker_backend.service;

import com.fighttracker.fighttracker_backend.model.TrainingSession;
import com.fighttracker.fighttracker_backend.repository.TrainingSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;

    public TrainingSessionService(TrainingSessionRepository trainingSessionRepository) {
        this.trainingSessionRepository = trainingSessionRepository;
    }

    public TrainingSession saveSession(TrainingSession session) {
        return trainingSessionRepository.save(session);
    }

    public List<TrainingSession> findAllSessions() {
        return trainingSessionRepository.findAll();
    }

    public Optional<TrainingSession> findById(Long id) {
        return trainingSessionRepository.findById(id);
    }

    public void deleteSession(Long id) {
        trainingSessionRepository.deleteById(id);
    }

    // Esempio di logica business: trovare sessioni per utente
    public List<TrainingSession> findByUserId(Long userId) {
        return trainingSessionRepository.findByUserId(userId);
    }
}
