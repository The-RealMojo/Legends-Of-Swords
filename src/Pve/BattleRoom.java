package Pve;

import battle.*;
import java.util.*;

public class BattleRoom extends Room {

    public BattleRoom(int roomNumber) {
        super(roomNumber);
    }

    @Override
    public void enter(Party party) {
        System.out.println("Entering Battle Room #" + roomNumber);

        List<Unit> enemies = generateEnemies(party);

        Battle battle = new Battle(party.getMembers(), enemies);
        battle.startBattle();

        if (party.hasAliveMembers()) {
            rewardPlayer(party, enemies);
        }
    }

    private List<Unit> generateEnemies(Party party) {
        List<Unit> enemies = new ArrayList<>();

        int playerLevel = party.getTotalLevel();
        int enemyCount = (int)(Math.random() * 5) + 1;

        for (int i = 0; i < enemyCount; i++) {
            int level = Math.max(1, playerLevel - 5 + (int)(Math.random() * 10));
            enemies.add(new Enemy("Enemy" + i, level,
                    10 + level, 5 + level,
                    50 + level * 10, 20));
        }

        return enemies;
    }

    private void rewardPlayer(Party party, List<Unit> enemies) {
        int totalExp = 0;
        int totalGold = 0;

        for (Unit e : enemies) {
            totalExp += 50 * e.getLevel();
            totalGold += 75 * e.getLevel();
        }

        party.addGold(totalGold);
        party.gainExperience(totalExp);

        System.out.println("Gained EXP: " + totalExp + ", Gold: " + totalGold);
    }
}