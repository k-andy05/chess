package model;

import chess.ChessBoard;

public class GameData {
    public int gameID;
    public String whiteUsername;
    public String blackUsername;
    public String gameName;
    public ChessBoard game;

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessBoard game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }
}
