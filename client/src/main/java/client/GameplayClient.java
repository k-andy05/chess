package client;

import java.util.Arrays;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import request.ListRequest;
import result.ListResult;
import ui.EscapeSequences.*;

import static ui.EscapeSequences.*;

public class GameplayClient implements ClientState {
    private final ServerFacade server;
    private String[][] board = new String[8][8];
//    private Integer gameID;

    public GameplayClient(ServerFacade server) {
        this.server = server;
        ChessBoard board = new ChessBoard();
        board.resetBoard();
//        ChessBoard currentBoard = getGame();
//        System.out.print("Successfully got game! no errors");
//        System.out.println("Board that was gathered: " + currentBoard);
        updateBoard(board);
//        System.out.print("Successfully updated board! no errors");
        printBoard();
//        System.out.print("Successfully printed board! no errors!");
    }

    @Override
    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            switch (cmd) {
                case "leave" -> {
                    return leave();
                }
                case "print" -> {
                    printBoard();
                    return "";
                }
                default -> {
                    return help();
                }
            }
        } catch (Exception e) {
            throw new InvalidRequestException(401, e.getMessage());
        }
    }

    public String leave() {
        return "EXIT_GAME";
    }

    private void printBoard() {
        System.out.print(ERASE_SCREEN);

        boolean blackTeam = "BLACK".equals(server.playerColor);

        // Top Column names
        System.out.print("  ");
        for (int col = 0; col < 8; col++) {
            char colHeader = blackTeam ? (char) ('h' - col) : (char) ('a' + col);
            System.out.print(" " + colHeader + " ");
        }
        System.out.println();

        // Body
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

        // Bottom column names
        System.out.print("  ");
        for (int col = 0; col < 8; col++) {
            char colHeader = blackTeam ? (char) ('h' - col) : (char) ('a' + col);
            System.out.print(" " + colHeader + " ");
        }
        System.out.println();


//        for (int row = 0; row < 8; row++) {
//            System.out.print((8 - row) + " ");
//            for (int col = 0; col < 8; col++) {
//                String bg = ((row + col) % 2 == 0) ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
//                System.out.print(bg + board[row][col] + RESET_BG_COLOR + RESET_TEXT_COLOR);
//            }
//            System.out.println(" " + (8 - row));
//        }
//        System.out.print("  ");
//        for (char col = 'a'; col <= 'h'; col++) {
//            System.out.print(" " + col + " ");
//        }
//        System.out.println();
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

    private ChessBoard getGame() {
        ListRequest request = new ListRequest(server.authToken);
        System.out.println("This is ListRequest obj: " + request);
        try {
            ListResult result = server.list(request);
            System.out.println("This is ListResult obj: " + result);
            for (GameData game : result.gameList) {
                if (game.gameID == server.gameID) {
                    System.out.println("The right game object was found! " + game + " with this ChessBoard " + game.game);
                    return game.game.getBoard();
                }
            }
        } catch (Exception e) {
            throw new InvalidRequestException(401, e.getMessage());
        }
        throw new InvalidRequestException(401, "Error: not able to process inputs??? idk");
    }

    @Override
    public String help() {
        return """
                leave - game and go to logged-in display
                ... - NEED TO ADD ACTIONABLE COMMANDS FOR CHESS BOARD
                help - with possible commands
                """;
    }
}
