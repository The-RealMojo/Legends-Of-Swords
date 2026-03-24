package Pve;

import battle.Hero;
import java.util.*;

public class InnRoom extends Room {

    public InnRoom(int floor) {
        super(floor);
    }

    @Override
    public void execute(Party party) {
        System.out.println("Inn Room at floor " + floor);

        healAll(party);
        shop(party);
        recruitHero(party);
    }

    //
    private void healAll(Party party) {
        for (Hero h : party.getHeroes()) {
            h.restoreHp(9999);
            h.restoreMana(9999);
        }
        System.out.println("All heroes fully healed!");
    }

    //
    private void shop(Party party) {
        System.out.println("Visiting shop...");

        if (party.getGold() >= 200) {
            party.addGold(-200);

            for (Hero h : party.getHeroes()) {
                h.restoreHp(20);
            }

            System.out.println("Bought Bread (+20 HP)");
        }
        else if (party.getGold() >= 150) {
            party.addGold(-150);

            for (Hero h : party.getHeroes()) {
                h.restoreMana(10);
            }

            System.out.println("Bought Water (+10 Mana)");
        }
        else {
            System.out.println("Not enough gold to buy items.");
        }
    }

    //
    private void recruitHero(Party party) {
        if (floor > 10) return;
        if (party.getHeroes().size() >= 5) return;

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