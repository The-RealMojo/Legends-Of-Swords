package main;

import db.GameSaveDAO;
import ui.login.AccountManager;
import ui.login.JdbcUserDB;
import ui.login.LoginScreen;
import ui.login.UserDBProxy;

import javax.swing.*;

/**
 * DatabaseConnector --> GameSaveDAO --> JdbcUserDB --> UserDBProxy --> AccountManager --> LoginScreen.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameSaveDAO   dao      = new GameSaveDAO();
            JdbcUserDB    jdbc     = new JdbcUserDB(dao);
            UserDBProxy   proxy    = new UserDBProxy(jdbc);
            AccountManager manager = new AccountManager(proxy);
            LoginScreen   screen  = new LoginScreen(manager);
            screen.setVisible(true);
        });
    }
}
