package ProfileCreationSystem;

import database.GameSaveDAO;

public class ProfileCreationMain {

    public static void main(String[] args) {

        GameSaveDAO dao = new GameSaveDAO();

        JdbcUserDB userDB = new JdbcUserDB(dao);
        AccountManager accountManager = new AccountManager(userDB);

        LoginScreen loginScreen = new LoginScreen(accountManager);
        loginScreen.setVisible(true);
    }
}