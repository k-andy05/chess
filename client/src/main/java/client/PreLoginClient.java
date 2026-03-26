package client;

import java.util.Arrays;

import request.*;


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

    public String register(String... params) throws InvalidRequestException {
        if (params.length == 3) {
            String body = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"}",
                    params[0], params[1], params[2]
            );
            try {
                server.register(new RegisterRequest(body));
//                String loginBody = String.format(
//                        "{\"username\":\"%s\",\"password\":\"%s\"}",
//                        params[0], params[1]
//                );
//                server.login(new LoginRequest(loginBody));
                System.out.print("Successfully registered!\n");
                return "LOGIN_SUCCESS";
            } catch (Exception e) {
                String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                if (errorMsg.contains("taken")) {
                    throw new InvalidRequestException(403, "Registration failed: Username or email already taken");
                } else if (errorMsg.contains("bad request") || errorMsg.contains("empty")) {
                    throw new InvalidRequestException(400, "Registration failed: Please provide valid inputs");
                }
                throw new InvalidRequestException(500, "Registration failed");
            }
        }
        throw new InvalidRequestException(400, "Error: Invalid number of register inputs");
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
                return "LOGIN_SUCCESS";
            } catch (Exception e) {
                String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                if (errorMsg.contains("unauthorized")) {
                    throw new InvalidRequestException(401, "Login failed: Incorrect username or password");
                }
                throw new InvalidRequestException(500, "Login failed");
            }
        }
        throw new InvalidRequestException(400, "Error: Invalid number of inputs for login");
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
