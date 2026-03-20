package ProfileCreationSystem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardGUI extends JFrame {
    private JTextArea infoArea;
    private JButton pveButton;
    private JButton pvpButton;

    public DashboardGUI() {
        setTitle("Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        infoArea = new JTextArea();
        infoArea.setEditable(false);

        pveButton = new JButton("Play PvE");
        pvpButton = new JButton("Play PvP");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(pveButton);
        buttonPanel.add(pvpButton);

        add(new JScrollPane(infoArea), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pveButton.addActionListener(e -> selectGameMode("PvE"));
        pvpButton.addActionListener(e -> selectGameMode("PvP"));
    }

    public void displaySavedData(UserProfile profile) {
        StringBuilder text = new StringBuilder();

        text.append("Welcome, ").append(profile.getUsername()).append("\n\n");
        text.append("User ID: ").append(profile.getUserId()).append("\n");
        text.append("Best Score: ").append(profile.getScores()).append("\n");
        text.append("Ranking: ").append(profile.getRankings()).append("\n");
        text.append("Campaign Progress: ").append(profile.getCampaignProgress()).append("\n\n");

        text.append("Saved Parties:\n");
        List<String> parties = profile.getSavedParties();

        if (parties.isEmpty()) {
            text.append("- No saved parties yet\n");
        } else {
            for (String party : parties) {
                text.append("- ").append(party).append("\n");
            }
        }

        infoArea.setText(text.toString());
    }

    public void selectGameMode(String mode) {
        JOptionPane.showMessageDialog(this, "Selected mode: " + mode);
    }
}
