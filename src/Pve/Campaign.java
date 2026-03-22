package Pve;

public class Campaign {

    private Party party;
    private int currentRoom;

    public Campaign(Party party) {
        this.party = party;
        this.currentRoom = 1;
    }

    public void start() {
        while (currentRoom <= 30 && party.hasAliveMembers()) {

            Room room = generateRoom();
            room.enter(party);

            currentRoom++;
        }

        endCampaign();
    }

    private Room generateRoom() {
        int totalLevel = party.getTotalLevel();

        int bonus = (totalLevel / 10) * 3;
        int battleChance = 60 + bonus;

        if (Math.random() * 100 < battleChance) {
            return new BattleRoom(currentRoom);
        } else {
            return new InnRoom(currentRoom);
        }
    }

    private void endCampaign() {
        int score = calculateScore();
        System.out.println("Campaign finished. Score: " + score);
    }

    private int calculateScore() {
        int levelScore = party.getTotalLevel() * 100;
        int goldScore = party.getGold() * 10;

        return levelScore + goldScore;
    }
}