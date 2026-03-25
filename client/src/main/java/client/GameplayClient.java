package client;

import java.util.Arrays;
import ui.EscapeSequences.*;

import static ui.EscapeSequences.*;

public class GameplayClient implements ClientState {
    private final ServerFacade server;
    private String[][] board = new String[8][8];
//    private Integer gameID;

    public GameplayClient(ServerFacade server) {
        this.server = server;
//        this.gameID = gameID;
        this.printBoard();
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

        // Column names
        System.out.print("  ");
        for (char col = 'a'; col <= 'h'; col++) {
            System.out.print("  " + col + " ");
        }
        System.out.println();
        for (char row = 0; row < 8; row++) {
            System.out.print((8 - row) + " ");
            for (int col = 0; col < 8; col++) {
                String bg = ((row + col) % 2 == 0) ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                System.out.print(bg + board[row][col] + RESET_BG_COLOR);
            }
            System.out.println(" " + (8 - row));
        }
        System.out.print("  ");
        for (char col = 'a'; col <= 'h'; col++) {
            System.out.print("  " + col + " ");
        }
        System.out.println();
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
