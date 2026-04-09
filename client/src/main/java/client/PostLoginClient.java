package client;

import model.GameData;
import request.*;
import result.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostLoginClient implements ClientState {
    private final ServerFacade server;
    private List<GameData> cachedGames = new ArrayList<>();

    public PostLoginClient(ServerFacade server) {
        this.server = server;
    }

    @Override
    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            switch (cmd) {
                case "logout" -> {
                    return logout(params);
                }
                case "create" -> {
                    return create(params);
                }
                case "list" -> {
                    return list(params);
                }
                case "join" -> {
                    return join(params);
                }
                case "observe" -> {
                    return observe(params);
                }
                case "quit" -> {
                    return quit();
                }
                default -> {
                    return help();
                }
            }
        } catch (Exception e) {
            throw new InvalidRequestException(401, e.getMessage());
        }
    }

    public String logout(String... params) throws InvalidRequestException {
        if (params.length == 0) {
            try {
                server.logout(new LogoutRequest(server.authToken));
                server.authToken = "";
                return "LOGOUT_SUCCESS";
            } catch (Exception e) {
                throw new InvalidRequestException(500, "Logout failed: " + e.getMessage());
            }
        }
        throw new InvalidRequestException(401, "Error: Wrong number of logout inputs");
    }

    public String create(String... params) throws InvalidRequestException {
        if (params.length == 1) {
            String body = String.format(
                    "{\"gameName\":\"%s\"}", params[0]);
            try {
                CreateResult createResult = server.create(new CreateRequest(body, server.authToken));
                return String.format("Game '%s' successfully created.", params[0]);
            } catch (Exception e) {
                String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                if (errorMsg.contains("bad request") || errorMsg.contains("taken")) {
                    throw new InvalidRequestException(400, "Create failed: That game name is invalid or taken");
                }
                throw new InvalidRequestException(500, "Create failed: " + e.getMessage());
            }
        }
        throw new InvalidRequestException(401, "Error: Number of inputs for create is incorrect");
    }

    public String list(String... params) throws InvalidRequestException {
        if (params.length == 0) {
            try {
                ListResult listResult = server.list(new ListRequest(server.authToken));
                cachedGames = listResult.gameList != null ? listResult.gameList : new ArrayList<>();
                if (cachedGames.isEmpty()) {
                    return "No games currently exist";
                }
                StringBuilder formattedGameList = new StringBuilder();
                for (int i = 0; i < cachedGames.size(); i++) {
                    GameData game = cachedGames.get(i);
                    formattedGameList.append(i + 1)
                            .append(". ")
                            .append(game.gameName)
                            .append(" | White: ")
                            .append(game.whiteUsername != null ? game.whiteUsername : "n/a")
                            .append(" | Black: ")
                            .append(game.blackUsername != null ? game.blackUsername : "n/a")
                            .append("\n");
                }
                return formattedGameList.toString().trim();
            } catch (Exception e) {
                throw new InvalidRequestException(500, "Failed to retrieved games: " + e.getMessage());
            }
        }
        throw new InvalidRequestException(401, "Error: Incorrect number of inputs for list command");
    }

    public String join(String... params) throws InvalidRequestException {
        try {
            cachedGames = server.list(new ListRequest(server.authToken)).gameList;
        } catch (Exception e) {
            throw new InvalidRequestException(500, "Failed to retrieve list of possible games to join" + e.getMessage());
        }
        if (params.length == 2) {
            try {
                int gameIndex = Integer.parseInt(params[0]) - 1;
                String playerColor = params[1].toUpperCase();

                if (gameIndex < 0 || gameIndex >= cachedGames.size()) {
                    throw new InvalidRequestException(400, "Join failed: Invalid game number. Type 'list' to see possible games");
                }
                if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
                    throw new InvalidRequestException(400, "Join failed: Team color must be WHITE or BLACK");
                }

                int actualGameID = cachedGames.get(gameIndex).gameID;

                String body = String.format(
                        "{\"playerColor\":\"%s\",\"gameID\":%d}",
                        playerColor, actualGameID
                );

                server.join(new JoinRequest(server.authToken, body));
                return "JOIN_SUCCESS";
            } catch (NumberFormatException e) {
                throw new InvalidRequestException(400, "Join failed: Game number must be type int not string");
            } catch (Exception e) {
                String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                if (errorMsg.contains("already taken") || errorMsg.contains("403")) {
                    throw new InvalidRequestException(403, "Join failed: That team color is already taken");
                }
                throw new InvalidRequestException(500, "Join failed: " + e.getMessage());
            }
        }
        throw new InvalidRequestException(400, "Error: Incorrect number of inputs for join command");
    }

    public String observe(String... params) throws InvalidRequestException {
        try {
            cachedGames = server.list(new ListRequest(server.authToken)).gameList;
        } catch (Exception e) {
            throw new InvalidRequestException(500, "Failed to retrieve list of possible games to join" + e.getMessage());
        }
        if (params.length == 1) {
            try {
                int gameIndex = Integer.parseInt(params[0]) -1;

                if (gameIndex < 0 || gameIndex >= cachedGames.size()) {
                    throw new InvalidRequestException(400, "Observe failed: Invalid game number. Type 'list' to see possible games");
                }
                int gameID = cachedGames.get(gameIndex).gameID;
                server.gameID = gameID;
                server.playerColor = null;
                return "JOIN_SUCCESS";
            } catch (NumberFormatException e) {
                throw new InvalidRequestException(400, "Observe failed: Game number must be an integer");
            } catch (Exception e) {
                throw new InvalidRequestException(500, "Observe failed: " + e.getMessage());
            }
        }
        throw new InvalidRequestException(400, "Error: Incorrect number of inputs for observe command");
    }

    public String quit() {
        try {
            server.logout(new LogoutRequest(server.authToken));
        } catch (Exception ignored) {}
        return "quit";
    }

    @Override
    public String help() {
        return """
                create <NAME> - create game
                list - games
                join <NUMBER> [WHITE|BLACK] - a game
                observe <NUMBER> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }
}
