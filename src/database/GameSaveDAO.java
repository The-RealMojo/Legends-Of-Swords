package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameSaveDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnector.getInstance().getConnection();
    }

    // =========================
    // USER METHODS
    // =========================

    public boolean createUser(String username, String password) {
        String sql = "INSERT INTO Users (username, password) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean authenticate(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            return false;
        }
    }

    public int getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM Users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public String getPasswordByUsername(String username) {
        String sql = "SELECT password FROM Users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("password");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // PARTY METHODS
    // =========================

    public boolean hasSavedParty(int userId) {
        String sql = "SELECT * FROM Parties WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            return false;
        }
    }

    public List<String> getSavedParties(int userId) {
        List<String> parties = new ArrayList<>();

        String sql = "SELECT party_name FROM Parties WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                parties.add(rs.getString("party_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parties;
    }

    // =========================
    // PvP METHODS
    // =========================

    public void recordPvpResult(String winnerUsername, String loserUsername) {
        int winnerId = getUserIdByUsername(winnerUsername);
        int loserId = getUserIdByUsername(loserUsername);

        String sql = "INSERT INTO PvpMatches (winner_id, loser_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, winnerId);
            stmt.setInt(2, loserId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // PvE METHODS
    // =========================

    public void saveCampaignProgress(int userId, String partyName, int currentRoom, int gold, List<battle.Unit> units) {
        String sql = "INSERT INTO Parties (user_id, party_name, current_room, gold) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, partyName);
            stmt.setInt(3, currentRoom);
            stmt.setInt(4, gold);

            stmt.executeUpdate();
            System.out.println("Campaign saved successfully for user " + userId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Pve.Campaign fetchSavedCampaign(int userId) {
        String sql = "SELECT * FROM Parties WHERE user_id = ? ORDER BY party_id DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                List<battle.Hero> fetchedHeroes = new ArrayList<>();
                // Default hero to prevent null errors
                fetchedHeroes.add(new battle.Hero("Hero", 1, 10, 5, 100, 50));

                Pve.Party savedParty = new Pve.Party(fetchedHeroes);
                return new Pve.Campaign(fetchedHeroes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}