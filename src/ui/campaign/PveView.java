package ui.campaign;

import db.GameSaveDAO;
import game.battle.Hero;
import game.campaign.Campaign;
import game.campaign.PvEController;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

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
    private JButton inventoryBtn;
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
        inventoryBtn = new JButton("Inventory");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        buttonPanel.add(partyInfoBtn);
        buttonPanel.add(inventoryBtn);
        buttonPanel.add(nextRoomBtn);
        buttonPanel.add(saveExitBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        nextRoomBtn.addActionListener(e -> advanceRoom());
        saveExitBtn.addActionListener(e -> saveAndExit());
        partyInfoBtn.addActionListener(e -> showPartyInfo());
        inventoryBtn.addActionListener(e -> showInventory());
    }

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

    private void showInventory() {
        Campaign campaign = controller.getCampaign();
        if (campaign == null) return;

        var party = campaign.getParty();
        var inv = party.getInventory();

        if (inv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inventory is empty.");
            return;
        }

        String[] items = inv.entrySet().stream()
                .map(e -> formatInventoryItem(e.getKey(), e.getValue()))
                .toArray(String[]::new);

        String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Select item to use:",
                "Inventory",
                JOptionPane.PLAIN_MESSAGE,
                null,
                items,
                items[0]
        );

        if (chosen == null) return;

        String itemName = chosen.split(" x")[0];

        String[] heroNames = party.getHeroes().stream()
                .map(Hero::getName)
                .toArray(String[]::new);

        String heroChoice = (String) JOptionPane.showInputDialog(
                this,
                "Use " + itemName + " on which hero?",
                "Target Hero",
                JOptionPane.PLAIN_MESSAGE,
                null,
                heroNames,
                heroNames[0]
        );

        if (heroChoice == null) return;

        Hero target = party.getHeroes().stream()
                .filter(h -> h.getName().equals(heroChoice))
                .findFirst()
                .orElse(null);

        if (target == null) return;

        String result = applyItemEffect(target, itemName);

        if (result == null) {
            JOptionPane.showMessageDialog(this, "That item cannot be used right now.");
            return;
        }

        party.useItem(itemName);

        appendLog(result);
        refreshStatus();
    }

    private String formatInventoryItem(String itemName, int qty) {
        return itemName + " x" + qty + " " + getItemDescription(itemName);
    }

    private String getItemDescription(String itemName) {
        return switch (itemName) {
            case "Bread" -> "(+20 HP)";
            case "Cheese" -> "(+50 HP)";
            case "Steak" -> "(+200 HP)";
            case "Water" -> "(+10 Mana)";
            case "Juice" -> "(+30 Mana)";
            case "Wine" -> "(+100 Mana)";
            case "Elixir" -> "(Revive + Full HP + Mana)";
            default -> "";
        };
    }

    private String applyItemEffect(Hero h, String itemName) {
        return switch (itemName) {
            case "Bread" -> {
                int before = h.getHp();
                h.restoreHp(20);
                yield h.getName() + " used Bread: +" + (h.getHp() - before) + " HP";
            }
            case "Cheese" -> {
                int before = h.getHp();
                h.restoreHp(50);
                yield h.getName() + " used Cheese: +" + (h.getHp() - before) + " HP";
            }
            case "Steak" -> {
                int before = h.getHp();
                h.restoreHp(200);
                yield h.getName() + " used Steak: +" + (h.getHp() - before) + " HP";
            }
            case "Water" -> {
                int before = h.getMana();
                h.restoreMana(10);
                yield h.getName() + " used Water: +" + (h.getMana() - before) + " Mana";
            }
            case "Juice" -> {
                int before = h.getMana();
                h.restoreMana(30);
                yield h.getName() + " used Juice: +" + (h.getMana() - before) + " Mana";
            }
            case "Wine" -> {
                int before = h.getMana();
                h.restoreMana(100);
                yield h.getName() + " used Wine: +" + (h.getMana() - before) + " Mana";
            }
            case "Elixir" -> {
                boolean wasDead = !h.isAlive();
                if (wasDead) {
                    h.setHp(1);
                }
                int beforeHp = h.getHp();
                int beforeMana = h.getMana();
                h.restoreHp(9999);
                h.restoreMana(9999);
                yield h.getName() + " used Elixir: +" + (h.getHp() - beforeHp) + " HP, +" +
                        (h.getMana() - beforeMana) + " Mana" + (wasDead ? " (revived)" : "");
            }
            default -> null;
        };
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
                        new java.util.ArrayList<>(campaign.getParty().getHeroes()),
                        campaign.getParty().getInventory()
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
                            new java.util.ArrayList<>(campaign.getParty().getHeroes()),
                            campaign.getParty().getInventory()
                    );
                    JOptionPane.showMessageDialog(this, "Party saved!");
                }
            }
        }
    }

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
                    "<html><b>%s</b><br/>[%s] Lv%d<br/>HP:%d/%d<br/>Mana:%d/%d<br/>EXP:%d/%d</html>",
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