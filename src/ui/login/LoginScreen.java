package ui.login;

import game.pvp.PvpManager;
import ui.dashboard.DashboardGUI;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    private final JTextField     usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);
    private final AccountManager accountManager;

    public LoginScreen(AccountManager accountManager) {
        this.accountManager = accountManager;
        setTitle("Legends of Sword and Wand — Login");
        setSize(400, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Username:")); panel.add(usernameField);
        panel.add(new JLabel("Password:")); panel.add(passwordField);
        JButton reg = new JButton("Register"), login = new JButton("Login");
        panel.add(reg); panel.add(login);
        add(panel, BorderLayout.CENTER);

        reg  .addActionListener(e -> onRegister());
        login.addActionListener(e -> onLogin());
    }

    private void onRegister() {
        boolean ok = accountManager.register(usernameField.getText(), new String(passwordField.getPassword()));
        showMessage(ok ? "Registration successful!" : "Failed — username may already exist.");
        if (ok) { usernameField.setText(""); passwordField.setText(""); }
    }

    private void onLogin() {
        UserProfile user = accountManager.login(usernameField.getText(), new String(passwordField.getPassword()));
        if (user != null) {
            DashboardGUI dash = new DashboardGUI(new PvpManager(accountManager.getUserDB()), user);
            dash.setVisible(true);
            dispose();
        } else {
            showMessage("Login failed — invalid credentials.");
        }
    }

    public void showMessage(String msg) { JOptionPane.showMessageDialog(this, msg); }
}
