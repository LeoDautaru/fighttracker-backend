package com.fighttracker.fighttracker_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class MatchDTO {
    private Long id;
    private String opponent;
    private String location;

    @JsonFormat(pattern = "yyyy-MM-dd")  // Per compatibilità con input date del front-end
    private LocalDate date;

    private String result;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
