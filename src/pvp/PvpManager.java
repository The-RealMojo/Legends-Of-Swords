package pvp;

import ProfileCreationSystem.IUserDB;
import java.util.List;

public class PvpManager {
    private IUserDB userDB;

    public PvpManager(IUserDB userDB) {
        this.userDB = userDB;
    }

    public boolean canInvite(String inviterUsername, String opponentUsername) {
        if (opponentUsername == null || opponentUsername.trim().isEmpty()) {
            return false;
        }

        opponentUsername = opponentUsername.trim();

        if (inviterUsername.equals(opponentUsername)) {
            return false;
        }

        if (!userDB.usernameExists(opponentUsername)) {
            return false;
        }

        if (!userDB.hasSavedParty(inviterUsername)) {
            return false;
        }

        if (!userDB.hasSavedParty(opponentUsername)) {
            return false;
        }

        return true;
    }

    public List<String> getSavedParties(String username) {
        return userDB.getSavedParties(username);
    }

    public void recordMatchResult(String winnerUsername, String loserUsername) {
        userDB.recordPvpResult(winnerUsername, loserUsername);
    }
}