package ui.battle;

import game.battle.Action;
import game.battle.Battle;
import game.battle.Hero;
import game.battle.Unit;
import game.battle.observer.BattleObserver;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Design patterns:
 *   Observer  — implements BattleObserver to receive damage/turn events.
 *   Strategy  — action buttons delegate to Battle.executeAction(Action).
 */
public class BattleGUI extends JFrame implements BattleObserver {

    /** Read by AttackStrategy before executing the attack. Set to null after. */
    public static Unit selectedTarget = null;

    private final Battle     battle;
    private final List<Unit> playerParty;
    private final List<Unit> enemyParty;
    private final Runnable   onBattleEnd;

    private JTextArea logArea;
    private JPanel    heroPanel, enemyPanel;
    private JButton   attackBtn, defendBtn, waitBtn, castBtn;

    public BattleGUI(List<Unit> playerParty, List<Unit> enemyParty, Runnable onBattleEnd) {
        this.playerParty = playerParty;
        this.enemyParty  = enemyParty;
        this.onBattleEnd = onBattleEnd;
        this.battle      = new Battle(playerParty, enemyParty);
        this.battle.addObserver(this);
        buildUI();
        refreshDisplay();
        promptOrAutoAct();
    }

    //UI
    private void buildUI() {
        setTitle("Battle — Legends of Sword and Wand");
        setSize(760, 610); setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null); setLayout(new BorderLayout(8,8));

        heroPanel  = new JPanel(); heroPanel .setBorder(BorderFactory.createTitledBorder("Your Party"));
        enemyPanel = new JPanel(); enemyPanel.setBorder(BorderFactory.createTitledBorder("Enemies"));
        JPanel parties = new JPanel(new GridLayout(1,2,8,0));
        parties.add(heroPanel); parties.add(enemyPanel);
        parties.setPreferredSize(new Dimension(740, 215));
        add(parties, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false); logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setLineWrap(true);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        attackBtn = new JButton("⚔ Attack");
        defendBtn = new JButton("🛡 Defend");
        waitBtn   = new JButton("⏳ Wait");
        castBtn   = new JButton("✨ Cast Ability");
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btns.add(attackBtn); btns.add(defendBtn); btns.add(waitBtn); btns.add(castBtn);
        add(btns, BorderLayout.SOUTH);

