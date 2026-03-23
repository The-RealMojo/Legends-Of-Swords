package ProfileCreationSystem;

import pvp.Invite;
import pvp.PvpManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardGUI extends JFrame {
    private JTextArea infoArea;
    private JButton pveButton;
    private JButton pvpButton;
    private JButton inviteButton;
    private JTextField opponentField;

    private PvpManager pvpManager;
    private UserProfile currentUser;

    public DashboardGUI(PvpManager pvpManager) {
        this.pvpManager = pvpManager;

        setTitle("Dashboard");
        setSize(550, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        infoArea = new JTextArea();
        infoArea.setEditable(false);

        pveButton = new JButton("Play PvE");
        pvpButton = new JButton("Play PvP");
        inviteButton = new JButton("Send PvP Invite");
        opponentField = new JTextField(12);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Opponent Username:"));
        topPanel.add(opponentField);
        topPanel.add(inviteButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(pveButton);
        buttonPanel.add(pvpButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(infoArea), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pveButton.addActionListener(e -> selectGameMode("PvE"));
        pvpButton.addActionListener(e -> selectGameMode("PvP"));
        inviteButton.addActionListener(e -> sendInvite());
    }

    public void displaySavedData(UserProfile profile) {
        this.currentUser = profile;

        StringBuilder text = new StringBuilder();

        text.append("Welcome, ").append(profile.getUsername()).append("\n\n");
        text.append("User ID: ").append(profile.getUserId()).append("\n");
        text.append("Scores: ").append(profile.getScores()).append("\n");
        text.append("Rankings: ").append(profile.getRankings()).append("\n");
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

    private void sendInvite() {
        String opponent = opponentField.getText().trim();

        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "No logged-in user found.");
            return;
        }

        if (!pvpManager.canInvite(currentUser.getUsername(), opponent)) {
            JOptionPane.showMessageDialog(this,
                    "Invite failed. Opponent may not exist, be yourself, or one of the users has no saved party.");
            return;
        }

        Invite invite = new Invite(currentUser.getUsername(), opponent);

        int accepted = JOptionPane.showConfirmDialog(
                this,
                opponent + ", do you accept this PvP invite?",
                "PvP Invite",
                JOptionPane.YES_NO_OPTION
        );

        if (accepted != JOptionPane.YES_OPTION) {
            invite.decline();
            JOptionPane.showMessageDialog(this, "Invite declined.");
            return;
        }

        invite.accept();

        java.util.List<String> myParties = pvpManager.getSavedParties(currentUser.getUsername());
        java.util.List<String> opponentParties = pvpManager.getSavedParties(opponent);

        String myParty = (String) JOptionPane.showInputDialog(
                this,
                "Choose your party:",
                "Party Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                myParties.toArray(),
                myParties.get(0)
        );

        String enemyParty = (String) JOptionPane.showInputDialog(
                this,
                "Choose " + opponent + "'s party:",
                "Party Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opponentParties.toArray(),
                opponentParties.get(0)
        );

        if (myParty == null || enemyParty == null) {
            JOptionPane.showMessageDialog(this, "Party selection cancelled.");
            return;
        }

        invite.setSenderParty(myParty);
        invite.setReceiverParty(enemyParty);

        JOptionPane.showMessageDialog(this,
                "PvP invite accepted.\n\n" +
                        "Player A: " + invite.getSenderUsername() + "\n" +
                        "Party: " + invite.getSenderParty() + "\n\n" +
                        "Player B: " + invite.getReceiverUsername() + "\n" +
                        "Party: " + invite.getReceiverParty() + "\n\n" +
                        "Match is ready for BattleView integration.");
    }
}
