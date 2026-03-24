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

    public void recordPvpResult(int winnerId, int loserId) {
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

        String sql = "INSERT INTO Parties (user_id, party_name) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, partyName);

            stmt.executeUpdate();

            // NOTE: This is a simplified version
            // You can expand later to store:
            // - room
            // - gold
            // - hero stats

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}