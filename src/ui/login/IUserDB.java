package ui.login;

import java.util.List;

/**
 * User database interface.
 */
public interface IUserDB {
    boolean usernameExists(String username);
    void createUser(String username, String password);
    boolean authenticate(String username, String password);
    UserProfile findUsername(String username);
    boolean hasSavedParty(String username);
    List<String> getSavedParties(String username);
    void recordPvpResult(String winner, String loser);
}