        attackBtn.addActionListener(e -> handleAttack());
        defendBtn.addActionListener(e -> playerAction(Action.DEFEND));
        waitBtn  .addActionListener(e -> playerAction(Action.WAIT));
        castBtn  .addActionListener(e -> handleCast());
    }

    //Player turn
    private void handleAttack() {
        if (battle.isBattleOver()) return;
        Unit current = battle.getCurrentUnit();
        if (current==null||enemyParty.contains(current)) return;

        List<Unit> targets = new ArrayList<>();
        for (Unit u : enemyParty) if (u.isAlive()) targets.add(u);
        if (targets.isEmpty()) return;

        Unit target;
        if (targets.size()==1) {
            target = targets.get(0);
        } else {
            String[] names = targets.stream().map(Unit::getName).toArray(String[]::new);
            String chosen = (String) JOptionPane.showInputDialog(this,
                "Choose attack target:", "Attack", JOptionPane.PLAIN_MESSAGE, null, names, names[0]);
            if (chosen==null) return;
            target = targets.stream().filter(u->u.getName().equals(chosen)).findFirst().orElse(targets.get(0));
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
        if (current==null||enemyParty.contains(current)) return;
        setButtonsEnabled(false);
        battle.executeAction(action);
        afterPlayerAction();
    }

    private void handleCast() {
        if (battle.isBattleOver()) return;
        Unit current = battle.getCurrentUnit();
        if (current==null||enemyParty.contains(current)) return;
        if (!(current instanceof Hero hero)) { playerAction(Action.CAST); return; }

        String[] abilities = hero.getAvailableAbilityNames();
        if (abilities==null||abilities.length==0) { log(hero.getName()+" has no castable abilities."); return; }

        String chosen = (String) JOptionPane.showInputDialog(this,
            "Choose an ability:", "Cast Ability", JOptionPane.PLAIN_MESSAGE, null, abilities, abilities[0]);
        if (chosen==null) return;

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

    @Override
    public void onAbilityResult(Unit actor, String result) {
        log(result);
        refreshDisplay();
    }

    //Enemy auto-turns
    private void promptOrAutoAct() {
        Unit current = battle.getCurrentUnit();
        if (current == null) {
            endBattle();
            return;
        }

        if (enemyParty.contains(current)) {
            Timer t = new Timer(500, null);
            t.addActionListener(e -> {
                if (battle.isBattleOver()) {
                    t.stop();
                    endBattle();
                    return;
                }

                Unit c = battle.getCurrentUnit();
                if (c == null) {
                    t.stop();
                    endBattle();
                    return;
                }

                if (enemyParty.contains(c)) {
                    Action enemyAction = battle.getEnemyAction(c);
                    battle.executeAction(enemyAction);
                    refreshDisplay();

                    if (battle.isBattleOver()) {
                        t.stop();
                        endBattle();
                    }
                } else {
                    t.stop();
                    setButtonsEnabled(true);
                    log("--- " + c.getName() + "'s turn ---");
                }
            });
            t.start();
        } else {
            setButtonsEnabled(true);
            log("--- " + current.getName() + "'s turn ---");
        }
    }
    //End
    private void endBattle() {
        setButtonsEnabled(false);
        String winner = battle.getWinner();
        log("\n=== Battle Over! "+(winner!=null?winner+" win!":"Draw")+" ===");
        Timer delay = new Timer(1500, e -> {
            JOptionPane.showMessageDialog(this, "Battle finished!\n"+(winner!=null?winner+" won.":"Draw."),
                "Battle Result", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            if (onBattleEnd!=null) onBattleEnd.run();
        });
        delay.setRepeats(false); delay.start();
    }

    //Display
    private void refreshDisplay() {
        refreshParty(heroPanel, playerParty); refreshParty(enemyPanel, enemyParty);
        revalidate(); repaint();
    }
    private void refreshParty(JPanel panel, List<Unit> party) {
        panel.removeAll(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Unit cur = battle.getCurrentUnit();
        for (Unit u : party) {
            String tag=(u==cur)?" ◄ TURN":"", stun=u.isStunned()?" [STUNNED]":"", dead=!u.isAlive()?" [DEAD]":"";
            JLabel lbl = new JLabel(String.format(
                "<html><b>%s</b>%s%s%s<br/>HP:%d/%d  Mana:%d/%d  ATK:%d DEF:%d</html>",
                u.getName(),tag,stun,dead, u.getHp(),u.getMaxHp(),u.getMana(),u.getMaxMana(),u.getAttack(),u.getDefense()));
            lbl.setBorder(BorderFactory.createEmptyBorder(4,6,4,6));
            lbl.setForeground(!u.isAlive()?Color.GRAY:(u==cur?new Color(0,120,0):Color.BLACK));
            panel.add(lbl);
        }
    }
    private void setButtonsEnabled(boolean b) { attackBtn.setEnabled(b); defendBtn.setEnabled(b); waitBtn.setEnabled(b); castBtn.setEnabled(b); }
    private void log(String msg) { logArea.append(msg+"\n"); logArea.setCaretPosition(logArea.getDocument().getLength()); }

    //BattleObserver
    @Override public void onTurnStart(Unit u)         { log("--- "+u.getName()+"'s turn ---"); }
    @Override public void onAction(Unit a, Action act) { log(a.getName()+" → "+act); }
    @Override public void onDamage(Unit a, Unit t, int d){ log(a.getName()+" deals "+d+" to "+t.getName()+" (HP:"+t.getHp()+"/"+t.getMaxHp()+")"); }
    @Override public void onBattleEnd(String w)        { log("Winner: "+w); }
}
