package dataaccess;

import exception.DataAccessException;
import model.*;

import java.util.ArrayList;

public interface GameDAO {

    void clearAllGame() throws DataAccessException;

    ArrayList<GameData> getGames() throws DataAccessException;

    void userJoin(String playerColor, int gameID, String username) throws DataAccessException;

    GameData getGameByName(String gameName) throws DataAccessException;

    GameData getGameByID(Integer gameID) throws DataAccessException;

    void createGame(String gameName) throws DataAccessException;
}

