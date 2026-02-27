package dataaccess;

import chess.ChessBoard;
import model.GameData;

import java.util.ArrayList;

public class GameDAO {

    public static void clearAllGame() {}

    public static ArrayList<GameData> getGames() {
        ArrayList<GameData> gameList = new ArrayList<>();
        ChessBoard test_board = new ChessBoard();
        test_board.resetBoard();
        gameList.add(new GameData(1222, "whitePlayer1", "blackPlayer2", "test_game", test_board));
        return gameList;
    }

    public static void joinGame(String playerColor, int gameID) {}

    public static GameData getGame(int gameID) {
        ChessBoard test_board = new ChessBoard();
        test_board.resetBoard();
        return new GameData(1111, "whitePlayer3", "blackPlayer4", "test_game2", test_board);
    }
}
