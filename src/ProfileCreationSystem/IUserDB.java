package Profile;

import java.util.List;

public interface IUserDB {
    boolean usernameExists(String username);
    void save(UserProfile user);
    UserProfile findUsername(String username);

    boolean hasSavedParty(String username);
    List<String> getSavedParties(String username);
    void recordPvpResult(String winnerUsername, String loserUsername);
}
