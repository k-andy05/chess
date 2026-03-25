package client;

import model.GameData;
import request.*;
import result.*;

import java.util.ArrayList;
import java.util.Arrays;

public class PostLoginClient implements ClientState {
    private final ServerFacade server;

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
                case "play" -> {}
                case "observe" -> {}
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
        return null;
    }
    public String logout(String... params) throws InvalidRequestException {
        if (params.length == 0) {
            try {
//                System.out.println("This is the auth token being passed into new LogoutRequest: " + server.authToken);
                server.logout(new LogoutRequest(server.authToken));
                return "LOGOUT_SUCCESS";
            } catch (Exception e) {
                throw new InvalidRequestException(401, e.getMessage());
            }
        }
        throw new InvalidRequestException(401, "Error: Wrong logout parameters");
    }

    public String create(String... params) throws InvalidRequestException {
        if (params.length == 1) {
            String body = String.format(
                    "{\"gameName\":\"%s\"}",
                    params[0]
            );
            try {
//                System.out.println("game name inputted by user: " + params[0]);
//                System.out.println("Making sure authtoken is set before game creation..." + server.authToken);
                CreateResult createResult = server.create(new CreateRequest(body, server.authToken));
//                System.out.println("This is gameID assigned..." + createResult.gameID);
                return "GAME_CREATED; GameID=" + createResult.gameID;
            } catch (Exception e) {
                throw new InvalidRequestException(401, e.getMessage());
            }
        }
        throw new InvalidRequestException(401, "Error: game not created");
    }

    public String list(String... params) throws InvalidRequestException {
        if (params.length == 0) {
            try {
                ListResult listResult = server.list(new ListRequest(server.authToken));
                System.out.println("ListResult" + listResult);
                System.out.println("ListResult gamelist: " + listResult.gameList);
                ArrayList<GameData> games = listResult.gameList;
                System.out.println("These are the gameData objects currently: " + games);
                if (games == null || games.isEmpty()) {return "No games currently exist";}
                StringBuilder formattedGameList = new StringBuilder();
                for (int i = 0; i < games.size(); i++) {
                    GameData game = games.get(i);
                    formattedGameList.append(i + 1)
                            .append(". ")
                            .append(game.gameName)
                            .append(" | White: ")
                            .append(game.whiteUsername != null ? game.whiteUsername : "n/a")
                            .append(" | Black: ")
                            .append(game.blackUsername != null ? game.blackUsername : "n/a")
                            .append(")\n");
                }
                return formattedGameList.toString().trim();
            } catch (Exception e) {
                throw new InvalidRequestException(401, e.getMessage());
            }
        }
        throw new InvalidRequestException(401, "Error: Gamelist not queried for correctly");
    }

//    public String play(String... params) throws InvalidRequestException {}

//    public String observe(String... params) throws InvalidRequestException {} // TODO

    @Override
    public String help() {
        return """
                create <NAME> - create game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }
}
