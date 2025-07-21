package com.fighttracker.fighttracker_backend.service;

import com.fighttracker.fighttracker_backend.model.RefreshToken;
import com.fighttracker.fighttracker_backend.model.User;
import com.fighttracker.fighttracker_backend.repository.RefreshTokenRepository;
import com.fighttracker.fighttracker_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trovato con id: " + userId));

        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(refreshTokenDurationMs);

        if (existingTokenOpt.isPresent()) {
            RefreshToken existingToken = existingTokenOpt.get();
            existingToken.setToken(token);
            existingToken.setExpiryDate(expiryDate);
            return refreshTokenRepository.save(existingToken);
        } else {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(token);
            refreshToken.setExpiryDate(expiryDate);
            return refreshTokenRepository.save(refreshToken);
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public void deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trovato con id: " + userId));
        refreshTokenRepository.deleteByUser(user);
    }
}
