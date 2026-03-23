package database;

import battle.Unit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class GameSaveDAO {

    //with the use of ai

    private final DatabaseConnector db;

    public GameSaveDAO() {
        this.db = DatabaseConnector.getInstance();
    }

    // Saves the party state and campaign progress
    public void saveCampaignProgress(int userId, String partyName, int currentRoom, int gold, List<Unit> heroes) {
        String insertPartySql = "INSERT INTO Parties (user_id, party_name, is_active_campaign, current_room, gold) VALUES (?, ?, true, ?, ?)";
        String insertHeroSql = "INSERT INTO Heroes (party_id, hero_name, level, hp_current, hp_max, mana_current, mana_max, attack, defense) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = db.getConnection();
            if (conn != null) {
                // 1. Save the Party to get the generated party_id
                PreparedStatement partyStmt = conn.prepareStatement(insertPartySql, Statement.RETURN_GENERATED_KEYS);
                partyStmt.setInt(1, userId);
                partyStmt.setString(2, partyName);
                partyStmt.setInt(3, currentRoom);
                partyStmt.setInt(4, gold);
                partyStmt.executeUpdate();

                ResultSet rs = partyStmt.getGeneratedKeys();
                int partyId = -1;
                if (rs.next()) {
                    partyId = rs.getInt(1);
                }

                // 2. Save each Hero in the list to the Heroes table
                if (partyId != -1) {
                    PreparedStatement heroStmt = conn.prepareStatement(insertHeroSql);
                    for (Unit hero : heroes) {
                        heroStmt.setInt(1, partyId);
                        heroStmt.setString(2, hero.getName());
                        heroStmt.setInt(3, hero.getLevel());
                        heroStmt.setInt(4, hero.getHp());
                        heroStmt.setInt(5, hero.getHp()); // maxHp isn't exposed by a getter in Unit yet, using current as placeholder
                        heroStmt.setInt(6, hero.getMana());
                        heroStmt.setInt(7, hero.getMana()); // maxMana placeholder
                        heroStmt.setInt(8, hero.getAttack());
                        heroStmt.setInt(9, hero.getDefense());
                        heroStmt.addBatch(); // Adds this hero to a batch for efficient saving
                    }
                    heroStmt.executeBatch(); // Executes the batch save
                    System.out.println("Campaign and party successfully saved to the database.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saving campaign: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
