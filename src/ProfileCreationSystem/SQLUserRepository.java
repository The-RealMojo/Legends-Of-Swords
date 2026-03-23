package ProfileCreationSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLUserRepository implements IUserDB {
    private IDBConnection dbConnection;

    public SQLUserRepository(IDBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public boolean usernameExists(String username) {
        boolean exists = false;

        try {
            Connection conn = dbConnection.getConnection();
            String sql = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exists = true;
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }

    @Override
    public void save(UserProfile user) {
        try {
            Connection conn = dbConnection.getConnection();
            String sql = "INSERT INTO Users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());

            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserProfile findUsername(String username) {
        UserProfile user = null;

        try {
            Connection conn = dbConnection.getConnection();
            String sql = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");

                user = new UserProfile();
                user.setUserId(String.valueOf(userId));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setScores(0);
                user.setRankings(0);
                user.setCampaignProgress(0);

                loadSavedParties(user, userId);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    private void loadSavedParties(UserProfile user, int userId) {
        try {
            Connection conn = dbConnection.getConnection();
            String sql = "SELECT party_name FROM Parties WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                user.addSavedParty(rs.getString("party_name"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasSavedParty(String username) {
        boolean hasParty = false;

        try {
            Connection conn = dbConnection.getConnection();
            String sql = """
                    SELECT 1
                    FROM Parties p
                    JOIN Users u ON p.user_id = u.user_id
                    WHERE u.username = ?
                    LIMIT 1
                    """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            hasParty = rs.next();

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hasParty;
    }

    @Override
    public List<String> getSavedParties(String username) {
        List<String> parties = new ArrayList<>();

        try {
            Connection conn = dbConnection.getConnection();
            String sql = """
                    SELECT p.party_name
                    FROM Parties p
                    JOIN Users u ON p.user_id = u.user_id
                    WHERE u.username = ?
                    ORDER BY p.party_name
                    """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                parties.add(rs.getString("party_name"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parties;
    }

    @Override
    public void recordPvpResult(String winnerUsername, String loserUsername) {
        try {
            Connection conn = dbConnection.getConnection();

            String winSql = "UPDATE Users SET pvp_wins = pvp_wins + 1 WHERE username = ?";
            PreparedStatement winStmt = conn.prepareStatement(winSql);
            winStmt.setString(1, winnerUsername);
            winStmt.executeUpdate();
            winStmt.close();

            String lossSql = "UPDATE Users SET pvp_losses = pvp_losses + 1 WHERE username = ?";
            PreparedStatement lossStmt = conn.prepareStatement(lossSql);
            lossStmt.setString(1, loserUsername);
            lossStmt.executeUpdate();
            lossStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
