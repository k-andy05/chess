package client;

import java.util.Arrays;
import java.util.Scanner;

import request.*;
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
        System.out.println("\n" + EscapeSequences.SET_TEXT_COLOR_BLUE + help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
//                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_BLINKING + EscapeSequences.SET_TEXT_COLOR_WHITE + "[LOGGED_OUT] >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
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
        if (params.length == 3) {
//            String username = params[0];
//            String password = params[1];
//            String email = params[2];
            String body = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"}",
                    params[0], params[1], params[2]
            );
            try {
                RegisterRequest request = new RegisterRequest(body);
                server.register(request);
                return "Successfully registered, you are now able to login" + "\n";
            } catch (Exception e) {
                throw new InvalidRequestException(401, e.getMessage());
            }
        }
        throw new InvalidRequestException(401, "Error: Invalid register inputs");
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
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }
}
