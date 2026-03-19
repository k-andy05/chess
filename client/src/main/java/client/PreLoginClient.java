package client;

import java.util.Arrays;
import java.util.Scanner;

import ui.EscapeSequences;


public class PreLoginClient {
    private final ServerFacade server;
//    private final WebSocketFacade ws;
//
    public PreLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
//        ws = new WebSocketFacade(serverUrl, this);
    }

    public void run() {
        System.out.println("Welcome to 240 chess. Type Help to get started.");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_BLINKING + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
//        System.out.print("filler for reset");
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (InvalidRequestException e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws InvalidRequestException {
        return "test";
    }

    public String login(String... params) throws InvalidRequestException {
        return "test";
    }

    public String help() {
//        if (state == State.SIGNEDOUT) {
//            return """
//                    - signIn <yourname>
//                    - quit
//                    """;
//        }
        return """
                - signIn <yourname>
                - quit
                """;
    }
}
