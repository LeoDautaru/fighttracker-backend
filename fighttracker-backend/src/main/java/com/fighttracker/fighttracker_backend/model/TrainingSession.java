package com.fighttracker.fighttracker_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "training_sessions")
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'utente associato è obbligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @NotBlank(message = "Il tipo di allenamento è obbligatorio")
    @Size(max = 50, message = "Il tipo di allenamento non può superare 50 caratteri")
    private String type;

    @Min(value = 1, message = "La durata minima è 1 minuto")
    @Max(value = 300, message = "La durata massima è 300 minuti")
    private int duration;

    @Min(value = 1, message = "L'intensità minima è 1")
    @Max(value = 10, message = "L'intensità massima è 10")
    private int intensity;

    @Size(max = 255, message = "Le note non possono superare 255 caratteri")
    private String notes;

    @NotNull(message = "La data e ora della sessione sono obbligatorie")
    private LocalDateTime dateTime;

    public TrainingSession() {}

    public TrainingSession(User user, String type, int duration, int intensity, String notes, LocalDateTime dateTime) {
        this.user = user;
        this.type = type;
        this.duration = duration;
        this.intensity = intensity;
        this.notes = notes;
        this.dateTime = dateTime;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getIntensity() { return intensity; }
    public void setIntensity(int intensity) { this.intensity = intensity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
}
