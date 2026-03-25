package game.campaign;

import game.battle.*;
import game.party.Party;
import ui.battle.BattleGUI;

import javax.swing.*;
import java.util.*;

/**
 * execute() opens BattleGUI on the EDT via invokeLater, then blocks the
 * calling background thread via synchronized wait/notify until the battle ends.
 * Rewards (win):  +75g per enemy level, +50 EXP per enemy level split among survivors.
 * Penalty (loss): -10% gold.
 */
public class BattleRoom extends Room {
    public BattleRoom(int floor) { super(floor); }

    @Override
    public String execute(Party party) {
        List<Unit> enemies   = generateEnemies(party);
        List<Unit> heroUnits = new ArrayList<>(party.getHeroes());

        StringBuilder result = new StringBuilder("⚔ Enemies encountered:\n");
        for (Unit e : enemies)
            result.append(String.format("  %s  Lv%d  HP:%d  ATK:%d  DEF:%d\n",
                e.getName(), e.getLevel(), e.getMaxHp(), e.getAttack(), e.getDefense()));

        final Object lock = new Object();
        final boolean[] won = {false};

        SwingUtilities.invokeLater(() -> {
            BattleGUI gui = new BattleGUI(heroUnits, enemies, () -> {
                won[0] = heroUnits.stream().anyMatch(Unit::isAlive);
                synchronized (lock) { lock.notifyAll(); }
            });
            gui.setVisible(true);
        });
        synchronized (lock) { try { lock.wait(); } catch (InterruptedException ignored) {} }

        if (won[0]) {
            int gold=0, exp=0;
            for (Unit e:enemies) { gold+=75*e.getLevel(); exp+=50*e.getLevel(); }
            party.addGold(gold);
            List<Hero> survivors=new ArrayList<>();
            for (Unit u:heroUnits) if(u.isAlive()&&u instanceof Hero h) survivors.add(h);
            if (!survivors.isEmpty()) {
                int each=exp/survivors.size();
                for (Hero h:survivors) if(h.addExp(each)) result.append(h.getName()).append(" levelled up!\n");
            }
            result.append("\nVictory! +").append(gold).append("g, +").append(exp).append(" EXP.\n");
        } else {
            int penalty=(int)(party.getGold()*0.10);
            party.addGold(-penalty);
            result.append("\nDefeated! Lost ").append(penalty).append("g.\n");
        }
        return result.toString();
    }

    private List<Unit> generateEnemies(Party party) {
        List<Unit> enemies = new ArrayList<>();
        Random rand = new Random();

        int count = rand.nextInt(3) + 1;

        int partySize = Math.max(1, party.getHeroes().size());
        int partyAvg = Math.max(1, party.getTotalLevel() / partySize);

        String[] ENEMY_TYPES = {
                "Goblin", "Ogre", "Skeleton", "Orc", "Bandit", "Slime"
        };

        java.util.Map<String, Integer> counts = new java.util.HashMap<>();

        for (int i = 0; i < count; i++) {
            int lvl = Math.max(1, partyAvg + rand.nextInt(3) - 1);

            String base = ENEMY_TYPES[rand.nextInt(ENEMY_TYPES.length)];

            int num = counts.getOrDefault(base, 0) + 1;
            counts.put(base, num);

            String name = base + "_" + num;

            enemies.add(new Enemy(
                    name,
                    lvl,
                    5 + lvl,             // attack
                    Math.max(1, lvl),    // defense
                    70 + lvl * 15,       // hp
                    0
            ));
        }

        return enemies;
    }
}
