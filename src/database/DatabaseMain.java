package database;

import battle.Hero;
import battle.Unit;
import java.util.ArrayList;
import java.util.List;

public class DatabaseMain {
    public static void main(String[] args) {
        System.out.println("--- Testing Campaign & Party Save Module ---");

        GameSaveDAO gameSaveDAO = new GameSaveDAO();

        // 1. Create dummy data using your friend's battle classes
        System.out.println("Creating a test party...");
        List<Unit> myParty = new ArrayList<>();
        myParty.add(new Hero("Arthur", 5, 20, 10, 100, 30));
        myParty.add(new Hero("Merlin", 4, 25, 5, 80, 50));

        // 2. Test saving the campaign progress and party status
        // Note: For this to work, a user with user_id = 1 must exist in your Users table.
        int testUserId = 1;
        String partyName = "The Round Table";
        int currentRoom = 12; // e.g., They made it to room 12
        int gold = 500;

        System.out.println("Saving campaign progress to database...");
        gameSaveDAO.saveCampaignProgress(testUserId, partyName, currentRoom, gold, myParty);

        System.out.println("Test complete! Check your MySQL database to see the saved party and heroes.");
    }
}
