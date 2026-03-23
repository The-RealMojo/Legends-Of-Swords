package Pve;

import java.util.Random;

public class RoomFactory {

    public static Room createRoom(int floor, int totalLevel) {
        Random rand = new Random();

        int bonus = (totalLevel / 10) * 3;
        int battleChance = 60 + bonus;

        int roll = rand.nextInt(100);

        if (roll < battleChance) {
            return new BattleRoom(floor);
        } else {
            return new InnRoom(floor);
        }
    }
}