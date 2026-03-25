package client;

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
                case "logout" -> {}
                case "create" -> {}
                case "list" -> {}
                case "play" -> {}
                case "observe" -> {}
                default -> {
                    return help();
                }
            }
        } catch (Exception e) {
            throw new InvalidRequestException(401, e.getMessage());
        }
        return null;
    }

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
