package dataaccess;

import exception.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class MySqlUserDAO implements UserDAO {

    public MySqlUserDAO () {
        configureTable();
    }

    private void configureTable() {
//        String dropStatement = "DROP TABLE IF EXISTS userdata";
        String statement = """
            CREATE TABLE IF NOT EXISTS userdata (
                username VARCHAR(255) NOT NULL PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL
            );
            """;
        try (Connection conn = DatabaseManager.getConnection();
//             PreparedStatement dropPs = conn.prepareStatement(dropStatement);
             PreparedStatement ps = conn.prepareStatement(statement)) {
//            dropPs.executeUpdate();
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to initialize userdata table");
        }
    }

    @Override
    public void clearAllUser() throws DataAccessException {
        String statement = "TRUNCATE TABLE userdata";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to clear userdata table");
        }
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        String statement = "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, username);
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            ps.setString(2, hashedPassword);
            ps.setString(3, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to add new input into userdata");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT username, password, email FROM userdata WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to get user data by username");
        }
        return null;
    }

    @Override
    public UserData getUserByEmail(String email) throws DataAccessException {
        String statement = "SELECT username, password, email FROM userdata WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to get user data by email");
        }
        return null;
    }
}
