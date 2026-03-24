package ProfileCreationSystem;

import database.GameSaveDAO;
import java.util.List;

public class JdbcUserDB implements IUserDB {

    private GameSaveDAO dao;

    public JdbcUserDB(GameSaveDAO dao) {
        this.dao = dao;
    }

    @Override
    public boolean usernameExists(String username) {
        return dao.getUserIdByUsername(username) != -1;
    }

    @Override
    public void save(UserProfile user) {
        dao.createUser(user.getUsername(), user.getPassword());
    }

    @Override
    public UserProfile findUsername(String username) {
        int userId = dao.getUserIdByUsername(username);

        if (userId == -1) {
            return null;
        }

        String password = dao.getPasswordByUsername(username);

        return new UserProfile(String.valueOf(userId), username, password);
    }

    @Override
    public boolean hasSavedParty(String username) {
        int userId = dao.getUserIdByUsername(username);
        return dao.hasSavedParty(userId);
    }

    @Override
    public List<String> getSavedParties(String username) {
        int userId = dao.getUserIdByUsername(username);
        return dao.getSavedParties(userId);
    }

    @Override
    public void recordPvpResult(String winnerUsername, String loserUsername) {
        int winnerId = dao.getUserIdByUsername(winnerUsername);
        int loserId = dao.getUserIdByUsername(loserUsername);
        dao.recordPvpResult(winnerId, loserId);
    }
}