package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import chess.ChessMove;
import chess.ChessPiece.PieceType;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.GameData;
import request.ListRequest;
import result.ListResult;
import ui.EscapeSequences.*;
import websocket.commands.UserGameCommand.*;
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
        ws.sendCommand(new UserGameCommand(CommandType.CONNECT, server.authToken, server.gameID));

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
                case "redraw" -> { // TODO implement getting current board from server
                    printBoard();
                    return "";
                }
                case "leave" -> {
                    ws.sendCommand(new UserGameCommand(CommandType.LEAVE, server.authToken, server.gameID));
                    return "";
                }
                case "move" -> {
                    ChessMove chessMove = makeChessMove(params);
                    UserGameCommand command = new UserGameCommand(CommandType.MAKE_MOVE, server.authToken, server.gameID);
                    ws.sendCommand(command);
                    return "";
                }
                case "resign" -> {
                    ws.sendCommand(new UserGameCommand(CommandType.RESIGN, server.authToken, server.gameID));
                    return "";
                }
                case "highlight" -> { // TODO implement getting current board and adjusting for possible moves in printing board itself
                    return highlight(params);
                }
                default -> {
                    return help();
                }
            }
        } catch (Exception e) {
            throw new InvalidRequestException(401, e.getMessage());
        }
    }

    private ChessMove makeChessMove(String ... params) throws InvalidRequestException {
        if (params.length == 2) {
            int startRow;
            int startCol;
            int endRow;
            int endCol;
            try {
                startCol = params[0].charAt(0) - 'a' + 1;
                startRow = params[0].charAt(1) - 0;
                endCol = params[1].charAt(0) - 'a' + 1;
                endRow = params[1].charAt(1) - 0;
            } catch (NumberFormatException e) {
                throw new InvalidRequestException(
                        400, "Error: input type must be <Letter><Number> for start and end positions.");
            }
            ChessPosition startPosition = new ChessPosition(startRow, startCol);
            ChessPosition endPosition = new ChessPosition(endRow, endCol);
            PieceType promotionPiece = null; // TODO figure out how to set propotionPiece type based on user input or stop and then ask user what piece they want it to be
            return new ChessMove(startPosition, endPosition, promotionPiece);

        }
        throw new InvalidRequestException(401, "Error: invalid number of inputs for move");
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
}
