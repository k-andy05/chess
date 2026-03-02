package dataaccess;

import chess.ChessBoard;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameDAO {
    private final HashMap<String, GameData> games = new HashMap<>();
//    private final ArrayList<GameData> games = new ArrayList<>();
    private int nextGameNumber = 1;

    public void clearAllGame() {
        games.clear();
    }

    public ArrayList<GameData> getGames() {
        return new ArrayList<>(games.values());
    }

    public void userJoin(String playerColor, int gameID, String username) {
        GameData desiredGame = this.getGameByID(gameID);
        if (playerColor.equals("WHITE")) {
            desiredGame.whiteUsername = username;
        }
        else {
            desiredGame.blackUsername = username;
        }
    }

    public GameData getGameByName(String gameName) {
        return games.get(gameName);
    }

    public GameData getGameByID(Integer gameID) {
        for (GameData game : games.values()) {
            if (game.gameID == gameID) {
                return game;
            }
        }
        return null;
    }

    public void createGame(String gameName) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        GameData newGame = new GameData(nextGameNumber, "", "", gameName, board);
        nextGameNumber++;
        games.put(gameName, newGame);
    }
}
