package battle;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Create heroes
        Hero hero1 = new Hero("Knight", 5, 20, 10, 100, 30);
        Hero hero2 = new Hero("Mage", 4, 25, 5, 80, 50);

        // Create enemies
        Enemy enemy1 = new Enemy("Goblin", 3, 15, 5, 60, 10);
        Enemy enemy2 = new Enemy("Orc", 4, 18, 8, 90, 20);

        // Add heroes to player party
        List<Unit> heroes = new ArrayList<>();
        heroes.add(hero1);
        heroes.add(hero2);

        // Add enemies to enemy party
        List<Unit> enemies = new ArrayList<>();
        enemies.add(enemy1);
        enemies.add(enemy2);

        // Start battle
        Battle battle = new Battle(heroes, enemies);
        battle.startBattle();

        // Print final HP values
        System.out.println("\n--- Final HP ---");
        for (Unit u : heroes) {
            System.out.println(u.getName() + " HP: " + u.getHp());
        }
        for (Unit u : enemies) {
            System.out.println(u.getName() + " HP: " + u.getHp());
        }
    }
}


