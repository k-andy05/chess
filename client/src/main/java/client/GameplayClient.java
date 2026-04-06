package client;

import java.util.Arrays;
import java.util.EmptyStackException;

import chess.ChessBoard;
import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.GameData;
import request.ListRequest;
import result.ListResult;
import ui.EscapeSequences.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class GameplayClient implements ClientState, NotificationHandler{
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private String[][] board = new String[8][8];

    public GameplayClient(ServerFacade server) throws ResponseException {
        this.server = server;
        this.ws = new WebSocketFacade(server.serverUrl , this);
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        updateBoard(board);
        printBoard();
    }

    @Override
    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            switch (cmd) {
                case "redraw" -> {
                    printBoard();
                    return "";
                }
                case "leave" -> {
                    return leave();
                }
                case "move" -> {
                    return makeMove();
                }
                case "resign" -> {
                    return resign();
                }
                case "highlight" -> {
                    return highlight();
                }
                default -> {
                    return help();
                }
            }
        } catch (Exception e) {
            throw new InvalidRequestException(401, e.getMessage());
        }


//        try {
//            switch (cmd) {
//                case "leave" -> {
//                    return leave();
//                }
//                case "print" -> {
//                    printBoard();
//                    return "";
//                }
//                default -> {
//                    return help();
//                }
//            }
//        } catch (Exception e) {
//            throw new InvalidRequestException(401, e.getMessage());
//        }
    }

    public String leave() {
        return "EXIT_GAME";
    }

    private void printBoard() {
        System.out.print(ERASE_SCREEN);
        boolean blackTeam = "BLACK".equals(server.playerColor);
        System.out.print("  ");
        for (int col = 0; col < 8; col++) {
            char colHeader = blackTeam ? (char) ('h' - col) : (char) ('a' + col);
            System.out.print(" " + colHeader + " ");
        }
        System.out.println();
        for (int row = 0; row < 8; row++) {
            int rowLabel = blackTeam ? (row + 1) : (8 - row);
            int rowIndex = blackTeam ? (7 - row) : row;
            System.out.print(rowLabel +  " ");
            for (int col = 0; col < 8; col++) {
                int colIndex = blackTeam ? (7 - col) : col;
                String backGround = ((rowIndex + colIndex) % 2 == 0) ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                System.out.print(backGround + board[rowIndex][colIndex] + RESET_BG_COLOR + RESET_TEXT_COLOR);
            }
            System.out.println(" " + rowLabel);
        }
        System.out.print("  ");
        for (int col = 0; col < 8; col++) {
            char colHeader = blackTeam ? (char) ('h' - col) : (char) ('a' + col);
            System.out.print(" " + colHeader + " ");
        }
        System.out.println();
    }

    private void updateBoard(ChessBoard currentBoard) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var piece = currentBoard.getPiece(new chess.ChessPosition(row, col));
                if (piece == null) {
                    board[8 - row][col - 1] = EMPTY;
                } else {
                    board[8 - row][col - 1] = mapPiece(piece);
                }
            }

        }
    }

    private String mapPiece(chess.ChessPiece piece) {
        String textColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
        String pieceSymbol = switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
        };
        return textColor + pieceSymbol;
    }

    @Override
    public String help() {
        return """
                help - with possible commands
                redraw - chess board with current game status
                leave - current game interface and remove player from game
                move <start position> <end position> - a chess piece
                resign - from the game (will not remove player from the game)
                highlight <chesspiece position> - legal moves
                """;
    }

    @Override
    public void notify(ServerMessage notification) { // TODO implement switch cases
        ServerMessage.ServerMessageType msgType = notification.getServerMessageType();
        switch (msgType) {
            case LOAD_GAME -> {}
            case ERROR -> {}
            case NOTIFICATION -> {}
        }
    }
}
