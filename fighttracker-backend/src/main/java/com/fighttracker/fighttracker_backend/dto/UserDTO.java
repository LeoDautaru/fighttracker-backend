package com.fighttracker.fighttracker_backend.dto;

import com.fighttracker.fighttracker_backend.model.User;

public class UserDTO {
    private Long id;
    private String username;
    private String email;

    public UserDTO() {}

    public UserDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
