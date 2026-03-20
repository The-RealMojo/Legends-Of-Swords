package ProfileCreationSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            String sql = "SELECT 1 FROM Users WHERE username = ?";
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
}
