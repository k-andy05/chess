package client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import chess.ChessMove;
import chess.ChessPiece.PieceType;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class GameplayClient implements ClientState, NotificationHandler{
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private String[][] board = new String[8][8];
    private ChessGame game;
    private final Scanner scanner;

    public GameplayClient(ServerFacade server, Scanner scanner) throws ResponseException {
        this.server = server;
        this.ws = new WebSocketFacade(server.serverUrl , this);
        this.scanner = scanner;
        ws.sendCommand(new UserGameCommand(CommandType.CONNECT, server.authToken, server.gameID));
    }

    @Override
    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            switch (cmd) {
                case "redraw" -> {
                    if (params.length == 0) {
                        printBoard(null, null);
                        return "";
                    } throw new InvalidRequestException(400, "Error: Incorrect number of inputs for redraw");
                }
                case "leave" -> {
                    if (params.length == 0) {
                        ws.sendCommand(new UserGameCommand(CommandType.LEAVE, server.authToken, server.gameID));
                        return "EXIT_GAME";
                    } throw new InvalidRequestException(400, "Error: Incorrect number of inputs for leave");
                }
                case "move" -> {
                    ChessMove chessMove = makeChessMove(params);
                    MakeMoveCommand moveCommand = new MakeMoveCommand(server.authToken, server.gameID, chessMove);
                    ws.sendCommand(moveCommand);
                    return "";
                }
                case "resign" -> {
                    if (params.length == 0) {
                        System.out.print("Are you sure you want to resign? (yes/no) ");
                        String confirmation = scanner.nextLine();
                        if (confirmation.equals("yes")) {
                            ws.sendCommand(new UserGameCommand(CommandType.RESIGN, server.authToken, server.gameID));
                            return "";
                        } else if (confirmation.equals("no")) {
                            return "";
                        } else {
                            System.out.print("Command unclear... try the resign command again");
                            return "";
                        }
                    } throw new InvalidRequestException(400, "Error: Incorrect number of inputs for resign");
                }
                case "highlight" -> {
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
                startRow = params[0].charAt(1) - '0';
                endCol = params[1].charAt(0) - 'a' + 1;
                endRow = params[1].charAt(1) - '0';
            } catch (NumberFormatException e) {
                throw new InvalidRequestException(
                        400, "Error: input type must be <Letter><Number> for start and end positions.");
            }
            ChessPosition startPosition = new ChessPosition(startRow, startCol);
            ChessPosition endPosition = new ChessPosition(endRow, endCol);
            PieceType promotionPiece = null;
            if ((game.getTeamTurn() == ChessGame.TeamColor.WHITE && endRow == 8) ||
                    game.getTeamTurn() == ChessGame.TeamColor.BLACK && endRow == 1) {
                System.out.println("Options for promotion piece input are queen, rook, bishop, or knight");
                String inputPromotionPiece = scanner.nextLine();
                promotionPiece = switch (inputPromotionPiece.toLowerCase()) {
                    case "queen" -> PieceType.QUEEN;
                    case "rook" -> PieceType.ROOK;
                    case "bishop" -> PieceType.BISHOP;
                    case "knight" -> PieceType.KNIGHT;
                    default -> throw new InvalidRequestException(400,
                            "Error: options for promotion piece input are queen, rook, bishop, or knight");
                };
            }
            return new ChessMove(startPosition, endPosition, promotionPiece);
        }
        throw new InvalidRequestException(401,
                "Error: invalid number of inputs for move. Example format: 'move <start> <end>'");
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
    public void notify(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        ServerMessage.ServerMessageType msgType = serverMessage.getServerMessageType();
        switch (msgType) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                this.game = loadGameMessage.getGame();
                updateBoard(this.game.getBoard());
                printBoard(null, null);
            }
            case ERROR -> {
                ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                System.out.println(SET_TEXT_COLOR_RED + errorMessage.getErrorMsg() + RESET_TEXT_COLOR);

            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                System.out.println(SET_TEXT_COLOR_GREEN + notificationMessage.getMessage() + RESET_TEXT_COLOR);

            }
        }
    }

    private void printBoard(ChessPosition startPosition, Collection<ChessMove> validMoves) {
        System.out.print(ERASE_SCREEN);
        boolean blackTeam = "BLACK".equals(server.playerColor);
        System.out.print("\n  ");
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
                int posCol = blackTeam ? (8 - col) : (col + 1);
                ChessPosition currentSquare = new ChessPosition(rowLabel, posCol);
                boolean isHighlight = false;
                if (validMoves != null) {
                    for (ChessMove move : validMoves) {
                        if (move.getEndPosition().equals(currentSquare)) {
                            isHighlight = true;
                            break;
                        }
                    }
                }
                String backGround;
                if (startPosition != null && startPosition.equals(currentSquare) && this.game.getBoard().getPiece(currentSquare) != null) {
                    backGround = SET_BG_COLOR_MAGENTA; // Highlight the piece you selected
                } else if (isHighlight) {
                    backGround = ((rowIndex + colIndex) % 2 == 0) ? SET_BG_COLOR_GREEN : SET_BG_COLOR_DARK_GREEN;
                } else {
                    backGround = ((rowIndex + colIndex) % 2 == 0) ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                }
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

    private String highlight(String[] params) throws InvalidRequestException {
        if (params.length != 1) {
            throw new InvalidRequestException(400, "Error: Incorrect number of inputs for highlight command");
        }
        if (this.game == null) {
            throw new InvalidRequestException(400, "Error: No active game state");
        }
        try {
            int col = params[0].charAt(0) - 'a' + 1;
            int row = params[0].charAt(1) - '0';
            ChessPosition startPosition = new ChessPosition(row, col);

            var validMoves = this.game.validMoves(startPosition);

            printBoard(startPosition, validMoves);
            return "";
        } catch (Exception e) {
            throw new InvalidRequestException(400, "Error: startPosition of piece is incorrect/invalid");
        }
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
