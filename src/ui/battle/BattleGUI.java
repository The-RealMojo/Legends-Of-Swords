package ui.battle;

import game.battle.Action;
import game.battle.Battle;
import game.battle.Hero;
import game.battle.Unit;
import game.battle.observer.BattleObserver;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BattleGUI extends JFrame implements BattleObserver {

    public static Unit selectedTarget = null;

    private final Battle battle;
    private final List<Unit> playerParty;
    private final List<Unit> enemyParty;
    private final Runnable onBattleEnd;
    private final String leftPanelTitle;
    private final String rightPanelTitle;

    private JTextPane logArea;
    private StyledDocument logDoc;

    private JPanel heroPanel, enemyPanel;
    private JButton attackBtn, defendBtn, waitBtn, castBtn;

    public BattleGUI(List<Unit> playerParty, List<Unit> enemyParty, Runnable onBattleEnd) {
        this(playerParty, enemyParty, onBattleEnd, "Your Party", "Enemies");
    }

    public BattleGUI(List<Unit> playerParty, List<Unit> enemyParty, Runnable onBattleEnd,
                     String leftPanelTitle, String rightPanelTitle) {
        this.playerParty = playerParty;
        this.enemyParty = enemyParty;
        this.onBattleEnd = onBattleEnd;
        this.leftPanelTitle = leftPanelTitle;
        this.rightPanelTitle = rightPanelTitle;

        this.battle = new Battle(playerParty, enemyParty);
        this.battle.addObserver(this);

        buildUI();
        refreshDisplay();
        promptOrAutoAct();
    }

    // UI
    private void buildUI() {
        setTitle("Battle — Legends of Sword and Wand");
        setSize(760, 690);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        heroPanel = new JPanel();
        heroPanel.setBorder(BorderFactory.createTitledBorder(leftPanelTitle));

        enemyPanel = new JPanel();
        enemyPanel.setBorder(BorderFactory.createTitledBorder(rightPanelTitle));

        JPanel parties = new JPanel(new GridLayout(1, 2, 8, 0));
        parties.add(heroPanel);
        parties.add(enemyPanel);
        parties.setPreferredSize(new Dimension(740, 390));
        add(parties, BorderLayout.NORTH);

        logArea = new JTextPane();
        logArea.setEditable(false);
        logArea.setBackground(new Color(245, 239, 224));
        logArea.setFont(new Font("Serif", Font.PLAIN, 14));
        logDoc = logArea.getStyledDocument();

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Battle Log"));
        add(logScroll, BorderLayout.CENTER);

        attackBtn = new JButton("⚔ Attack");
        defendBtn = new JButton("🛡 Defend");
        waitBtn = new JButton("⏳ Wait");
        castBtn = new JButton("✨ Cast Ability");

        JPanel btns = new JPanel(new FlowLayout());
        btns.add(attackBtn);
        btns.add(defendBtn);
        btns.add(waitBtn);
        btns.add(castBtn);

        add(btns, BorderLayout.SOUTH);

        attackBtn.addActionListener(e -> handleAttack());
        defendBtn.addActionListener(e -> playerAction(Action.DEFEND));
        waitBtn.addActionListener(e -> playerAction(Action.WAIT));
        castBtn.addActionListener(e -> handleCast());
    }

    // LOG SYSTEM
    private void appendStyled(String text, Color color, boolean bold) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);
        StyleConstants.setFontFamily(style, "Serif");
        StyleConstants.setFontSize(style, 14);

        try {
            logDoc.insertString(logDoc.getLength(), text, style);
            logArea.setCaretPosition(logDoc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void logTurn(String name) {
        appendStyled("\n--- " + name + "'s turn ---\n", new Color(120, 90, 40), true);
    }

    private void logAction(Unit actor, Action act) {
        boolean player = playerParty.contains(actor);
        Color c = player ? new Color(0, 102, 204) : new Color(160, 40, 40);
        appendStyled(actor.getName() + " → " + prettyAction(act) + "\n", c, true);
    }

    private String prettyAction(Action act) {
        return switch (act) {
            case ATTACK -> "ATTACK";
            case DEFEND -> "DEFEND";
            case WAIT -> "WAIT";
            case CAST -> "CAST";
        };
    }

    private void logDamage(String msg) {
        appendStyled(msg + "\n", new Color(180, 20, 20), false);
    }

    private void logHeal(String msg) {
        appendStyled(msg + "\n", new Color(20, 140, 60), false);
    }

    private void logNormal(String msg) {
        appendStyled(msg + "\n", new Color(45, 45, 45), false);
    }

    private void logVictory(String msg) {
        appendStyled("\n" + msg + "\n", new Color(140, 100, 30), true);
    }

    // PLAYER ACTIONS
    private void handleAttack() {
        if (battle.isBattleOver()) return;

        Unit current = battle.getCurrentUnit();
        if (current == null || enemyParty.contains(current)) return;

        List<Unit> targets = new ArrayList<>();
        for (Unit u : enemyParty) {
            if (u.isAlive()) targets.add(u);
        }

        if (targets.isEmpty()) return;

        Unit target;
        if (targets.size() == 1) {
            target = targets.get(0);
        } else {
            String[] names = targets.stream().map(Unit::getName).toArray(String[]::new);
            String chosen = (String) JOptionPane.showInputDialog(
                    this,
                    "Choose attack target:",
                    "Attack",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    names,
                    names[0]
            );
            if (chosen == null) return;
            target = targets.stream()
                    .filter(u -> u.getName().equals(chosen))
                    .findFirst()
                    .orElse(targets.get(0));
        }

        setButtonsEnabled(false);
        selectedTarget = target;
        battle.executeAction(Action.ATTACK);
        selectedTarget = null;

        afterPlayerAction();
    }

    private void playerAction(Action action) {
        if (battle.isBattleOver()) return;
        Unit current = battle.getCurrentUnit();
        if (current == null || enemyParty.contains(current)) return;

        setButtonsEnabled(false);
        battle.executeAction(action);
        afterPlayerAction();
    }

    private void handleCast() {
        Unit current = battle.getCurrentUnit();
        if (!(current instanceof Hero hero)) return;

        String[] abilities = hero.getAvailableAbilityNames();
        if (abilities.length == 0) {
            logNormal("No abilities available.");
            return;
        }

        String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Choose ability",
                "Cast",
                JOptionPane.PLAIN_MESSAGE,
                null,
                abilities,
                abilities[0]
        );

        if (chosen == null) return;

        setButtonsEnabled(false);
        hero.setChosenAbility(hero.stripAbilityLabel(chosen));
        battle.executeAction(Action.CAST);
        hero.setChosenAbility(null);

        afterPlayerAction();
    }

    private void afterPlayerAction() {
        refreshDisplay();
        if (battle.isBattleOver()) endBattle();
        else promptOrAutoAct();
    }

    // ENEMY
    private void promptOrAutoAct() {
        Unit current = battle.getCurrentUnit();
        if (current == null) return;

        if (enemyParty.contains(current)) {
            Timer t = new Timer(500, null);
            t.addActionListener(e -> {
                Unit acting = battle.getCurrentUnit();
                if (acting == null || battle.isBattleOver()) {
                    ((Timer) e.getSource()).stop();
                    if (battle.isBattleOver()) endBattle();
                    return;
                }

                Action enemyAction = battle.getEnemyAction(acting);
                battle.executeAction(enemyAction);
                refreshDisplay();

                if (battle.isBattleOver()) {
                    ((Timer) e.getSource()).stop();
                    endBattle();
                } else {
                    promptOrAutoAct();
                }
            });
            t.setRepeats(false);
            t.start();
        } else {
            setButtonsEnabled(true);
            logTurn(current.getName());
        }
    }

    // DISPLAY
    private void refreshDisplay() {
        refreshParty(heroPanel, playerParty);
        refreshParty(enemyPanel, enemyParty);
    }

    private void refreshParty(JPanel panel, List<Unit> party) {
        panel.removeAll();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        Unit cur = battle.getCurrentUnit();

        for (Unit u : party) {
            boolean showMana = playerParty.contains(u);
            boolean isCurrent = (u == cur);
            boolean isDead = !u.isAlive();

            String nameText = isDead ? "✖ " + u.getName() + " [DEAD]" : u.getName();
            if (isCurrent && !isDead) {
                nameText += "  ◄ TURN";
            }
            if (u.isStunned() && !isDead) {
                nameText += "  [STUNNED]";
            }

            String text;
            if (showMana) {
                text = String.format(
                        "<html><b>%s</b><br/>Lv%d<br/>HP: %d/%d<br/>Mana: %d/%d<br/>ATK: %d   DEF: %d</html>",
                        nameText,
                        u.getLevel(),
                        u.getHp(), u.getMaxHp(),
                        u.getMana(), u.getMaxMana(),
                        u.getAttack(), u.getDefense()
                );
            } else {
                text = String.format(
                        "<html><b>%s</b><br/>Lv%d<br/>HP: %d/%d<br/>ATK: %d   DEF: %d</html>",
                        nameText,
                        u.getLevel(),
                        u.getHp(), u.getMaxHp(),
                        u.getAttack(), u.getDefense()
                );
            }

            JLabel lbl = new JLabel(text);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 210, 210)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

            if (isDead) {
                lbl.setForeground(Color.GRAY);
            } else if (isCurrent) {
                lbl.setForeground(new Color(0, 120, 0));
            } else {
                lbl.setForeground(Color.BLACK);
            }

            panel.add(lbl);
            panel.add(Box.createVerticalStrut(8));
        }

        panel.revalidate();
        panel.repaint();
    }

    // OBSERVER
    @Override
    public void onAction(Unit actor, Action action) {
        logAction(actor, action);
    }

    @Override
    public void onDamage(Unit attacker, Unit target, int damage) {
        logDamage(attacker.getName() + " deals " + damage + " to " +
                target.getName() + " (HP:" + target.getHp() + "/" + target.getMaxHp() + ")");
    }

    @Override
    public void onAbilityResult(Unit actor, String result) {
        String lower = result.toLowerCase();
        if (lower.contains("recover") || lower.contains("heal") || lower.contains("restores")) {
            logHeal(result);
        } else {
            logNormal(result);
        }
    }

    @Override
    public void onBattleEnd(String winner) {
        logVictory("=== Battle Over! " + winner + " win! ===");
    }

    // END
    private void endBattle() {
        setButtonsEnabled(false);
        Timer t = new Timer(1500, e -> {
            dispose();
            if (onBattleEnd != null) onBattleEnd.run();
        });
        t.setRepeats(false);
        t.start();
    }

    private void setButtonsEnabled(boolean b) {
        attackBtn.setEnabled(b);
        defendBtn.setEnabled(b);
        waitBtn.setEnabled(b);
        castBtn.setEnabled(b);
    }
}