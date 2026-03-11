package dataaccess;

import exception.DataAccessException;
import model.*;

import java.util.ArrayList;

public interface DataAccess {
    void clearAllAuth() throws DataAccessException;

    void createAuth(String authToken, String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void clearAllGame() throws DataAccessException;

    ArrayList<GameData> getGames() throws DataAccessException;

    void userJoin(String playerColor, int GameID, String username) throws DataAccessException;

    GameData getGameByName(String gameName) throws DataAccessException;

    GameData getGameByID(Integer gameID) throws DataAccessException;

    void createGame(String gameName) throws DataAccessException;

    void clearAllUser() throws DataAccessException;

    void createUser(String username, String password, String email) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    UserData getUserByEmail(String email) throws DataAccessException;
}
