package dataaccess;

import exception.DataAccessException;
import model.UserData;

public class MySqlUserDAO implements UserDAO {
    @Override
    public void clearAllUser() throws DataAccessException {

    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public UserData getUserByEmail(String email) throws DataAccessException {
        return null;
    }
}
