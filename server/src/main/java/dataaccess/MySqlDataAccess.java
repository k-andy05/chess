package dataaccess;

import exception.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

import static dataaccess.DatabaseManager.*;

public class MySqlDataAccess implements DataAccess{

    public MySqlDataAccess() throws DataAccessException {
        createDatabase();
    }

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

    @Override
    public void clearAllGame() throws DataAccessException {

    }

    @Override
    public ArrayList<GameData> getGames() throws DataAccessException {
        return null;
    }

    @Override
    public void userJoin(String playerColor, int GameID, String username) throws DataAccessException {

    }

    @Override
    public GameData getGameByName(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGameByID(Integer gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void createGame(String gameName) throws DataAccessException {

    }

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
