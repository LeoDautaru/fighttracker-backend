package com.fighttracker.fighttracker_backend.controller;

import com.fighttracker.fighttracker_backend.dto.AuthRequest;
import com.fighttracker.fighttracker_backend.dto.AuthResponse;
import com.fighttracker.fighttracker_backend.dto.UserCreateDTO;
import com.fighttracker.fighttracker_backend.dto.UserCreateResponseDTO;
import com.fighttracker.fighttracker_backend.model.User;
import com.fighttracker.fighttracker_backend.model.RefreshToken;
import com.fighttracker.fighttracker_backend.repository.UserRepository;
import com.fighttracker.fighttracker_backend.service.CustomUserDetailsService;
import com.fighttracker.fighttracker_backend.service.RefreshTokenService;
import com.fighttracker.fighttracker_backend.util.JwtUtil;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    // --- REGISTRAZIONE ---
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email già utilizzata");
        }

        if (userRepository.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Username già utilizzato");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(new UserCreateResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        ));
    }

    // --- LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Email o password errati");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        // Crea un refresh token per l'utente
        User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return ResponseEntity.ok(new AuthResponse(jwt, refreshToken.getToken()));
    }

    // --- REFRESH TOKEN ---
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String requestToken = request.get("refreshToken");

        return refreshTokenService.findByToken(requestToken)
                .map(token -> {
                    if (refreshTokenService.isExpired(token)) {
                        refreshTokenService.deleteByUserId(token.getUser().getId());
                        return ResponseEntity.status(403).body("Refresh token scaduto");
                    }

                    String jwt = jwtUtil.generateToken(
                            userDetailsService.loadUserByUsername(token.getUser().getEmail()));
                    return ResponseEntity.ok(Map.of("accessToken", jwt));
                })
                .orElse(ResponseEntity.status(403).body("Refresh token non valido"));
    }
}
