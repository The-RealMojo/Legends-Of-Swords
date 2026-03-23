package Pve;

import battle.Hero;
import java.util.*;

public class Campaign {
    private Party party;
    private int currentRoom;
    private final int maxRooms = 30;

    public Campaign(List<Hero> heroes) {
        this.party = new Party(heroes);
        this.currentRoom = 0;
    }

    public void start() {
        System.out.println("Campaign started!");

        while (currentRoom < maxRooms) {
            currentRoom++;

            Room room = RoomFactory.createRoom(currentRoom, party.getTotalLevel());
            room.execute(party);

            party.addGold(100);
        }

        endCampaign();
    }

    private void endCampaign() {
        System.out.println("Campaign finished!");
        System.out.println("Final Score: " + calculateScore());
    }

    private int calculateScore() {
        return (party.getTotalLevel() * 100) + (party.getGold() * 10);
    }
}