package dataaccess;

import exception.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySqlAuthDAO {

    public void clearAllAuth() throws DataAccessException {
        String var = "TRUNCATE TABLE auth";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(var);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
