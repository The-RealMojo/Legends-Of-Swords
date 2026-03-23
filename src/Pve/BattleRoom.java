package Pve;

import battle.*;
import java.util.*;

public class BattleRoom extends Room {

    public BattleRoom(int floor) {
        super(floor);
    }

    @Override
    public void execute(Party party) {
        System.out.println("Battle Room at floor " + floor);

        List<Unit> enemies = generateEnemies(party);

        List<Unit> heroesAsUnits = new ArrayList<>(party.getHeroes());

        Battle battle = new Battle(heroesAsUnits, enemies);
        battle.startBattle();
    }

    private List<Unit> generateEnemies(Party party) {
        List<Unit> enemies = new ArrayList<>();
        Random rand = new Random();

        int totalLevel = party.getTotalLevel();
        int count = rand.nextInt(5) + 1;

        for (int i = 0; i < count; i++) {
            int level = Math.max(1, totalLevel - 10 + rand.nextInt(11));

            enemies.add(new Enemy(
                    "Enemy" + i,
                    level,
                    level * 5,
                    level * 3,
                    level * 20,
                    level * 10
            ));
        }

        return enemies;
    }
}