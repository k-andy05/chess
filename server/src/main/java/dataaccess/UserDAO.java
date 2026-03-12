package dataaccess;

import exception.DataAccessException;
import model.*;

public interface UserDAO {

    void clearAllUser() throws DataAccessException;

    void createUser(String username, String password, String email) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    UserData getUserByEmail(String email) throws DataAccessException;
}

