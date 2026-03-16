package ProfileCreationSystem;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    private String userId;
    private String username;
    private String password;
    private int scores;
    private int rankings;
    private int campaignProgress;
    private List<String> savedParties;

    public UserProfile() {
        savedParties = new ArrayList<>();
    }

    public UserProfile(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.scores = 0;
        this.rankings = 0;
        this.campaignProgress = 0;
        this.savedParties = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScores() {
        return scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }

    public int getRankings() {
        return rankings;
    }

    public void setRankings(int rankings) {
        this.rankings = rankings;
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

    public void addSavedParty(String partyName) {
        savedParties.add(partyName);
    }
}
