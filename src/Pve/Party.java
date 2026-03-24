package Pve;

import battle.Hero;
import java.util.*;

public class Party {
    private List<Hero> heroes;
    private int gold;

    public Party(List<Hero> heroes) {
        this.heroes = heroes;
        this.gold = 500; // start gold
    }

    public List<Hero> getHeroes() {
        return heroes;
    }


    public void addHero(Hero h) {
        if (h == null) return;

        if (heroes.size() >= 5) {
            System.out.println("Party is full. Cannot add more heroes.");
            return;
        }

        heroes.add(h);
    }

    public int getTotalLevel() {
        int sum = 0;
        for (Hero h : heroes) sum += h.getLevel();
        return sum;
    }

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        gold += amount;
        if (gold < 0) gold = 0;
    }
}