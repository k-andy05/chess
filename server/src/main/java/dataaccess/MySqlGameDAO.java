package dataaccess;

import exception.DataAccessException;
import model.GameData;

import java.util.ArrayList;

public class MySqlGameDAO implements GameDAO {
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
}
