package com.fighttracker.fighttracker_backend.controller;

import com.fighttracker.fighttracker_backend.dto.UserProfileUpdateDTO;
import com.fighttracker.fighttracker_backend.model.User;
import com.fighttracker.fighttracker_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String email = authentication.getName(); // Dal token JWT
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        return ResponseEntity.ok(user);
    }

    @PutMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> updateProfile(
            @RequestPart("profile") UserProfileUpdateDTO profileDTO,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        System.out.println("DTO ricevuto: " + profileDTO.getUsername() + ", " + profileDTO.getEmail());

        // Aggiorna i dati
        if (profileDTO.getUsername() != null) {
            user.setUsername(profileDTO.getUsername());
        }

        if (profileDTO.getEmail() != null) {
            user.setEmail(profileDTO.getEmail());
        }

        if (profileDTO.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(profileDTO.getProfilePictureUrl());
        }

        // Upload file se presente
        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = "uploads/profile-pics/";
                Files.createDirectories(Paths.get(uploadDir));

                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filepath = Paths.get(uploadDir, filename);

                file.transferTo(filepath);

                String fileUrl = "/uploads/profile-pics/" + filename;
                user.setProfilePictureUrl(fileUrl);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Errore nel salvataggio del file");
            }
        }

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File vuoto");
        }

        try {
            String uploadDir = "uploads/profile-pics/";
            Files.createDirectories(Paths.get(uploadDir));

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filepath = Paths.get(uploadDir, filename);

            file.transferTo(filepath);

            String fileUrl = "/uploads/profile-pics/" + filename;
            user.setProfilePictureUrl(fileUrl);
            userRepository.save(user);

            return ResponseEntity.ok(user);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Errore nel salvataggio del file");
        }
    }
}
