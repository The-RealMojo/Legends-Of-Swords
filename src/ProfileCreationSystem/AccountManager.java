package ProfileCreationSystem;

public class AccountManager {
    private IUserDB userDB;

    public AccountManager(IUserDB userDB) {
        this.userDB = userDB;
    }

    public boolean register(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        username = username.trim();

        if (userDB.usernameExists(username)) {
            return false;
        }

        UserProfile newUser = new UserProfile();
        newUser.setUsername(username);
        newUser.setPassword(PasswordUtil.hashPassword(password));

        userDB.save(newUser);
        return true;
    }

    public UserProfile login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        username = username.trim();

        UserProfile user = userDB.findUsername(username);

        if (user == null) {
            return null;
        }

        if (user.getPassword().equals(PasswordUtil.hashPassword(password))) {
            return user;
        }

        return null;
    }

    public IUserDB getUserDB() {
        return userDB;
    }
}
