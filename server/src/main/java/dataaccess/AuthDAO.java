package dataaccess;

import exception.DataAccessException;
import model.*;

import java.util.ArrayList;

public interface AuthDAO {
    void clearAllAuth() throws DataAccessException;

    void createAuth(String authToken, String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}

