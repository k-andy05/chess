package client;

import java.util.Arrays;
import java.util.Scanner;

import request.*;
import result.LoginResult;
import ui.EscapeSequences;


public class PreLoginClient implements ClientState {
    private final ServerFacade server;

    public PreLoginClient(ServerFacade server) {
        this.server = server;
    }

    @Override
    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            switch (cmd) {
                case "register" -> {
                    return register(params);
                }
                case "login" -> {
                    return login(params);
                }
                case "quit" -> {
                    return "quit";
                }
                default -> {
                    return help();
                }
            }
        } catch (Exception e) {
            throw new InvalidRequestException(401, e.getMessage());
        }
    }

//    public void run() {
//        System.out.println("Welcome to 240 chess. Type Help to get started.");
//        System.out.println("\n" + EscapeSequences.SET_TEXT_COLOR_BLUE + help());
//
//        Scanner scanner = new Scanner(System.in);
//        var result = "";
//        while (!result.equals("quit")) {
//            printPrompt();
//            String line = scanner.nextLine();
//
//            try {
//                result = eval(line);
//                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
//            } catch (Throwable e) {
//                var msg = e.toString();
//                System.out.print(msg);
//            }
//        }
//        System.out.println();
//    }

//    private void printPrompt() {
//        System.out.print("\n" + EscapeSequences.RESET_TEXT_BLINKING + EscapeSequences.SET_TEXT_COLOR_WHITE + "[LOGGED_OUT] >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
////        System.out.print("filler for reset");
//    }

    public String register(String... params) throws InvalidRequestException {
        if (params.length == 3) {
            String body = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"}",
                    params[0], params[1], params[2]
            );
            try {
                server.register(new RegisterRequest(body));
//                server.login(new LoginRequest()); //TODO make sure you are logged in after registering
                return "Successfully registered, you are now able to login" + "\n";
            } catch (Exception e) {
                throw new InvalidRequestException(401, e.getMessage());
            }
        }
        throw new InvalidRequestException(401, "Error: Invalid register inputs");
    }

    public String login(String... params) throws InvalidRequestException {
        if (params.length == 2) {
            String body = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\"}",
                    params[0], params[1]
            );
            try {
                System.out.println("Logging in with username: " + params[0] + " and password: " + params[1]);
                server.login(new LoginRequest(body));
//                LoginResult loginResult = server.login(new LoginRequest(body));
//                String authToken = loginResult.authToken;
                return "LOGIN_SUCCESS";
            } catch (Exception e) {
                throw new InvalidRequestException(401, e.getMessage());
            }
        }
        throw new InvalidRequestException(401, "Error: Invalid login inputs");
    }

    @Override
    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }
}
