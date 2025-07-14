package com.fighttracker.fighttracker_backend.controller;

import com.fighttracker.fighttracker_backend.dto.UserCreateDTO;
import com.fighttracker.fighttracker_backend.dto.UserCreateResponseDTO;
import com.fighttracker.fighttracker_backend.model.User;
import com.fighttracker.fighttracker_backend.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors()
                    .stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce("", (a, b) -> a + b + "; ");
            return ResponseEntity.badRequest().body(errors);
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body("Email già in uso");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            return ResponseEntity.badRequest().body("Username già in uso");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(new UserCreateResponseDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        UserCreateResponseDTO responseDTO = new UserCreateResponseDTO(user.getId(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        User user = userOpt.get();
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail()
        ));
    }

}
