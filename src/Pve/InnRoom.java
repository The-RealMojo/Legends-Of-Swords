package Pve;

import battle.*;
import java.util.*;

public class InnRoom extends Room {

    public InnRoom(int roomNumber) {
        super(roomNumber);
    }

    @Override
    public void enter(Party party) {
        System.out.println("Entering Inn Room #" + roomNumber);

        healParty(party);

        if (roomNumber <= 10) {
            recruitHero(party);
        }
    }

    private void healParty(Party party) {
        for (Unit u : party.getMembers()) {
            u.restoreHp(9999);
            u.restoreMana(9999);
        }
        System.out.println("Party fully healed.");
    }

    private void recruitHero(Party party) {
        if (party.getMembers().size() >= 5) return;

        int level = (int)(Math.random() * 4) + 1;

        // your rule: level 1 = free, else 100 per level
        int cost = (level == 1) ? 0 : level * 100;

        Hero newHero = new Hero("Recruit", level,
                10 + level, 5 + level,
                100, 50);

        if (party.getGold() >= cost) {
            party.addGold(-cost);
            party.addMember(newHero);

            System.out.println("Recruited hero (Level " + level + ") for " + cost + " gold");
        }
    }
}