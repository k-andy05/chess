package dataaccess;

import exception.DataAccessException;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO {
    @Override
    public void clearAllAuth() throws DataAccessException {

    }

    @Override
    public void createAuth(String authToken, String username) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

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
