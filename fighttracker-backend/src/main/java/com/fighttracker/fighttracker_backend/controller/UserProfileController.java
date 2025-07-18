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

    // Percorso assoluto per Windows
    private static final String UPLOAD_DIR = "C:/Users/Mauro lella cici/Documents/GitHub/fighttracker-backend/fighttracker-backend/uploads/profile-pics/";

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

        System.out.println("Aggiornamento profilo: " + profileDTO.getUsername() + ", " + profileDTO.getEmail());

        if (profileDTO.getUsername() != null) {
            user.setUsername(profileDTO.getUsername());
        }

        if (profileDTO.getEmail() != null) {
            user.setEmail(profileDTO.getEmail());
        }

        // Salvataggio file immagine
        if (file != null && !file.isEmpty()) {
            try {
                Files.createDirectories(Paths.get(UPLOAD_DIR));

                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filepath = Paths.get(UPLOAD_DIR, filename);

                file.transferTo(filepath.toFile());

                String fileUrl = "/uploads/profile-pics/" + filename;
                user.setProfilePictureUrl(fileUrl);

                System.out.println("Immagine caricata: " + fileUrl);

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
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filepath = Paths.get(UPLOAD_DIR, filename);

            file.transferTo(filepath.toFile());

            String fileUrl = "/uploads/profile-pics/" + filename;
            user.setProfilePictureUrl(fileUrl);
            userRepository.save(user);

            System.out.println("Avatar aggiornato: " + fileUrl);

            return ResponseEntity.ok(user);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Errore nel salvataggio del file");
        }
    }
}
