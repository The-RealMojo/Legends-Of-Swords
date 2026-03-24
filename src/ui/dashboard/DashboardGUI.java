package ui.dashboard;

import db.GameSaveDAO;
import game.battle.Hero;
import game.pvp.Invite;
import game.pvp.PvpManager;
import ui.dashboard.HallOfFameUI;
import ui.campaign.PveView;
import ui.login.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Main dashboard shown after login.
 */
public class DashboardGUI extends JFrame {

    private final PvpManager pvpManager;
    private final UserProfile currentUser;

    private JTextArea infoArea;

    public DashboardGUI(PvpManager pvpManager, UserProfile currentUser) {
        this.pvpManager = pvpManager;
        this.currentUser = currentUser;

        setTitle("Dashboard — " + currentUser.getUsername());
        setSize(580, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildUI();
        displaySavedData(currentUser);

        revalidate();
        repaint();
    }

    // UI setup

    private void buildUI() {
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        JTextField opponentField = new JTextField(12);
        JButton inviteButton = new JButton("Send PvP Invite");

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Opponent Username:"));
        topPanel.add(opponentField);
        topPanel.add(inviteButton);

        JButton pveButton = new JButton("👹 Play PvE");
        JButton continueButton = new JButton("▶ Continue PvE");
        JButton pvpButton = new JButton("⚔ Play PvP");
        JButton hallOfFameButton = new JButton("🏆 Hall of Fame");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(pveButton);
        buttonPanel.add(continueButton);
        buttonPanel.add(pvpButton);
        buttonPanel.add(hallOfFameButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(infoArea), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pveButton.addActionListener(e -> launchPvE());
        continueButton.addActionListener(e -> continuePvE());
        pvpButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Use the invite system above for PvP."));
        inviteButton.addActionListener(e -> sendInvite(opponentField.getText().trim()));
        hallOfFameButton.addActionListener(e -> {
            HallOfFameUI ui = new HallOfFameUI(new GameSaveDAO());
            ui.setVisible(true);
        });
    }

    // Dashboard text

    public void displaySavedData(UserProfile profile) {
        StringBuilder message = new StringBuilder();

        message.append("Welcome, ").append(profile.getUsername()).append("\n\n");
        message.append("User ID: ").append(profile.getUserId()).append("\n");
        message.append("Best Score: ").append(profile.getScores()).append("\n");
        message.append("Campaign Progress: room ")
                .append(profile.getCampaignProgress())
                .append("/30\n\n");

        message.append("Saved Parties:\n");
        List<String> parties = profile.getSavedParties();

        if (parties.isEmpty()) {
            message.append("  — none yet\n");
        } else {
            for (String party : parties) {
                message.append("  • ").append(party).append("\n");
            }
        }

        infoArea.setText(message.toString());
    }

    // New PvE campaign

    private void launchPvE() {
        Hero.HeroClass[] classes = {
                Hero.HeroClass.ORDER,
                Hero.HeroClass.CHAOS,
                Hero.HeroClass.WARRIOR,
                Hero.HeroClass.MAGE
        };

        String[] classNames = Arrays.stream(classes)
                .map(Enum::name)
                .toArray(String[]::new);

        String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Choose your hero's starting class:",
                "New Campaign",
                JOptionPane.PLAIN_MESSAGE,
                null,
                classNames,
                classNames[2]
        );

        if (chosen == null) {
            return;
        }

        Hero.HeroClass heroClass = Hero.HeroClass.valueOf(chosen);

        String heroName = JOptionPane.showInputDialog(
                this,
                "Enter your hero's name:",
                "Hero Name",
                JOptionPane.PLAIN_MESSAGE
        );

        if (heroName == null || heroName.isBlank()) {
            heroName = chosen.charAt(0) + "ero";
        }

        String partyName = JOptionPane.showInputDialog(
                this,
                "Enter a party name:",
                "My Party"
        );

        if (partyName == null || partyName.isBlank()) {
            partyName = "My Party";
        }

        Hero startingHero = new Hero(heroName.trim(), heroClass);

        GameSaveDAO dao = new GameSaveDAO();
        PveView view = new PveView(
                List.of(startingHero),
                dao,
                currentUser.getUserId(),
                partyName.trim()
        );

        view.setVisible(true);
    }

    // Continue PvE campaign

    private void continuePvE() {
        GameSaveDAO dao = new GameSaveDAO();
        List<String> saved = dao.getIncompleteCampaigns(currentUser.getUserId());

        if (saved.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No incomplete PvE campaigns.");
            return;
        }

        String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Select campaign:",
                "Continue",
                JOptionPane.PLAIN_MESSAGE,
                null,
                saved.toArray(),
                saved.get(0)
        );

        if (chosen == null) return;

        List<Hero> heroes = dao.loadCampaignHeroes(currentUser.getUserId(), chosen);

        PveView view = new PveView(
                heroes,
                dao,
                currentUser.getUserId(),
                chosen,
                true
        );

        view.setVisible(true);
    }

    // PvP

    private void sendInvite(String opponent) {
        if (!pvpManager.canInvite(currentUser.getUsername(), opponent)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invite failed. Opponent may not exist, be yourself, or lack a saved party."
            );
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

        List<String> myParties = pvpManager.getSavedParties(currentUser.getUsername());
        List<String> opponentParties = pvpManager.getSavedParties(opponent);

        String myParty = (String) JOptionPane.showInputDialog(
                this,
                "Choose your party:",
                "Party Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                myParties.toArray(),
                myParties.get(0)
        );

        String opponentParty = (String) JOptionPane.showInputDialog(
                this,
                "Choose " + opponent + "'s party:",
                "Party Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opponentParties.toArray(),
                opponentParties.get(0)
        );

        if (myParty == null || opponentParty == null) {
            JOptionPane.showMessageDialog(this, "Selection cancelled.");
            return;
        }

        invite.setSenderParty(myParty);
        invite.setReceiverParty(opponentParty);

        GameSaveDAO dao = new GameSaveDAO();
        List<Hero> myHeroes       = dao.loadCampaignHeroes(currentUser.getUserId(), myParty);
        List<Hero> opponentHeroes = dao.loadCampaignHeroes(dao.getUserIdByUsername(opponent), opponentParty);

        if (myHeroes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your selected party has no heroes.");
            return;
        }
        if (opponentHeroes.isEmpty()) {
            JOptionPane.showMessageDialog(this, opponent + "'s selected party has no heroes.");
            return;
        }

        // Cast to List<Unit> for BattleGUI
        List<game.battle.Unit> p1Units = new java.util.ArrayList<>(myHeroes);
        List<game.battle.Unit> p2Units = new java.util.ArrayList<>(opponentHeroes);

        ui.battle.BattleGUI pvpBattle = new ui.battle.BattleGUI(
                p1Units,
                p2Units,
                () -> {
                    boolean p1Won = myHeroes.stream().anyMatch(game.battle.Unit::isAlive);
                    pvpManager.recordMatchResult(
                            p1Won ? currentUser.getUsername() : opponent,
                            p1Won ? opponent : currentUser.getUsername()
                    );
                    JOptionPane.showMessageDialog(this, "PvP result recorded!");
                }
        );

        pvpBattle.setVisible(true);
    }
}