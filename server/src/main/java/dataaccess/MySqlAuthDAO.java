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
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearAllAuth() throws DataAccessException {
        String statement = "TRUNCATE TABLE authdata";
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
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
            throw new DataAccessException(e.getMessage());
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
            throw new DataAccessException(e.getMessage());
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
            throw new DataAccessException(e.getMessage());
        }
    }

//    private final String[] createStatements = {
//        """
//        CREATE TABLE IF NOT EXISTS AuthData (
//          `authToken` TEXT DEFAULT NULL,
//          `
//        """
//    };

//    private void executeUpdate(String statement, Object... params) throws DataAccessException {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (PreparedStatement ps = conn.prepareStatement(statement)) {
////            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
//                for (int i = 0; i < params.length; i++) {
//                    Object param = params[i];
//                    if (param instanceof String p) ps.setString(i + 1, p);
//                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
//                    else if (param instanceof AuthData p) ps.setString(i + 1, p.toString());
//                    else if (param == null) ps.setNull(i + 1, NULL);
//                }
//                ps.executeUpdate();
////                ResultSet rs = ps.getGeneratedKeys();
////                if (rs.next()) {
////                    return rs.getInt(1);
////                }
////                return 0;
//            }
//        } catch (SQLException e) {
//            throw new DataAccessException(e.getMessage(), "unable to update database");
//        }
//    }

//    public void clearAllAuth() throws DataAccessException {
//        String var = "TRUNCATE TABLE auth";
//        try {
//            Connection conn = DatabaseManager.getConnection();
//            PreparedStatement ps = conn.prepareStatement(var);
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            throw new DataAccessException(e.getMessage());
//        }
//    }

}
