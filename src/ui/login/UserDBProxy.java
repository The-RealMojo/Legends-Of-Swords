package ui.login;

import java.util.List;

public class UserDBProxy implements IUserDB {

    private final IUserDB realDB;

    public UserDBProxy(IUserDB realDB) {
        this.realDB = realDB;
    }

    @Override
    public boolean usernameExists(String username) {
        return realDB.usernameExists(username);
    }

    @Override
    public void createUser(String username, String password) {
        realDB.createUser(username, password);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return realDB.authenticate(username, password);
    }

    @Override
    public UserProfile findUsername(String username) {
        return realDB.findUsername(username);
    }

    @Override
    public boolean hasSavedParty(String username) {
        return realDB.hasSavedParty(username);
    }

    @Override
    public List<String> getSavedParties(String username) {
        return realDB.getSavedParties(username);
    }

    @Override
    public void recordPvpResult(String winner, String loser) {
        realDB.recordPvpResult(winner, loser);
    }
}