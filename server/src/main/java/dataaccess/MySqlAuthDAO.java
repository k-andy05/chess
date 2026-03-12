package dataaccess;

import exception.DataAccessException;
import model.AuthData;

import java.sql.*;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO () {
        configureTable();
    }

    private void configureTable() throws DataAccessException {
        String statement = """
                CREATE TABLE IF NOT EXISTS authdata (
                    authToken VARCHAR(255) NOT NULL PRIMARY KEY,
                    username VARCHAR(255) NOT NULL
                );
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to initialize userdata table");
        }
    }

    @Override
    public void clearAllAuth() throws DataAccessException {
        String statement = "TRUNCATE TABLE authdata";
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to clear data from authdata table");
        }
    }

    @Override
    public void createAuth(String authToken, String username) throws DataAccessException {
        String statement = "INSERT INTO authdata (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to create authdata entry");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT authToken, username FROM authdata WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to get auth data by authToken");
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM authdata WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to delete auth data entry");
        }
    }
}
