package ui.login;

/**
 * Handles registration and login logic.
 */
public class AccountManager {

    private final IUserDB userDB;

    public AccountManager(IUserDB userDB) {
        this.userDB = userDB;
    }

    public boolean register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        username = username.trim();

        if (userDB.usernameExists(username)) {
            return false;
        }

        userDB.createUser(username, password);
        return true;
    }

    public UserProfile login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        return userDB.authenticate(username.trim(), password)
                ? userDB.findUsername(username.trim())
                : null;
    }

    public IUserDB getUserDB() {
        return userDB;
    }
}