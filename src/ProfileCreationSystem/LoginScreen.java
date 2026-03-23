package ProfileCreationSystem;

import pvp.PvpManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton loginButton;

    private AccountManager accountManager;

    public LoginScreen(AccountManager accountManager) {
        this.accountManager = accountManager;

        setTitle("Profile Creation System");
        setSize(400, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        registerButton = new JButton("Register");
        loginButton = new JButton("Login");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(registerButton);
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                onRegisterSubmit(username, password);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                onLoginSubmit(username, password);
            }
        });
    }

    public void onRegisterSubmit(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            showMessage("Username cannot be empty.");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showMessage("Password cannot be empty.");
            return;
        }

        boolean success = accountManager.register(username, password);

        if (success) {
            showMessage("Registration successful.");
            usernameField.setText("");
            passwordField.setText("");
        } else {
            showMessage("Registration failed. Username may already exist.");
        }
    }

    public void onLoginSubmit(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            showMessage("Username cannot be empty.");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showMessage("Password cannot be empty.");
            return;
        }

        UserProfile user = accountManager.login(username, password);

        if (user != null) {
            showMessage("Login successful. Welcome, " + user.getUsername() + "!");

            PvpManager pvpManager = new PvpManager(accountManager.getUserDB());
            DashboardGUI dashboard = new DashboardGUI(pvpManager);
            dashboard.displaySavedData(user);
            dashboard.setVisible(true);

            dispose();
        } else {
            showMessage("Login failed. Invalid username or password.");
        }
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
