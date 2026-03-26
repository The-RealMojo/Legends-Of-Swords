package ui.login;

import db.GameSaveDAO;

import java.util.List;

public class JdbcUserDB implements IUserDB {

    private final GameSaveDAO dao;

    public JdbcUserDB(GameSaveDAO dao) {
        this.dao = dao;
    }

    @Override
    public boolean usernameExists(String username) {
        return dao.getUserIdByUsername(username) != -1;
    }

    @Override
    public void createUser(String username, String password) {
        dao.createUser(username, password);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return dao.authenticate(username, password);
    }

    @Override
    public UserProfile findUsername(String username) {
        int id = dao.getUserIdByUsername(username);
        if (id == -1) {
            return null;
        }

        UserProfile profile = new UserProfile(id, username);
        profile.setScores(dao.getBestScore(id));
        List<String> parties = dao.getSavedParties(id);
        profile.setSavedParties(parties);

        // Load campaign progress from whichever party was saved last
        for (String party : parties) {
            int[] progress = dao.loadCampaignProgress(id, party);
            if (progress != null) {
                profile.setCampaignProgress(progress[0]);
                break;
            }
        }

        return profile;
    }

    @Override
    public boolean hasSavedParty(String username) {
        return dao.hasSavedParty(dao.getUserIdByUsername(username));
    }

    @Override
    public List<String> getSavedParties(String username) {
        return dao.getSavedParties(dao.getUserIdByUsername(username));
    }

    @Override
    public void recordPvpResult(String winner, String loser) {
        dao.recordPvpResult(winner, loser);
    }
}