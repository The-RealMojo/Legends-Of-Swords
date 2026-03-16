package ProfileCreationSystem;

import javax.swing.*;
import java.awt.*;

public class DashboardGUI extends JFrame {
    private JTextArea infoArea;

    public DashboardGUI() {
        setTitle("Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        infoArea = new JTextArea();
        infoArea.setEditable(false);

        add(new JScrollPane(infoArea), BorderLayout.CENTER);
    }

    public void displaySavedData(UserProfile profile) {
        String text = "";
        text += "User ID: " + profile.getUserId() + "\n";
        text += "Username: " + profile.getUsername() + "\n";
        text += "Scores: " + profile.getScores() + "\n";
        text += "Rankings: " + profile.getRankings() + "\n";
        text += "Campaign Progress: " + profile.getCampaignProgress() + "\n";

        infoArea.setText(text);
    }

    public void selectGameMode(String mode) {
        JOptionPane.showMessageDialog(this, "Selected mode: " + mode);
    }
}
