package com.fighttracker.fighttracker_backend.dto;

import com.fighttracker.fighttracker_backend.model.TrainingSession;

import java.time.LocalDateTime;

public class TrainingSessionDTO {

    private Long id;
    private String type;
    private int duration;
    private int intensity;
    private String notes;
    private LocalDateTime dateTime;
    private Long userId;
    private String username;

    public TrainingSessionDTO() {}

    public TrainingSessionDTO(TrainingSession session) {
        this.id = session.getId();
        this.type = session.getType();
        this.duration = session.getDuration();
        this.intensity = session.getIntensity();
        this.notes = session.getNotes();
        this.dateTime = session.getDateTime();

        if (session.getUser() != null) {
            this.userId = session.getUser().getId();
            this.username = session.getUser().getUsername();
        }
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
