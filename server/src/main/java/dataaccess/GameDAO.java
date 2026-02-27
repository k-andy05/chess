package dataaccess;

import chess.ChessBoard;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameDAO {

    private static final ArrayList<GameData> games = new ArrayList<>();

    public static void clearAllGame() {
        games.clear();
    }

    public static ArrayList<GameData> getGames() {
        return new ArrayList<>(games);
//        ArrayList<GameData> gameList = new ArrayList<>();
//        ChessBoard test_board = new ChessBoard();
//        test_board.resetBoard();
//        int gameID = new Random().nextInt(100) + 1;
//        gameList.add(new GameData(gameID, "whitePlayer1", "blackPlayer2", "test_game", test_board));
//        return gameList;
    }

    public static void joinGame(String playerColor, int gameID) {}

    public static GameData getGame(int gameID) {
        ChessBoard test_board = new ChessBoard();
        test_board.resetBoard();
        return new GameData(1111, "whitePlayer3", "blackPlayer4", "test_game2", test_board);
    }
}
