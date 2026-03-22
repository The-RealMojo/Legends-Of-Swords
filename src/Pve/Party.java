package Pve;

import battle.Unit;
import java.util.*;

public class Party {

    private List<Unit> members;
    private int gold;
    private int experience;

    public Party() {
        members = new ArrayList<>();
        gold = 0;
        experience = 0;
    }

    public void addMember(Unit u) {
        members.add(u);
    }

    public List<Unit> getMembers() {
        return members;
    }

    public int getTotalLevel() {
        int sum = 0;
        for (Unit u : members) {
            sum += u.getLevel();
        }
        return sum;
    }

    public boolean hasAliveMembers() {
        for (Unit u : members) {
            if (u.isAlive()) return true;
        }
        return false;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public int getGold() {
        return gold;
    }

    public void gainExperience(int exp) {
        experience += exp;
    }
}