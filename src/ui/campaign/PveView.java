package ui.campaign;

import db.GameSaveDAO;
import game.battle.Hero;
import game.campaign.Campaign;
import game.campaign.PvEController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Swing UI for campaign.
 */
public class PveView extends JFrame {

    private final PvEController controller;
    private final GameSaveDAO dao;
    private final int userId;

    private JTextArea outputArea;
    private JPanel partyPanel;
    private JLabel roomLabel;
    private JLabel goldLabel;
    private JButton nextRoomBtn;
    private JButton saveExitBtn;
    private JButton partyInfoBtn;
    private boolean roomInProgress;

    public PveView(List<Hero> heroes) {
        this(heroes, null, -1, "My Party", false);
    }

    public PveView(List<Hero> heroes, GameSaveDAO dao, int userId, String partyName) {
        this(heroes, dao, userId, partyName, false);
    }

    public PveView(List<Hero> heroes, GameSaveDAO dao, int userId, String partyName, boolean continueCampaign) {
        this.dao = dao;
        this.userId = userId;

        setTitle("PvE Campaign — Legends of Sword and Wand");
        setSize(720, 590);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        controller = new PvEController();

        if (continueCampaign && dao != null && userId != -1) {
            controller.continueCampaign(heroes, dao, userId, partyName);
        } else if (dao != null && userId != -1) {
            controller.startCampaign(heroes, dao, userId, partyName);
        } else {
            controller.startCampaign(heroes);
        }

        buildUI();
        refreshStatus();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                Campaign campaign = controller.getCampaign();

                if (roomInProgress) {
                    JOptionPane.showMessageDialog(
                            PveView.this,
                            "You cannot exit while a room is being resolved or while a battle is open."
                    );
                    return;
                }

                if (campaign != null && !campaign.isFinished()) {
                    int choice = JOptionPane.showConfirmDialog(
                            PveView.this,
                            "Save before exiting?",
                            "Exit",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        campaign.saveProgress();
                        dispose();
                    } else if (choice == JOptionPane.NO_OPTION) {
                        dispose();
                    }
                } else {
                    dispose();
                }
            }
        });
    }

    // UI setup

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        roomLabel = new JLabel("Room: 0/30");
        goldLabel = new JLabel("Gold: 500g");

        roomLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        goldLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        statusBar.add(roomLabel);
        statusBar.add(goldLabel);
        add(statusBar, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setLineWrap(true);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        partyPanel = new JPanel();
        partyPanel.setLayout(new BoxLayout(partyPanel, BoxLayout.Y_AXIS));
        partyPanel.setBorder(BorderFactory.createTitledBorder("Party"));
        partyPanel.setPreferredSize(new Dimension(190, 0));
        add(partyPanel, BorderLayout.EAST);

        nextRoomBtn = new JButton("Next Room →");
        saveExitBtn = new JButton("Save & Exit");
        partyInfoBtn = new JButton("Party Info");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        buttonPanel.add(partyInfoBtn);
        buttonPanel.add(nextRoomBtn);
        buttonPanel.add(saveExitBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        nextRoomBtn.addActionListener(e -> advanceRoom());
        saveExitBtn.addActionListener(e -> saveAndExit());
        partyInfoBtn.addActionListener(e -> showPartyInfo());
    }

    // Actions

    private void advanceRoom() {
        Campaign campaign = controller.getCampaign();
        if (campaign == null) {
            return;
        }

        roomInProgress = true;
        nextRoomBtn.setEnabled(false);
        saveExitBtn.setEnabled(false);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return campaign.nextRoom();
            }

            @Override
            protected void done() {
                try {
                    appendLog(get());
                    refreshStatus();

                    if (campaign.isFinished()) {
                        nextRoomBtn.setText("CAMPAIGN COMPLETED");
                        offerPartySave(campaign);
                    } else {
                        nextRoomBtn.setEnabled(true);
                    }
                } catch (Exception ex) {
                    appendLog("[Error] " + ex.getMessage());
                    nextRoomBtn.setEnabled(true);
                }

                roomInProgress = false;
                saveExitBtn.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void saveAndExit() {
        Campaign campaign = controller.getCampaign();

        if (roomInProgress) {
            JOptionPane.showMessageDialog(this, "You cannot exit while a room is being resolved or while a battle is open.");
            return;
        }

        if (campaign != null) {
            campaign.saveProgress();
            JOptionPane.showMessageDialog(this, "Progress has been saved!");
        }

        dispose();
    }

    private void showPartyInfo() {
        Campaign campaign = controller.getCampaign();
        if (campaign == null) {
            return;
        }

        StringBuilder details = new StringBuilder();
        for (Hero hero : campaign.getParty().getHeroes()) {
            details.append(hero.getName())
                    .append(" [").append(hero.getClassDisplayName()).append("] Lv").append(hero.getLevel()).append("\n")
                    .append("HP: ").append(hero.getHp()).append("/").append(hero.getMaxHp()).append("\n")
                    .append("Mana: ").append(hero.getMana()).append("/").append(hero.getMaxMana()).append("\n")
                    .append("ATK: ").append(hero.getAttack()).append("  DEF: ").append(hero.getDefense()).append("\n")
                    .append("EXP: ").append(hero.getCurrentExp()).append("/").append(hero.getExpNeededForNextLevelTotal()).append("\n\n");
        }

        JTextArea textArea = new JTextArea(details.toString(), 14, 48);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(textArea),
                "Party Status",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void offerPartySave(Campaign campaign) {
        if (dao == null || userId == -1) {
            return;
        }

        List<String> savedParties = dao.getSavedParties(userId);

        if (savedParties.size() >= 5) {
            String[] options = savedParties.toArray(new String[0]);
            String replace = (String) JOptionPane.showInputDialog(
                    this,
                    "You have 5 saved parties.\nChoose one to replace:",
                    "Replace Party",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (replace != null) {
                dao.deleteParty(userId, replace);
                dao.saveCampaignProgress(
                        userId,
                        "Party_" + System.currentTimeMillis(),
                        campaign.getCurrentRoom(),
                        campaign.getParty().getGold(),
                        new java.util.ArrayList<>(campaign.getParty().getHeroes())
                );
                JOptionPane.showMessageDialog(this, "Party saved (replaced " + replace + ").");
            }
        } else {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Save party for PvP?",
                    "Save Party",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                String name = JOptionPane.showInputDialog(this, "Party name:");
                if (name != null && !name.isBlank()) {
                    dao.saveCampaignProgress(
                            userId,
                            name,
                            campaign.getCurrentRoom(),
                            campaign.getParty().getGold(),
                            new java.util.ArrayList<>(campaign.getParty().getHeroes())
                    );
                    JOptionPane.showMessageDialog(this, "Party saved!");
                }
            }
        }
    }

    // Display

    private void refreshStatus() {
        Campaign campaign = controller.getCampaign();
        if (campaign == null) {
            return;
        }

        roomLabel.setText("Room: " + campaign.getCurrentRoom() + "/" + campaign.getMaxRooms());
        goldLabel.setText("Gold: " + campaign.getParty().getGold() + "g");

        partyPanel.removeAll();

        for (Hero hero : campaign.getParty().getHeroes()) {
            JLabel label = new JLabel(String.format(
                    "<html><b>%s</b><br/>[%s] Lv%d<br/>HP:%d/%d<br/>Mana:%d/%d</html>",
                    hero.getName(),
                    hero.getClassDisplayName(),
                    hero.getLevel(),
                    hero.getHp(),
                    hero.getMaxHp(),
                    hero.getMana(),
                    hero.getMaxMana(),
                    hero.getCurrentExp(),
                    hero.getExpNeededForNextLevelTotal()
            ));

            label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            if (!hero.isAlive()) {
                label.setForeground(Color.GRAY);
            }

            partyPanel.add(label);
            partyPanel.add(new JSeparator());
        }

        partyPanel.revalidate();
        partyPanel.repaint();
    }

    private void appendLog(String text) {
        outputArea.append(text + "\n" + "─".repeat(44) + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    public PvEController getController() {
        return controller;
    }
}