package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<String, GameData> games = new HashMap<>();
    private int nextGameNumber = 1;

    @Override
    public void clearAllGame() {
        games.clear();
    }

    @Override
    public ArrayList<GameData> getGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void userJoin(String playerColor, int gameID, String username) {
        GameData desiredGame = this.getGameByID(gameID);
        if (playerColor.equals("WHITE")) {
            desiredGame.whiteUsername = username;
        }
        else {
            desiredGame.blackUsername = username;
        }
    }

    @Override
    public GameData getGameByName(String gameName) {
        return games.get(gameName);
    }

    @Override
    public GameData getGameByID(Integer gameID) {
        for (GameData game : games.values()) {
            if (game.gameID == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void createGame(String gameName) {
        ChessGame game = new ChessGame();
//        ChessBoard board = new ChessBoard();
        game.getBoard().resetBoard();
        GameData newGame = new GameData(nextGameNumber, null, null, gameName, game);
        nextGameNumber++;
        games.put(gameName, newGame);
    }
}
