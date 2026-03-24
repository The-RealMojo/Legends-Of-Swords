package Pve;

import battle.Hero;
import java.util.*;

public class InnRoom extends Room {

    private Scanner scanner = new Scanner(System.in);

    public InnRoom(int floor) {
        super(floor);
    }

    @Override
    public void execute(Party party) {
        System.out.println("Inn Room at floor " + floor);


        healHeroes(party);


        shop(party);


        recruitHero(party);
    }

    private void healHeroes(Party party) {

        System.out.println("Do you want to heal your heroes? (yes/no)");

        String choice = scanner.nextLine().trim().toLowerCase();

        if (!choice.equals("yes")) return;

        for (Hero h : party.getHeroes()) {

            System.out.println("Heal " + h.getName() + "? (yes/no)");
            String heroChoice = scanner.nextLine().trim().toLowerCase();

            if (heroChoice.equals("yes")) {

                int cost = 50; // example cost per hero

                if (party.getGold() >= cost) {

                    party.addGold(-cost);
                    h.restoreHp(9999);  // full HP
                    h.restoreMana(9999); // full Mana
                    System.out.println(h.getName() + " fully healed!");

                } else {

                    System.out.println("Not enough gold to heal " + h.getName());
                }
            }
        }
    }

    private void shop(Party party) {

        System.out.println("Do you want to visit the shop? (yes/no)");

        String choice = scanner.nextLine().trim().toLowerCase();

        if (!choice.equals("yes")) return;

        System.out.println("Available items: ");
        System.out.println("1. Bread (+20 HP) - 200 Gold");
        System.out.println("2. Water (+10 Mana) - 150 Gold");
        System.out.println("Enter item number to buy or 0 to skip:");

        int itemChoice = scanner.nextInt();

        scanner.nextLine(); // consume newline

        switch (itemChoice) {
            case 1:
                if (party.getGold() >= 200) {
                    party.addGold(-200);
                    for (Hero h : party.getHeroes()) {
                        h.restoreHp(20);
                    }
                    System.out.println("Bought Bread (+20 HP)");
                } else {
                    System.out.println("Not enough gold.");
                }
                break;
            case 2:
                if (party.getGold() >= 150) {
                    party.addGold(-150);
                    for (Hero h : party.getHeroes()) {
                        h.restoreMana(10);
                    }
                    System.out.println("Bought Water (+10 Mana)");
                } else {
                    System.out.println("Not enough gold.");
                }
                break;
            default:
                System.out.println("Skipped shop.");
        }
    }

    private void recruitHero(Party party) {

        if (floor > 10) return;
        if (party.getHeroes().size() >= 5) return;

        System.out.println("Do you want to recruit a new hero? (yes/no)");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (!choice.equals("yes")) return;

        Random rand = new Random();
        int level = rand.nextInt(4) + 1;
        int cost = (level == 1) ? 0 : level * 200;

        System.out.println("Found hero (Level " + level + ") Cost: " + cost);

        if (party.getGold() >= cost) {
            party.addGold(-cost);
            Hero newHero = new Hero(
                    "Recruit" + level,
                    level,
                    level * 5,
                    level * 3,
                    level * 20,
                    level * 10
            );
            party.getHeroes().add(newHero);
            System.out.println("Hero recruited!");
        } else {
            System.out.println("Not enough gold to recruit.");
        }
    }
}