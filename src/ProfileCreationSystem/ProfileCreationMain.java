package ProfileCreationSystem;

import database.DatabaseConnector;

public class ProfileCreationMain {
    public static void main(String[] args) {
        DatabaseConnector connector = DatabaseConnector.getInstance();
        connector.openConnection();

        IUserDB userDB = new SQLUserRepository(connector);
        AccountManager accountManager = new AccountManager(userDB);

        LoginScreen loginScreen = new LoginScreen(accountManager);
        loginScreen.setVisible(true);
    }
}
