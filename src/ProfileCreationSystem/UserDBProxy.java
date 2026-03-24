package ProfileCreationSystem;

import java.util.List;

public class UserDBProxy implements IUserDB {

    private IUserDB realUserDB;

    public UserDBProxy(IUserDB realUserDB) {
        this.realUserDB = realUserDB;
    }

    @Override
    public boolean usernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("PROXY SECURITY: Blocked empty username check.");
            return false;
        }
        System.out.println("PROXY LOG: Checking database for username: " + username);
        return realUserDB.usernameExists(username);
    }

    @Override
    public void save(UserProfile user) {
        System.out.println("PROXY LOG: Saving new user to database: " + user.getUsername());
        realUserDB.save(user);
    }

    @Override
    public UserProfile findUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        System.out.println("PROXY LOG: Fetching profile data for: " + username);
        return realUserDB.findUsername(username);
    }

    @Override
    public boolean hasSavedParty(String username) {
        return realUserDB.hasSavedParty(username);
    }

    @Override
    public List<String> getSavedParties(String username) {
        System.out.println("PROXY LOG: Fetching saved parties for: " + username);
        return realUserDB.getSavedParties(username);
    }

    @Override
    public void recordPvpResult(String winnerUsername, String loserUsername) {
        System.out.println("PROXY LOG: Recording PvP match. Winner: " + winnerUsername + " | Loser: " + loserUsername);
        realUserDB.recordPvpResult(winnerUsername, loserUsername);
    }
}