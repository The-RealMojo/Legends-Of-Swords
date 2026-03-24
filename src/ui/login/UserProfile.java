package ui.login;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private int userId;
    private String username;
    private int scores;
    private int campaignProgress;
    private List<String> savedParties;

    public UserProfile(int userId, String username) {
        this.userId = userId;
        this.username = username;
        this.scores = 0;
        this.campaignProgress = 0;
        this.savedParties = new ArrayList<>();
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getScores() {
        return scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }

    public int getCampaignProgress() {
        return campaignProgress;
    }

    public void setCampaignProgress(int campaignProgress) {
        this.campaignProgress = campaignProgress;
    }

    public List<String> getSavedParties() {
        return savedParties;
    }

    public void setSavedParties(List<String> savedParties) {
        this.savedParties = savedParties;
    }
}