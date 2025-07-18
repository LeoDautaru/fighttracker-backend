package com.fighttracker.fighttracker_backend.dto;

public class UserProfileResponse {
    private String username;
    private String profilePictureUrl;
    private int matchesCount;
    private int wins;
    private int losses;

    public UserProfileResponse(String username, String profilePictureUrl, int matchesCount, int wins, int losses) {
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
        this.matchesCount = matchesCount;
        this.wins = wins;
        this.losses = losses;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public int getMatchesCount() {
        return matchesCount;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }
}
